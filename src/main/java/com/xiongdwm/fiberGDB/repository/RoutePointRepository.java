package com.xiongdwm.fiberGDB.repository;

import com.xiongdwm.fiberGDB.bo.RoutePointDTOProjection;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.FiberConclusion;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

public interface RoutePointRepository extends ReactiveNeo4jRepository<RoutePoint, Long> {
        @Query("MATCH p = (start:RoutePoint {id: $startId})-[fiber:FIBER_CONCLUSION*1..7]-(end:RoutePoint {id: $endId}) "
                        +
                        "WHERE start <> end AND " +
                        "NONE(node IN nodes(p)[1..-1] WHERE node = start) AND " +
                        "REDUCE (sum=0, rel IN relationships(p) | sum + rel.weight) <= $weightLimit " +
                        "RETURN nodes(p) AS routes " +
                        "ORDER BY length(p) ASC " +
                        "LIMIT $routesCount")
        Flux<RoutePointDTOProjection> findRoutesByCypher(@Param("startId") Long startId, @Param("endId") Long endId, @Param("weightLimit") double weightLimit, @Param("routesCount") int routesCount);

        @Query(
                "MATCH p = (start:RoutePoint {id: $startId})-[fiber:FIBER_CONCLUSION*1..7]-(end:RoutePoint {id: $endId}) " +
                "WHERE start <> end " +
                "AND (" +
                        "$pointUsed IS NULL OR start.exist IN split($pointUsed, ',')" +
                ") " +
                "AND (" +
                        "$siteType IS NULL OR start.level IN split($siteType, ',')" +
                ") " +
                "AND (" +
                        "$fiberUsed IS NULL OR ALL(rel IN relationships(p) WHERE rel.stage IN split($fiberUsed, ','))" +
                ") " +
                "AND (" +
                        "$nodesAbandon IS NULL OR NONE(node IN nodes(p) WHERE toString(node.id) IN split($nodesAbandon, ','))" +
                ") " +
                "AND REDUCE(sum=0, rel IN relationships(p) | sum + rel.weight) <= $weight " +
                "RETURN nodes(p) AS routes " +
                "ORDER BY length(p) ASC " +
                "LIMIT $routeCount"
        )
        Flux<RoutePointDTOProjection> retrieveRoutesByParam(
                @Param("startId") Long startId,
                @Param("endId") Long endId,
                @Param("pointUsed") String pointUsed,
                @Param("siteType") String siteType,
                @Param("fiberUsed") String fiberUsed,
                @Param("nodesAbandon") String nodesAbandon,
                @Param("weight") double weight,
                @Param("routeCount") int routeCount
        );

        @Query("MATCH (p1:RoutePoint)-[r:FIBER]->(p2:RoutePoint) " +
                        "WITH p1, p2, MIN(r.weight) AS minWeight, COLLECT(r.name) AS context, COLLECT(r.stage) AS typeSet, MAX(r.maxDis) AS maxDis, MIN(r.minDis) AS minDis "
                        +"MERGE (p1)-[newRel:FIBER_CONCLUSION {weight: minWeight, context: context, typeSet: typeSet, maxDis: coalesce(maxDis, 0), minDis:coalesce(minDis, 0)}]->(p2)")
        Mono<Void> mergeFiber();

        @Query("MATCH (a:RoutePoint {id:$pid}), (b:RoutePoint) " +
                        "WHERE a <> b " +
                        "AND point.distance(point({longitude: a.lng, latitude: a.lat}), point({longitude: b.lng, latitude: b.lat})) <= $distance "
                        +
                        "AND NOT (a)-[:FIBER|FIBER_CONCLUSION]-(b) " +
                        "RETURN b, point.distance(point({longitude: a.lng, latitude: a.lat}), point({longitude: b.lng, latitude: b.lat})) AS dist "
                        +
                        "ORDER BY dist ASC")
        Flux<RoutePointDTOProjection> findRoutPointInDistanceWithNoConnection(@Param("pid") Long pid,@Param("distance") double distance);

        @Query("MATCH p = shortestPath((start:RoutePoint {id: $startId})-[:FIBER_CONCLUSION*..15]-(end:RoutePoint {id: $endId})) "
                        + "RETURN nodes(p)")
        Flux<RoutePointDTOProjection> bfsFlux(@Param("startId") Long startId,@Param("endId") Long endId); // shortest 函数只支持常量深度，所以写死15条中间光缆

        @Query("MATCH (n:RoutePoint {id: $id}) RETURN n")
        Mono<RoutePoint> findByPrimaryKey(@Param("id") long id);
        @Query("MATCH (n:RoutePoint {name: $name}) RETURN n")
        Mono<RoutePoint>findOneByName(@Param("name")String name);

        @Query("MATCH (from:RoutePoint {id: $fromId})-[r:FIBER_CONCLUSION]-(to:RoutePoint {id: $toId}) RETURN r")
        Mono<FiberConclusion> findFiberConclusionByFromIdAndToId(@Param("fromId") Long fromId, @Param("toId") Long toId);

}
