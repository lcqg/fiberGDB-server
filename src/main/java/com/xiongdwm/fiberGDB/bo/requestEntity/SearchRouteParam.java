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
        @Nullable String nodesAbandon,
        @Nullable String pointUsed,
        @Nullable String fiberUsed,
        @Nullable String siteType
        ) {

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
        public String nodesAbandon() {
            return nodesAbandon;
        }

        @Override
        @Nullable
        public String pointUsed() {
            return pointUsed;
        }

        @Override
        @Nullable
        public String fiberUsed() {
            return fiberUsed;
        }

        @Override
        @Nullable
        public String siteType() {
            return siteType;
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
