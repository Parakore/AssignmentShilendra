package com.muni.rcp.exception;

import org.springframework.http.HttpStatus;

public class InvalidInputException extends RcpException {
    public InvalidInputException(String message) {
        super("INVALID_INPUT", message, HttpStatus.BAD_REQUEST);
    }
}
