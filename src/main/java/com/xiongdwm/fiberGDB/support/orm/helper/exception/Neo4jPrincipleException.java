package com.xiongdwm.fiberGDB.support.orm.helper.exception;

public class Neo4jPrincipleException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public Neo4jPrincipleException(String type,String message) {
        super(type+":"+message);
    }
}
