package com.xiongdwm.fiberGDB.support.binlogSync.handler;


public interface TableEventHandler<T> {
    void handleInsertEvent(T entity);
    void handleUpdateEvent(T entity);
    void handleDeleteEvent(T entity);
}
