package com.xiongdwm.fiberGDB.bo.requestEntity;

import io.micrometer.common.lang.Nullable;

public record SearchRouteParam(
        @Nullable Long startId,
        @Nullable Long endId,
        @Nullable String fromStation,
        @Nullable String toStation,
        double weight,
        @Nullable int routeCount,
        @Nullable double maxDistance,
        @Nullable String type,
        @Nullable String resourceType) {

        @Override
        public Long startId() {
            return startId;
        }

        @Override
        public Long endId() {
            return endId;
        }

        @Override
        public String fromStation() {
            return fromStation;
        }

        @Override
        public String toStation() {
            return toStation;
        }

        @Override
        @Nullable
        public String type() {
            return type;
        }

        @Override
        @Nullable
        public String resourceType() {
            return resourceType;
        }

        @Override
        public double weight() {
            return weight==0.0?5.0d:weight;
        }
        @Override
        public int routeCount() {
            return routeCount==0?5:routeCount;
        }

        @Override
        public double maxDistance() {
            return maxDistance==0.0?100.0d:maxDistance;
        }

}
