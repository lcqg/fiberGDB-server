package com.xiongdwm.fiberGDB.entities.relationship;

import com.xiongdwm.fiberGDB.entities.RoutePoint;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.TargetNode;


public class FiberConclusion {
    @Id
    @GeneratedValue
    private Long id;
    private String context;
    private double weight;
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
}
