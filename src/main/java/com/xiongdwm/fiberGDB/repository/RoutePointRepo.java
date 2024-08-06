package com.xiongdwm.fiberGDB.repository;

import com.xiongdwm.fiberGDB.bo.RoutePointDTOProjection;
import com.xiongdwm.fiberGDB.entities.RoutePoint;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import org.springframework.data.neo4j.repository.query.Query;

public interface RoutePointRepo extends ReactiveNeo4jRepository<RoutePoint, Long> {
    @Query("MATCH p = (start:RoutePoint {id: $startId})-[fiber:FIBER*]-(end:RoutePoint {id: $endId}) " +
            "WHERE start <> end AND " +
            "NONE(node IN nodes(p)[1..-1] WHERE node = start) AND "+
            "REDUCE (sum=0, rel IN relationships(p) | sum + rel.weight) <= $weightLimit "+
            "RETURN nodes(p) AS routes")
   Flux<RoutePointDTOProjection> findRoutesByCypher(@Param("startId")Long startId, @Param("endId") Long endId, @Param("weightLimit") double weightLimit);

//    default List<Result> getPath(long startId, long endId, List<String> typeOfs, double weightLimit) {
//        System.out.println(startId);
//        System.out.println(endId);
//        Mono<List<Result>> mono=findResultByCypher(startId,endId,weightLimit).collectList();
//        List<Result>rs=mono.block();
//        System.out.println(rs);
//       return rs;
//    }
}
