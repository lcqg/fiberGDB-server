package com.xiongdwm.fiberGDB.support.binlogSync;

import com.xiongdwm.fiberGDB.bo.fiberRDB.FiberEntityRDB;
import com.xiongdwm.fiberGDB.support.binlogSync.manager.EntityColumnManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BinlogListenerInitializer {
    @Autowired
    private BinlogSyncComponent binlogSyncComponent;

    @Autowired
    private EntityColumnManager entityColumnManager;


    @PostConstruct
    public void initialize() throws IOException {
        entityColumnManager.initializeEntityColumns();
        binlogSyncComponent.registerTableEventHandler("fiber", "fiber", FiberEntityRDB.class);
        binlogSyncComponent.startSync();
    }

}

