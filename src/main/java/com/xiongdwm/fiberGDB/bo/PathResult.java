package com.xiongdwm.fiberGDB.bo;


import java.util.List;

import io.micrometer.common.lang.Nullable;


public record PathResult(List<RoutePointDTOProjection> routes, boolean isAllReused, double buildDistance)  {
    @Override
    public List<RoutePointDTOProjection> routes() {
        return routes;
    }

    @Override
    public boolean isAllReused() {
        return isAllReused;
    }

    @Override
    public double buildDistance() {
        return buildDistance;
    }
} 
