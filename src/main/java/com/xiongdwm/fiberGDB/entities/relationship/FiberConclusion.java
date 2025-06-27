package com.xiongdwm.fiberGDB.entities.relationship;

import com.xiongdwm.fiberGDB.entities.RoutePoint;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class FiberConclusion {
    @Id
    @GeneratedValue
    private Long id;
    private String context;
    private double weight;
    private String typeSet;
    private double maxDis;
    private double minDis;
    @TargetNode
    private RoutePoint towards;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public RoutePoint getTowards() {
        return towards;
    }

    public void setTowards(RoutePoint towards) {
        this.towards = towards;
    }

    public String getTypeSet() {
        return typeSet;
    }

    public void setTypeSet(String typeSet) {
        this.typeSet = typeSet;
    }

    public double getMaxDis() {
        return maxDis;
    }

    public void setMaxDis(double maxDis) {
        this.maxDis = maxDis;
    }

    public double getMinDis() {
        return minDis;
    }

    public void setMinDis(double minDis) {
        this.minDis = minDis;
    }

    
}
