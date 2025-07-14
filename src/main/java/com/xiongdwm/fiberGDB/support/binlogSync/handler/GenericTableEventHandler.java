package com.xiongdwm.fiberGDB.support.binlogSync.handler;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.xiongdwm.fiberGDB.bo.fiberRDB.FiberEntityRDB;
import com.xiongdwm.fiberGDB.bo.fiberRDB.RoutePointEntityRDB;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import com.xiongdwm.fiberGDB.entities.relationship.FiberConclusion;
import com.xiongdwm.fiberGDB.resources.RoutePointResources;
import com.xiongdwm.fiberGDB.support.BeanContext;
import com.xiongdwm.fiberGDB.support.FacilityStage;
import com.xiongdwm.fiberGDB.support.binlogSync.StringUtils;
import com.xiongdwm.fiberGDB.support.orm.helper.AbstractCypherHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public record GenericTableEventHandler<T>(
        Class<T> entityClass,
        Map<String, Integer> columnIndexMap
) implements TableEventHandler<T> {

    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new HashMap<>();

    @SuppressWarnings("unchecked")
    private <R> R convertToNeo4jEntity(T entity) {
        if (entity instanceof RoutePointEntityRDB dto) {
            RoutePoint routePoint = new RoutePoint();
            BeanUtils.copyProperties(dto, routePoint);
            routePoint.setType(RoutePoint.RoutePointType.valueOf(dto.getType()));
            routePoint.setExist(FacilityStage.valueOf(dto.getExist()));
            if("STATION".equals(dto.getType())) {
                routePoint.setLevel(dto.getSiteType());
            }
            return (R) routePoint;
        } else if (entity instanceof FiberEntityRDB dto) {
            Fiber fiber = new Fiber();
            BeanUtils.copyProperties(dto, fiber);
            FacilityStage existsEnum = FacilityStage.valueOf(dto.getExists());
            fiber.setStage(existsEnum.name());
            double weight = existsEnum.getCode() < 0 ? 99d
                    : FacilityStage.getHalf().contains(existsEnum) ? 0.5d : 1d;
            fiber.setWeight(weight);
            return (R) fiber;
        }
        throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass());
    }

    private void saveOrUpdateEntity(T entity, AbstractCypherHelper.OperationType opType) {
        System.out.println(entity);
        ApplicationContext context = BeanContext.getApplicationContext();
        RoutePointResources pointResources = context.getBean(RoutePointResources.class);

        if (entity instanceof RoutePointEntityRDB) {
            pointResources.save(convertToNeo4jEntity(entity));
        } else if (entity instanceof FiberEntityRDB dto) {
            pointResources.createFiberNoneReactive(dto.getFromStationId(), dto.getToStationId(),
                    convertToNeo4jEntity(entity), opType);
                    
            pointResources.createFiberNoneReactive(dto.getFromStationId(), dto.getToStationId(), fiber);
            RoutePoint point=pointResources.getRoutePointById(dto.getFromStationId());
            if(point!=null){
                FiberConclusion conclusion = pointResources.getFiberConclusionBetweenPoints(dto.getFromStationId(), dto.getToStationId());
                Set<FiberConclusion> conclusions = point.getConclusions();
                if(null ==conclusion){
                    conclusion = new FiberConclusion();
                    conclusion.setContext(dto.getName());
                    conclusion.setWeight(weight);
                    conclusion.setTypeSet(dto.getExists());
                    conclusion.setMaxDis(dto.getDis());
                    conclusion.setMinDis(dto.getDis());
                    conclusion.setTowards(pointResources.getRoutePointById(dto.getToStationId()));
                    if (conclusions == null) {
                        conclusions = Set.of(conclusion);
                        point.setConclusions(conclusions);
                    } else {
                        conclusions.add(conclusion);
                    }
                    pointResources.save(point);
                } else {
                    conclusions.removeIf(c -> c.getTowards().getId().equals(dto.getToStationId()));
                    var oldContext = conclusion.getContext();
                    conclusion.setContext(oldContext + "," + dto.getName());
                    var oldWeight = conclusion.getWeight();
                    conclusion.setWeight(Math.min(oldWeight, weight));
                    conclusion.setTypeSet(conclusion.getTypeSet() + "," + dto.getExists());
                    conclusion.setMaxDis(Math.max(conclusion.getMaxDis(), dto.getDis()));
                    conclusion.setMinDis(Math.min(conclusion.getMinDis(), dto.getDis()));
                    pointResources.save(point);
                }
            }

        }
    }

    @Override
    public void handleInsertEvent(T entity) {
        saveOrUpdateEntity(entity, AbstractCypherHelper.OperationType.CREATE);
    }

    @Override
    public void handleUpdateEvent(T entity) {
        saveOrUpdateEntity(entity, AbstractCypherHelper.OperationType.UPDATE);
    }

    @Override
    public void handleDeleteEvent(T entity) {
        // Neo4j delete logic 可按 entity 类型区分处理
        System.out.println("Delete not implemented yet for: " + entity);
    }

    public void handleInsertEvent(WriteRowsEventData data) {
        for (Serializable[] row : data.getRows()) {
            try {
                T entity = convertRowToEntity(row);
                handleInsertEvent(entity);
            } catch (Exception e) {
                System.err.println("Insert row failed: " + e.getMessage());
            }
        }
    }

    public void handleUpdateEvent(UpdateRowsEventData data) {
        for (Map.Entry<Serializable[], Serializable[]> entry : data.getRows()) {
            try {
                T entity = convertRowToEntity(entry.getValue());
                handleUpdateEvent(entity);
            } catch (Exception e) {
                System.err.println("Update row failed: " + e.getMessage());
            }
        }
    }

    public void handleDeleteEvent(DeleteRowsEventData data) {
        for (Serializable[] row : data.getRows()) {
            try {
                T entity = convertRowToEntity(row);
                handleDeleteEvent(entity);
            } catch (Exception e) {
                System.err.println("Delete row failed: " + e.getMessage());
            }
        }
    }

    private T convertRowToEntity(Serializable[] row) {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            Map<String, Field> fieldMap = getFieldMap(entityClass);

            for (Map.Entry<String, Integer> entry : columnIndexMap.entrySet()) {
                String fieldName = StringUtils.snakeToLowerCamelCase(entry.getKey());
                int index = entry.getValue();
                Field field = fieldMap.get(fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    field.set(entity, row[index]);
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert row to entity: " + entityClass.getSimpleName(), e);
        }
    }

    private Map<String, Field> getFieldMap(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, key -> {
            Map<String, Field> map = new HashMap<>();
            for (Field field : key.getDeclaredFields()) {
                map.put(field.getName(), field);
            }
            return map;
        });
    }

    @Override
    public String toString() {
        return "GenericTableEventHandler{" +
                "entityClass=" + entityClass +
                ", columnIndexMap=" + columnIndexMap +
                '}';
    }
}
