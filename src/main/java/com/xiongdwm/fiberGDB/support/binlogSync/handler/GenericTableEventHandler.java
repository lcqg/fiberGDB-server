package com.xiongdwm.fiberGDB.support.binlogSync.handler;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.xiongdwm.fiberGDB.bo.fiberRDB.FiberEntityRDB;
import com.xiongdwm.fiberGDB.bo.fiberRDB.RoutePointEntityRDB;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import com.xiongdwm.fiberGDB.repository.RoutePointRepo;
import com.xiongdwm.fiberGDB.resources.RoutePointResources;
import com.xiongdwm.fiberGDB.resources.impl.RoutePointResourcesImpl;
import com.xiongdwm.fiberGDB.support.FacilityStage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class GenericTableEventHandler<T> implements TableEventHandler<T> {

    @Autowired
    private RoutePointResources pointResources;

    public final Class<T> entityClass;
    public final Map<String, Integer> columnIndexMap;

    public GenericTableEventHandler(Class<T> entityClass, Map<String, Integer> columnIndexMap) {
        this.entityClass=entityClass;
        this.columnIndexMap=columnIndexMap;
    }

    @Override
    public void handleInsertEvent(T entity) {
        if (entity instanceof RoutePointEntityRDB dto) {
            RoutePoint routePoint=new RoutePoint();
            BeanUtils.copyProperties(dto,routePoint);
            pointResources.save(routePoint);
        }else if(entity instanceof FiberEntityRDB dto){
            Fiber fiber=new Fiber();
            BeanUtils.copyProperties(dto,fiber);
            fiber.setStage(dto.getExists().getText());
            double weight=dto.getExists().getCode()<0?99d: FacilityStage.getHalf().contains(dto.getExists())?0.5d:1d;
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
        for (Serializable[] row : data.getRows()) {
            T entity = convertRowToEntity(row);
            System.out.println(entity);
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
                Field field = entityClass.getDeclaredField(fieldName);
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
