package com.xiongdwm.fiberGDB.support.binLog4j;

import com.gitee.Jmysy.binlog4j.core.BinlogEvent;
import com.gitee.Jmysy.binlog4j.core.IBinlogEventHandler;
import com.gitee.Jmysy.binlog4j.springboot.starter.annotation.BinlogSubscriber;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import com.xiongdwm.fiberGDB.repository.RoutePointRepo;
import com.xiongdwm.fiberGDB.resources.RoutePointResources;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
@BinlogSubscriber(clientName = "master")
public class FiberEventHandler implements IBinlogEventHandler<Fiber> {
    @Resource
    RoutePointResources routePointResources;
    @Resource
    RoutePointRepo pointRepo;

    @Override
    public void onInsert(BinlogEvent<Fiber> binlogEvent) {


    }

    @Override
    public void onUpdate(BinlogEvent<Fiber> binlogEvent) {

    }

    @Override
    public void onDelete(BinlogEvent<Fiber> binlogEvent) {

    }

    @Override
    public boolean isHandle(String s, String s1) {
        return false;
    }
}
