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
    public Mono<Void> createFiber(Long fromId,Long toId,Fiber fiber) {
        Mono<RoutePoint>fromPoint=pointRepo.findById(fromId);
        Mono<RoutePoint>toPoint=pointRepo.findById(toId);

        return Mono.zip(fromPoint,toPoint).flatMap(t->{
            RoutePoint from=t.getT1();
            System.out.println(from);
            RoutePoint to=t.getT2();
            System.out.println(to);
            fiber.setTowards(to);
            System.out.println(fiber.toString());
            from.addCable(fiber);
            System.out.println(from.getCables());

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
    public List<LinkedList<RoutePointDTOProjection>> retrieve(Long startId,Long endId,double weightLimit){
        List<RoutePointDTOProjection>queryResult=pointRepo.findRoutesByCypher(startId,endId,weightLimit).collectList()
                .blockOptional().orElse(Collections.emptyList());
        if(queryResult.isEmpty())return Collections.emptyList();
        List<LinkedList<RoutePointDTOProjection>>result=new ArrayList<>();
        LinkedList<RoutePointDTOProjection> partialResult=new LinkedList<>();
        for(RoutePointDTOProjection r:queryResult) {
            if (r.getId().longValue() == endId.longValue()) {
                partialResult.addLast(r);
                result.add(partialResult);
                partialResult=new LinkedList<>();
                continue;
            }
            partialResult.addLast(r);
        }
        return result;
    }



    @Override
    public Long save(RoutePoint point) {
        return pointRepo.save(point).blockOptional().orElseThrow().getId();
    }

    @Override
    public void createFiberNoneReactive(Long fromId, Long toId, Fiber fiber) {
        RoutePoint fromPoint=pointRepo.findById(fromId).blockOptional().orElse(null);
        RoutePoint toPoint=pointRepo.findById(toId).blockOptional().orElse(null);
        if(fromPoint==null||toPoint==null)return;
        CypherHelper<Fiber>fiberRepo= new CypherHelper<Fiber>(neo4jClient);
        fiberRepo.createRelationship(fromPoint,toPoint,fiber, AbstractCypherHelper.RelationshipType.DIRECTED);
    }
}
