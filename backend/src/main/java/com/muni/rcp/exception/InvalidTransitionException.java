package com.muni.rcp.exception;

import org.springframework.http.HttpStatus;

public class InvalidTransitionException extends RcpException {
    public InvalidTransitionException(String message) {
        super("INVALID_TRANSITION", message, HttpStatus.BAD_REQUEST);
    }
}
