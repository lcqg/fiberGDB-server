package com.xiongdwm.fiberGDB.resources.impl;

import com.xiongdwm.fiberGDB.bo.PathResult;
import com.xiongdwm.fiberGDB.bo.RoutePointDTOProjection;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import com.xiongdwm.fiberGDB.repository.RoutePointRepository;
import com.xiongdwm.fiberGDB.resources.RoutePointResources;
import com.xiongdwm.fiberGDB.support.GpsUtils;
import com.xiongdwm.fiberGDB.support.orm.helper.AbstractCypherHelper;
import com.xiongdwm.fiberGDB.support.orm.helper.provider.CypherHelper;
import jakarta.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class RoutePointResourcesImpl implements RoutePointResources {
    @Resource
    private RoutePointRepository pointRepo;
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
    public List<PathResult> retrieve(Long startId, Long endId, double weightLimit, int routeCounts,double maxDistance) {
        List<RoutePointDTOProjection> queryResult = pointRepo
                .findRoutesByCypher(startId, endId, weightLimit, routeCounts)
                .collectList()
                .blockOptional().orElse(Collections.emptyList());
        System.out.println(queryResult.size());
        LinkedList<PathResult> result = new LinkedList<>();
        if (!queryResult.isEmpty()) {
            List<RoutePointDTOProjection> partial = new ArrayList<>();
            for (RoutePointDTOProjection node : queryResult) {
                partial.add(node);
                if (node.getId().longValue() == endId.longValue()) {
                    PathResult pathResult = new PathResult(new ArrayList<>(partial), true, 0.0d);
                    result.addLast(pathResult);
                    partial.clear();
                }
            }
        }
        result.forEach(it -> {
            System.out.println(it);
            System.out.println("-----------------");
        });
        
        if (queryResult.size() < routeCounts) {
            System.out.println("================== start bfs search ==========================");
            var remain=routeCounts - queryResult.size();
            List<PathResult> fromStartBfs= bfs(endId, maxDistance, startId, remain);
            List<PathResult> fromEndBfs= bfs(startId, maxDistance, endId, remain).stream().peek(it -> {
                Collections.reverse(it.routes()); // 移除起点
            }).toList();
            fromStartBfs.addAll(fromEndBfs);
            fromStartBfs.sort(Comparator.comparingDouble(PathResult::buildDistance));
            if (fromStartBfs.size() > remain) fromStartBfs = fromStartBfs.subList(0, remain);
            result.addAll(fromStartBfs);
        }
        return result;
    }

    public Flux<PathResult> retrieveFlux(Long startId, Long endId, double weightLimit, int routeCounts) {
        Flux<RoutePointDTOProjection> mainPathFlux = pointRepo
                .findRoutesByCypher(startId, endId, weightLimit, routeCounts);

        Flux<PathResult> mainPaths = mainPathFlux
                .bufferUntil(node -> node.getId().longValue() == endId.longValue())
                .filter(list -> !list.isEmpty())
                .map(list -> new PathResult(list, true, 0.0d));

        Flux<RoutePointDTOProjection> nearbyPoints = pointRepo
                .findRoutPointInDistanceWithNoConnection(startId, 500.0);

        Flux<PathResult> nearbyPaths = nearbyPoints
                .filter(nearby -> !nearby.getId().equals(endId))
                .flatMap(nearby -> pointRepo.bfsFlux(nearby.getId(), endId)
                        .collectList()
                        .filter(list -> !list.isEmpty())
                        .map(list -> new PathResult(list, false, 0.0d))
                )
                .take(routeCounts);

        return Flux.concat(mainPaths, nearbyPaths)
                .take(routeCounts);
    }

    @Override
    public RoutePoint findRoutePointByName(String name) {
        RoutePoint point=pointRepo.findOneByName(name)
                .blockOptional()
                .orElse(null);
        return point;
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

    private List<PathResult> bfs(Long target, double maxDistance,Long station,int remain) {
            LinkedList<PathResult> result = new LinkedList<>();
            RoutePoint stationPoint = pointRepo.findByPrimaryKey(station).blockOptional().orElse(null);
            if (stationPoint == null) {
                System.out.println("Start point not found: " + station);
                return Collections.emptyList();
            }
            System.out.println(stationPoint.toString());
            RoutePointDTOProjection projection=new RoutePointDTOProjection();
            BeanUtils.copyProperties(stationPoint, projection);
            List<RoutePointDTOProjection> nearbyPoints = pointRepo
                    .findRoutPointInDistanceWithNoConnection(station, maxDistance)
                    .collectList().blockOptional().orElse(Collections.emptyList());
            System.out.println("nearbyPoints: " + nearbyPoints.size());
            if (nearbyPoints.isEmpty())
                return Collections.emptyList();
            
            System.out.println("remain: " + remain);
            for (RoutePointDTOProjection nearby : nearbyPoints) {
                if (remain <= 0)
                    break;
                if (nearby.getId().longValue() == target.longValue())
                    continue;
                List<RoutePointDTOProjection> list = pointRepo.bfsFlux(nearby.getId(), target)
                        .collectList().blockOptional().orElse(Collections.emptyList());
                if (list.isEmpty()) continue;
                list.add(0, projection);
                double dis= GpsUtils.getDistance(projection.getLng(),projection.getLat(),list.get(1).getLng(),list.get(1).getLat());
                PathResult pathResult = new PathResult(list, false, dis);
                result.addLast(pathResult);
                remain--;
            }

        return result;
    }
}
