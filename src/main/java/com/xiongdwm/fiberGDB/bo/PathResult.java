package com.xiongdwm.fiberGDB.bo;

import com.xiongdwm.fiberGDB.entities.RoutePoint;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.summary.ResultSummary;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class PathResult{
    private List<Object> routes;

    public PathResult() {
    }

    public PathResult(List<Object> routes) {
        this.routes = routes;
    }

    public List<Object> getRoutes() {
        return routes;
    }

}
