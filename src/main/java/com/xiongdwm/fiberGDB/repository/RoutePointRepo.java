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
/**
 * Currently, the BeanUtils.copyProperties method in Apache Commons BeanUtils copies all properties from the source object to the target object, including null values. This behavior can lead to unintended overwriting of existing values in the target object when the source object has null properties.
 *
 * **Scenario**:
 * When updating an entity, only a subset of properties may be provided (e.g., only the name property is provided, while other properties like age are null). In such cases, copying all properties including null values will overwrite the existing values in the target object, which is not desirable.
 *
 * **Purpose**:
 * To enhance the BeanUtils.copyProperties method to support copying only non-null properties from the source object to the target object. This feature will prevent null values from overwriting existing values in the target object, ensuring that only the provided properties are updated.
 *
 * **Proposed Solution**:
 * Implement a new method, copyNonNullProperties, that iterates over the source object's properties and copies only those that are non-null to the target object.
 */
/**
 * Dear BeanUtils Maintainers,
 *
 * I hope this message finds you well. I am writing to express my interest in contributing to the BeanUtils library. I have been using this library extensively in my projects and have found it incredibly useful. As a way to give back to the community, I would like to propose an enhancement to the library.
 *
 * **Proposal**:
 * I would like to add a new method, `copyNonNullProperties`, to the BeanUtils library. This method will copy only non-null properties from the source object to the target object. The primary benefit of this method is to prevent null values from overwriting existing values in the target object, which is particularly useful in scenarios where partial updates are performed.
 *
 * **Use Case**:
 * For example, when updating an entity, only a subset of properties may be provided (e.g., only the name property is provided, while other properties like age are null). In such cases, copying all properties including null values will overwrite the existing values in the target object, which is not desirable. The `copyNonNullProperties` method will address this issue by ensuring that only the provided properties are updated.
 *
 * I am eager to contribute to the BeanUtils library and hope to make more contributions to the community in the future. I kindly request your consideration and approval for this enhancement.
 *
 * Thank you for your time and attention.
 *
 * Best regards,
 * [Your Name]
 */