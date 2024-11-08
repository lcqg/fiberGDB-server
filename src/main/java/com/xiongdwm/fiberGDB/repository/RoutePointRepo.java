package com.xiongdwm.fiberGDB.repository;

import com.xiongdwm.fiberGDB.bo.RoutePointDTOProjection;
import com.xiongdwm.fiberGDB.entities.RoutePoint;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

public interface RoutePointRepo extends ReactiveNeo4jRepository<RoutePoint, Long> {
    @Query("MATCH p = (start:RoutePoint {id: $startId})-[fiber:FIBER_CONCLUSION*1..5]-(end:RoutePoint {id: $endId}) " +
            "WHERE start <> end AND " +
            "NONE(node IN nodes(p)[1..-1] WHERE node = start) AND "+
            "REDUCE (sum=0, rel IN relationships(p) | sum + rel.weight) <= $weightLimit "+
            "RETURN nodes(p) AS routes "+
            "ORDER BY length(p) ASC " +
            "LIMIT 5")
   Flux<RoutePointDTOProjection> findRoutesByCypher(@Param("startId")Long startId, @Param("endId") Long endId, @Param("weightLimit") double weightLimit);

    // 整合fiber数据 两点之间的所有光缆合并
    @Query("MATCH (p1:RoutePoint)-[r:FIBER]->(p2:RoutePoint) " +
            "WITH p1, p2, MIN(r.weight) AS minWeight, COLLECT(r.name) AS context " +
            "MERGE (p1)-[newRel:FIBER_CONCLUSION {weight: minWeight, context: context}]->(p2)")
    Mono<Void> mergeFiber();
}
