package com.xiongdwm.fiberGDB.bo;

import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import jakarta.validation.constraints.NotNull;

public class FiberDto{
    private Long gdbId;
    private @NotNull Long id;
    private String name;
    private String dis;
    private String level;
    private String stage;
    private Long fromId;
    private Long toId;
    private double weight=1;

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
