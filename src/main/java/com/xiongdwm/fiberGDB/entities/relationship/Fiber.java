package com.xiongdwm.fiberGDB.entities.relationship;

import com.xiongdwm.fiberGDB.entities.RoutePoint;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;


@RelationshipProperties
public class Fiber {
    @Id
    @GeneratedValue
    private Long gdbId;
    private @NotNull Long id;
    private String name;
    private String dis;
    private String level;
    private String stage;
    @TargetNode
    private RoutePoint towards;

    private double weight=1d;

    public Long getGdbId() {
        return gdbId;
    }

    public void setGdbId(Long gdbId) {
        this.gdbId = gdbId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public RoutePoint getTowards() {
        return towards;
    }

    public void setTowards(RoutePoint towards) {
        this.towards = towards;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Fiber{" +
                "gdbId=" + gdbId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", dis='" + dis + '\'' +
                ", level='" + level + '\'' +
                ", stage='" + stage + '\'' +
                ", towards=" + towards +
                ", weight=" + weight +
                '}';
    }
}
