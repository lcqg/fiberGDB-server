package com.xiongdwm.fiberGDB.support.orm.entity;


import com.xiongdwm.fiberGDB.support.orm.entity.iInterfaces.DynamicPrinciple;

public class NodePrinciple extends DynamicPrinciple {

    @Override
    public String getTableName() {
        return null;
    }

    public String[] getPropertyNames() {
        return new String[0];
    }

    @Override
    public String[] getTablesName() {
        throw new UnsupportedOperationException("node example not support getTablesName");
    }

    @Override
    public String[] getPropertyNamesForEdge() {
        return new String[0];
    }

    @Override
    public String[] getPrincipalArray() {
        return new String[0];
    }
}
