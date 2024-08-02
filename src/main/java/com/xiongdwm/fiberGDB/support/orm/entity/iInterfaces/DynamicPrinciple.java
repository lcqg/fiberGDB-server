package com.xiongdwm.fiberGDB.support.orm.entity.iInterfaces;

import com.xiongdwm.fiberGDB.support.orm.helper.exception.Neo4jPrincipleException;
import org.springframework.data.neo4j.core.schema.Id;

import java.lang.reflect.Field;

public abstract class DynamicPrinciple {
    public abstract String getTableName();
    public abstract String[] getTablesName();
    public abstract String[] getPropertyNamesForEdge();
    public abstract String[] getPrincipalArray();
    public boolean nodeEvolved=false;

    protected String getPrimaryKeyName(Class<?> clazz){
        Field[]fields= clazz.getDeclaredFields();
        for(Field field:fields){
            if(field.isAnnotationPresent(Id.class)){
                return field.getName();
            }
        }
        throw new Neo4jPrincipleException("ENTITY","no primary key found in class `"+clazz.getSimpleName()+"`!!");
    }

    protected boolean isPropertiesNotInEntity(Class<?>clazz, String propertyName) {
        assert propertyName!=null;
        try {
            clazz.getDeclaredField(propertyName);
            return false;
        } catch (NoSuchFieldException e) {
           return true;
        }
    }

    public boolean isNodeEvolved() {
        return nodeEvolved;
    }

    public void setNodeEvolved(boolean nodeEvolved) {
        this.nodeEvolved = nodeEvolved;
    }
}
