package com.xiongdwm.fiberGDB.support.binlogSync;

import com.xiongdwm.fiberGDB.support.binlogSync.handler.GenericTableEventHandler;
import com.xiongdwm.fiberGDB.support.binlogSync.handler.TableEventHandler;
import com.xiongdwm.fiberGDB.support.binlogSync.manager.EntityColumnManager;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public record TableEventHandlerFactory(
        EntityColumnManager entityColumnManager) {

    public <T> TableEventHandler<T> createHandler(Class<T> entityClass) {
        Map<String, Integer> columnIndexMap = entityColumnManager.getEntityColumns(entityClass);
        return new GenericTableEventHandler<>(entityClass, columnIndexMap);
    }
}
