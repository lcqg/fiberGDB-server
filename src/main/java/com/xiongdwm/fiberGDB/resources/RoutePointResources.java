package com.xiongdwm.fiberGDB.resources;

import com.xiongdwm.fiberGDB.bo.PathResult;
import com.xiongdwm.fiberGDB.bo.RoutePointDTOProjection;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.Fiber;

import com.xiongdwm.fiberGDB.support.orm.helper.AbstractCypherHelper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

public interface RoutePointResources {
    Mono<Void> createFiber(Long fromId,Long toId,Fiber fiber);
    List<PathResult> retrieve(Long startId, Long endId, double weightLimit, int routeCounts, double maxDistance);
    Long save(RoutePoint point);
    void createFiberNoneReactive(Long fromId,Long toId,Fiber fiber, AbstractCypherHelper.OperationType operationType);
    Flux<PathResult> retrieveFlux(Long startId, Long endId, double weightLimit, int routeCounts);
    RoutePoint findRoutePointByName(String name);
}
