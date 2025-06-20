package com.xiongdwm.fiberGDB.resources;

import com.xiongdwm.fiberGDB.bo.RoutePointDTOProjection;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import reactor.core.publisher.Mono;


import java.util.List;

public interface RoutePointResources {
    Mono<Void> createFiber(Long fromId,Long toId,Fiber fiber);
    List<List<RoutePointDTOProjection>> retrieve(Long startId, Long endId, double weightLimit, int routeCounts);
    Long save(RoutePoint point);
    void createFiberNoneReactive(Long fromId,Long toId,Fiber fiber);
}
