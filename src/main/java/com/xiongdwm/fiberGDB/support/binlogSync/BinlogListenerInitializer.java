package com.xiongdwm.fiberGDB.support.binlogSync;

import com.xiongdwm.fiberGDB.bo.fiberRDB.FiberEntityRDB;
import com.xiongdwm.fiberGDB.bo.fiberRDB.RoutePointEntityRDB;
import com.xiongdwm.fiberGDB.support.binlogSync.manager.EntityColumnManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BinlogListenerInitializer {
    @Autowired
    private BinlogSyncComponent binlogSyncComponent;

    @Autowired
    private EntityColumnManager entityColumnManager;


    // @PostConstruct
    public void initialize() throws IOException {
        //entity's columns init
        entityColumnManager.initializeEntityColumns();

        //registration of handlers
        binlogSyncComponent.registerTableEventHandler("neo4j_data", "fiber", FiberEntityRDB.class);
        binlogSyncComponent.registerTableEventHandler("neo4j_data", "route_point", RoutePointEntityRDB.class);
        //todo add order to handlers, 'cause insert nodes should do before insert of relationships
        //start sync
        binlogSyncComponent.startSync();
    }

}

