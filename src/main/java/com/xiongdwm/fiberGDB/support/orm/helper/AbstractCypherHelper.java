package com.xiongdwm.fiberGDB.support.orm.helper;


import com.xiongdwm.fiberGDB.support.orm.entity.iInterfaces.DynamicPrinciple;
import org.neo4j.driver.Driver;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author xiong
 * @version 1.0.0
 * @neo4jVersion 5.21.2-community
 * @since 2021/05/31 16:00
 * @param <T> Relationship Entity class
 */
public abstract class AbstractCypherHelper<T>{

//    private final Neo4jTemplate neo4jTemplate;
    public enum RelationshipType {
        DIRECTED,
        UNDIRECTED,
        COMBINE
    }
    public enum OperationType{
        CREATE,
        UPDATE,
        DELETE,
        RETRIEVE
    }

    private final Neo4jClient neo4jClient;

//    public AbstractCypherHelper(Neo4jTemplate neo4jTemplate){
//        this.neo4jTemplate=neo4jTemplate;
//
//    }

    public AbstractCypherHelper(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public abstract boolean createRelationship(Object start, Object end, T relationship, RelationshipType direction);

    public abstract int updateRelationship(Object start, Object end, T relationship);

    public abstract List<T> retrieveRelationshipByPrinciple(DynamicPrinciple principle);


    protected void query(String cypher) {
        Collection<Map<String, Object>> o=neo4jClient.query(cypher).fetch().all();
        System.out.println(new ArrayList<>(o));
    }
}