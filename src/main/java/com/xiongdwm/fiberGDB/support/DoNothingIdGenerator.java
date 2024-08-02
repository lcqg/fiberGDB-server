package com.xiongdwm.fiberGDB.support;

import org.springframework.data.neo4j.core.schema.IdGenerator;
import org.springframework.stereotype.Component;

@Component("doNothing")
public class DoNothingIdGenerator implements IdGenerator<Long> {
    private Long parseId;

    public void setParseId(Long parseId) {
        this.parseId = parseId;
    }

    @Override
    public Long generateId(String primaryLabel, Object entity) {
        if (parseId == null) {
            throw new IllegalStateException("Id is null");
        }
        return parseId;
    }
}
