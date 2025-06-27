package com.xiongdwm.fiberGDB.support.exception;

public class CoordinateSystemException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CoordinateSystemException(String message) {
        super(message);
    }

    public CoordinateSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoordinateSystemException(Throwable cause) {
        super(cause);
    }

}
