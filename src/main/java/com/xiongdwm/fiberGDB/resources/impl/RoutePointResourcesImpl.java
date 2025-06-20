package com.xiongdwm.fiberGDB.resources.impl;

import com.xiongdwm.fiberGDB.bo.RoutePointDTOProjection;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import com.xiongdwm.fiberGDB.repository.RoutePointRepo;
import com.xiongdwm.fiberGDB.resources.RoutePointResources;
import com.xiongdwm.fiberGDB.support.orm.helper.AbstractCypherHelper;
import com.xiongdwm.fiberGDB.support.orm.helper.provider.CypherHelper;
import jakarta.annotation.Resource;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class RoutePointResourcesImpl implements RoutePointResources {
    @Resource
    private RoutePointRepo pointRepo;
    @Resource
    private Neo4jClient neo4jClient;

    @Override
    public Mono<Void> createFiber(Long fromId, Long toId, Fiber fiber) {
        Mono<RoutePoint> fromPoint = pointRepo.findById(fromId);
        Mono<RoutePoint> toPoint = pointRepo.findById(toId);

        return Mono.zip(fromPoint, toPoint).flatMap(t -> {
            RoutePoint from = t.getT1();
            RoutePoint to = t.getT2();
            fiber.setTowards(to);
            from.addCable(fiber);

            return pointRepo.save(from)
                    .then()
                    .onErrorResume(e -> {
                        System.err.println("Error saving point: " + e.getMessage());
                        return Mono.empty();
                    });
        }).onErrorResume(e -> {
            System.err.println("Error creating fiber: " + e.getMessage());
            return Mono.empty();
        });
    }

    @Override
    public List<List<RoutePointDTOProjection>> retrieve(Long startId, Long endId, double weightLimit, int routeCounts) {
        List<RoutePointDTOProjection> queryResult = pointRepo
                .findRoutesByCypher(startId, endId, weightLimit, routeCounts)
                .collectList()
                .blockOptional().orElse(Collections.emptyList());
        System.out.println(queryResult.size());
        LinkedList<List<RoutePointDTOProjection>> result = new LinkedList<>();
        if(!queryResult.isEmpty()){
            List<RoutePointDTOProjection> partial = new ArrayList<>();
            for (RoutePointDTOProjection node : queryResult) {
                partial.add(node);
                if (node.getId().longValue() == endId.longValue()) {
                    result.addLast((new ArrayList<>(partial)));
                    partial.clear();
                }
            }
        }
        result.forEach(it->{
            System.out.println(it);
            System.out.println("-----------------");
        });
        // 通过endId分割成多条路径
        if (queryResult.size() < routeCounts) {
            // 查找附近的点
            List<RoutePointDTOProjection> nearbyPoints = pointRepo
                    .findRoutPointInDistanceWithNoConnection(startId, 500.0)
                    .collectList().blockOptional().orElse(Collections.emptyList());
            if (nearbyPoints.isEmpty())
                return Collections.emptyList();
            var remain = routeCounts - queryResult.size();
            for (RoutePointDTOProjection nearby : nearbyPoints) {
                if (remain <= 0)
                    break;
                if (nearby.getId().longValue() == endId.longValue())
                    continue;
                List<RoutePointDTOProjection> list = pointRepo.bfsFlux(startId, nearby.getId(), weightLimit)
                        .collectList().blockOptional().orElse(Collections.emptyList());
                if (list.isEmpty())
                    continue;
                result.addLast(list);
                remain--;
            }

        }
        return result;
    }

    @Override
    public Long save(RoutePoint point) {
        return pointRepo.save(point).blockOptional().orElseThrow().getId();
    }

    @Override
    public void createFiberNoneReactive(Long fromId, Long toId, Fiber fiber) {
        RoutePoint fromPoint = pointRepo.findById(fromId).blockOptional().orElse(null);
        RoutePoint toPoint = pointRepo.findById(toId).blockOptional().orElse(null);
        if (fromPoint == null || toPoint == null)
            return;
        CypherHelper<Fiber> fiberRepo = new CypherHelper<Fiber>(neo4jClient);
        fiberRepo.createRelationship(fromPoint, toPoint, fiber, AbstractCypherHelper.RelationshipType.DIRECTED);
    }
}
