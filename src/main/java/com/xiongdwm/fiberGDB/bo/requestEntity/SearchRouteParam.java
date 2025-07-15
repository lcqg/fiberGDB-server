package com.xiongdwm.fiberGDB.bo.requestEntity;

import io.micrometer.common.lang.Nullable;

public record SearchRouteParam(
        @Nullable Long startId, // 起点 ID
        @Nullable Long endId, // 终点 ID
        @Nullable String fromStation, // 起点站名
        @Nullable String toStation, // 终点站名
        double weight, // 总权重
        @Nullable int routeCount, // 路径数量
        @Nullable double maxDistance, // 最大距离
        @Nullable String nodesAbandon, // 跳过节点 id 逗号分隔
        @Nullable String pointUsed, // 节点状态 对应exist字段 多个 逗号分隔
        @Nullable String fiberUsed, // 关系类型 对应stage字段 多个 逗号分隔
        @Nullable String siteType // 站点类型 对应level字段 多个 逗号分隔
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
