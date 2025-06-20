package com.xiongdwm.fiberGDB.bo.requestEntity;

public record SearchRouteParam(Long startId, Long endId, double weight, int routeCount) {
    @Override
    public Long startId() {
        return startId;
    }

    @Override
    public Long endId() {
        return endId;
    }

    @Override
    public double weight() {
        return weight==0.0?5.0d:weight;
    }
    @Override
    public int routeCount() {
        return routeCount==0?5:routeCount;
    }
}
