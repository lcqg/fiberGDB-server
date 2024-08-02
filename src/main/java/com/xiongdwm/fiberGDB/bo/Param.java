package com.xiongdwm.fiberGDB.bo;

import java.io.Serial;
import java.io.Serializable;

public class Param implements Serializable {
    private int outDegree;
    private Long startId;
    private Long endId;
    private String types;
    @Serial
    private static final long serialVersionUID = 1L;

    public int getOutDegree() {
        return outDegree;
    }

    public void setOutDegree(int outDegree) {
        this.outDegree = outDegree;
    }

    public Long getStartId() {
        return startId;
    }

    public void setStartId(Long startId) {
        this.startId = startId;
    }

    public Long getEndId() {
        return endId;
    }

    public void setEndId(Long endId) {
        this.endId = endId;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }
}
