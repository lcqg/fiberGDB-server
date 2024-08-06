package com.xiongdwm.fiberGDB.support.binlogSync;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.xiongdwm.fiberGDB.support.binlogSync.handler.GenericTableEventHandler;
import com.xiongdwm.fiberGDB.support.binlogSync.handler.TableEventHandler;
import com.xiongdwm.fiberGDB.support.binlogSync.manager.BinlogPositionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class BinlogSyncComponent {
    @Value("${frdb.hostname}")
    private String hostname;

    @Value("${frdb.port}")
    private int port;

    @Value("${frdb.username}")
    private String username;

    @Value("${frdb.password}")
    private String password;

    private BinaryLogClient client;
    private final Map<Long, String> tableMap;
    private final Map<String, TableEventHandler<?>> handlers;

    private final TableEventHandlerFactory handlerFactory;

    public BinlogSyncComponent(TableEventHandlerFactory handlerFactory) {
        this.tableMap = new HashMap<>();
        this.handlers = new HashMap<>();
        this.handlerFactory = handlerFactory;
    }

    public void registerTableEventHandler(String databaseName, String tableName, Class<?> entityClass) {
        String key = getMapKey(databaseName, tableName);
        TableEventHandler<?> handler = handlerFactory.createHandler(entityClass);
        handlers.put(key, handler);
        System.out.println("================registered handler:"+handlers.keySet()+"===========>>>>");
    }

    public void startSync() throws IOException {
        BinlogPositionManager.BinlogPosition binlogPosition = BinlogPositionManager.loadPosition();

        client = new BinaryLogClient(hostname, port, username, password);
        System.out.println("=================connected to binlog client=================>>>>");
        if (binlogPosition == null) return;

        client.setBinlogFilename(binlogPosition.getBinlogFilename());
        client.setBinlogPosition(binlogPosition.getPosition());
        System.out.println("=========binlog filename:"+client.getBinlogFilename()+", pos:"+client.getBinlogPosition()+"=======>>>>");

        client.registerEventListener(event -> {
            EventType eventType = event.getHeader().getEventType();
            switch (eventType) {
                case TABLE_MAP -> handleTableMapEvent(event);
                case EXT_WRITE_ROWS,WRITE_ROWS -> handleWriteRowsEvent(event);
                case EXT_UPDATE_ROWS,UPDATE_ROWS-> handleUpdateRowsEvent(event);
                case EXT_DELETE_ROWS,DELETE_ROWS-> handleDeleteRowsEvent(event);
            }
            BinlogPositionManager.savePosition(client.getBinlogFilename(), client.getBinlogPosition());
        });

        //unblocking
        new Thread(()->{
            try {
                client.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleTableMapEvent(Event event) {
        TableMapEventData data = event.getData();
        String fullTableName = data.getDatabase() + "." + data.getTable();
        tableMap.put(data.getTableId(), fullTableName);
    }

    private void handleWriteRowsEvent(Event event) {
        WriteRowsEventData data = event.getData();
        String fullTableName = tableMap.get(data.getTableId());
        if (fullTableName != null) {
            TableEventHandler<?> handler = handlers.get(fullTableName);
            System.out.println(handler);
            if (handler != null) {
                ((GenericTableEventHandler<?>) handler).handleInsertEvent(data);
            }
        }
    }

    private void handleUpdateRowsEvent(Event event) {
        UpdateRowsEventData data = event.getData();
        String fullTableName = tableMap.get(data.getTableId());

        if (fullTableName != null) {
            TableEventHandler<?> handler = handlers.get(fullTableName);
            if (handler != null) {
                ((GenericTableEventHandler<?>) handler).handleUpdateEvent(data);
            }
        }
    }

    private void handleDeleteRowsEvent(Event event) {
        DeleteRowsEventData data = event.getData();
        String fullTableName = tableMap.get(data.getTableId());

        if (fullTableName != null) {
            TableEventHandler<?> handler = handlers.get(fullTableName);
            if (handler != null) {
                ((GenericTableEventHandler<?>) handler).handleDeleteEvent(data);
            }
        }
    }

    private String getMapKey(String databaseName, String tableName) {
        return databaseName + "." + tableName;
    }

    public void stopSync() throws IOException {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }

}
