package com.xiongdwm.fiberGDB.bo.requestEntity;

public record SearchRouteParam(Long startId, Long endId, double weight) {
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
        return weight==0.0?4.0d:weight;
    }
}
