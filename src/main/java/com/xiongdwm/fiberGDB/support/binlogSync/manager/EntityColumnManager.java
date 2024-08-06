package com.xiongdwm.fiberGDB.support.binlogSync.manager;

import com.xiongdwm.fiberGDB.bo.fiberRDB.FiberEntityRDB;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EntityColumnManager {

    private final Map<Class<?>, Map<String, Integer>> entityColumnMap = new HashMap<>();

    public void registerEntityColumns(Class<?> entityClass, Map<String, Integer> columns) {
        entityColumnMap.put(entityClass, columns);
    }

    public Map<String, Integer> getEntityColumns(Class<?> entityClass) {
        return entityColumnMap.get(entityClass);
    }

    public void initializeEntityColumns() {
        registerEntityColumns(FiberEntityRDB.class, generateFiberColumnMap()); //fiber
    }

    private Map<String, Integer> generateFiberColumnMap() {
        Map<String, Integer> fiberColumnIndexMap = new HashMap<>();
        fiberColumnIndexMap.put("id", 0);
        fiberColumnIndexMap.put("name", 1);
        return fiberColumnIndexMap;
    }
}
