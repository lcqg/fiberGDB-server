package com.xiongdwm.fiberGDB.rest;

import com.xiongdwm.fiberGDB.support.View;
import com.xiongdwm.fiberGDB.support.binlogSync.BinlogSyncComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.xiongdwm.fiberGDB.support.binlogSync.manager.BinlogPositionManager;

@RestController
public class InitWebController {
    @Autowired
    private BinlogSyncComponent binlogSyncComponent;
    @Autowired
    private BinlogPositionManager binlogPositionManager;

    @RequestMapping("/binlog/sync/stop")
    public Object stop(){
        try{
            binlogSyncComponent.stopSync();
            return View.SUCCESS;
        }catch (Exception e){
            return View.getError(e.getLocalizedMessage());
        }
    }

    @RequestMapping("/binlog/sync/set")
    public Object set(@RequestParam("name") String name, @RequestParam("position") long position){
        try{
            binlogPositionManager.savePosition(name, position);
            return View.SUCCESS;
        }catch (Exception e){
            return View.getError(e.getLocalizedMessage());
        }
    }

    @RequestMapping("/binlog/sync/start")
    public Object start(){
        try{
            binlogSyncComponent.startSync();
            return View.SUCCESS;
        }catch (Exception e){
            return View.getError(e.getLocalizedMessage());
        }
    }
}
