package com.xiongdwm.fiberGDB.support.binLog4j;

import com.gitee.Jmysy.binlog4j.core.BinlogEvent;
import com.gitee.Jmysy.binlog4j.core.IBinlogEventHandler;
import com.gitee.Jmysy.binlog4j.springboot.starter.annotation.BinlogSubscriber;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.repository.RoutePointRepo;
import com.xiongdwm.fiberGDB.resources.RoutePointResources;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
@BinlogSubscriber(clientName = "master")
public class RoutePointEventHandler implements IBinlogEventHandler<RoutePoint> {
    @Resource
    RoutePointResources routePointResources;
    @Resource
    RoutePointRepo pointRepo;

    @Override
    public void onInsert(BinlogEvent<RoutePoint> binlogEvent) {
        RoutePoint routePoint=binlogEvent.getData();
        routePointResources.save(routePoint);

    }

    @Override
    public void onUpdate(BinlogEvent<RoutePoint> binlogEvent) {
        RoutePoint routePoint=binlogEvent.getData();
        routePointResources.save(routePoint);
    }

    @Override
    public void onDelete(BinlogEvent<RoutePoint> binlogEvent) {
        RoutePoint routePoint = binlogEvent.getData();
        pointRepo.delete(routePoint);
    }

    @Override
    public boolean isHandle(String db, String tb) {
        return db.equals("fiber")&&tb.equals("route_point");
    }
}
