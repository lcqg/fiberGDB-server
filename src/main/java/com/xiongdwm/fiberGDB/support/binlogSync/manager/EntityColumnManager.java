package com.xiongdwm.fiberGDB.support.binlogSync.manager;

import com.xiongdwm.fiberGDB.bo.fiberRDB.FiberEntityRDB;
import com.xiongdwm.fiberGDB.bo.fiberRDB.RoutePointEntityRDB;
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
        registerEntityColumns(RoutePointEntityRDB.class, generateRoutePointMap()); //routePoint
    }

    private Map<String, Integer> generateFiberColumnMap() {
        Map<String, Integer> fiberColumnIndexMap = new HashMap<>();
        fiberColumnIndexMap.put("id", 0);
        fiberColumnIndexMap.put("name", 1);
        fiberColumnIndexMap.put("from_station_id",24);
        fiberColumnIndexMap.put("to_station_id",27);
        fiberColumnIndexMap.put("exists",36);
        return fiberColumnIndexMap;
    }
    private Map<String,Integer>generateRoutePointMap(){
        Map<String,Integer> routePointColumnIndexMap=new HashMap<>();
        routePointColumnIndexMap.put("id",0);
        routePointColumnIndexMap.put("name",1);
        routePointColumnIndexMap.put("lat",2);
        routePointColumnIndexMap.put("lng",3);
        routePointColumnIndexMap.put("address",4);
        routePointColumnIndexMap.put("type",6);
        routePointColumnIndexMap.put("gps84",7);
        routePointColumnIndexMap.put("icon",8);
        routePointColumnIndexMap.put("area",15);
        routePointColumnIndexMap.put("level",21);
        routePointColumnIndexMap.put("exist",46);
        return routePointColumnIndexMap;
    }
}
