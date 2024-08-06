package com.xiongdwm.fiberGDB.support.binlogSync.handler;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.xiongdwm.fiberGDB.bo.fiberRDB.FiberEntityRDB;
import com.xiongdwm.fiberGDB.bo.fiberRDB.RoutePointEntityRDB;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import com.xiongdwm.fiberGDB.resources.RoutePointResources;
import com.xiongdwm.fiberGDB.support.BeanContext;
import com.xiongdwm.fiberGDB.support.FacilityStage;
import com.xiongdwm.fiberGDB.support.binlogSync.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;


public record GenericTableEventHandler<T>(Class<T> entityClass,
                                          Map<String, Integer> columnIndexMap) implements TableEventHandler<T> {


    @Override
    public void handleInsertEvent(T entity) {
        ApplicationContext context= BeanContext.getApplicationContext();
        RoutePointResources pointResources=context.getBean(RoutePointResources.class);
        if (entity instanceof RoutePointEntityRDB dto) {
            RoutePoint routePoint = new RoutePoint();
            BeanUtils.copyProperties(dto, routePoint);
            routePoint.setType(RoutePoint.RoutePointType.valueOf(dto.getType()));
            routePoint.setExist(FacilityStage.valueOf(dto.getExist()));
            pointResources.save(routePoint);
        } else if (entity instanceof FiberEntityRDB dto) {
            Fiber fiber = new Fiber();
            BeanUtils.copyProperties(dto, fiber);
            FacilityStage existsEnum=FacilityStage.valueOf(dto.getExists());
            fiber.setStage(existsEnum.getText());
            double weight = existsEnum.getCode() < 0 ? 99d : FacilityStage.getHalf().contains(existsEnum) ? 0.5d : 1d;
            fiber.setWeight(weight);
            pointResources.createFiberNoneReactive(dto.getFromStationId(), dto.getToStationId(), fiber);
        }
    }

    @Override
    public void handleUpdateEvent(T entity) {
        // neo4j update
    }

    @Override
    public void handleDeleteEvent(T entity) {
        // neo4j delete
    }

    public void handleInsertEvent(WriteRowsEventData data) {
//        System.out.println("insert!!");
        for (Serializable[] row : data.getRows()) {
//            System.out.println(Arrays.toString(row));
            T entity = convertRowToEntity(row);
//            System.out.println(entity);
            handleInsertEvent(entity);
        }
    }

    public void handleUpdateEvent(UpdateRowsEventData data) {
        for (Map.Entry<Serializable[], Serializable[]> entry : data.getRows()) {
            T entity = convertRowToEntity(entry.getValue());
            handleUpdateEvent(entity);
        }
    }

    public void handleDeleteEvent(DeleteRowsEventData data) {
        for (Serializable[] row : data.getRows()) {
            T entity = convertRowToEntity(row);
            handleDeleteEvent(entity);
        }
    }

    private T convertRowToEntity(Serializable[] row) {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Integer> entry : columnIndexMap.entrySet()) {
                String fieldName = entry.getKey();
                int index = entry.getValue();
                String snake2camel= StringUtils.snakeToLowerCamelCase(fieldName);
                Field field = entityClass.getDeclaredField(snake2camel);
                field.setAccessible(true);
                field.set(entity, row[index]);
            }

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert row to entity", e);
        }
    }

    @Override
    public String toString() {
        return "GenericTableEventHandler{" +
                "entityClass=" + entityClass +
                ", columnIndexMap=" + columnIndexMap +
                '}';
    }
}
