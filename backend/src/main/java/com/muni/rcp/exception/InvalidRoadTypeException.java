package com.muni.rcp.exception;

import org.springframework.http.HttpStatus;

public class InvalidRoadTypeException extends RcpException {
    public InvalidRoadTypeException(String message) {
        super("INVALID_ROAD_TYPE", message, HttpStatus.BAD_REQUEST);
    }
}
