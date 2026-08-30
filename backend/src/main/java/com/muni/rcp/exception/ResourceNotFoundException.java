package com.muni.rcp.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RcpException {
    public ResourceNotFoundException(String message) {
        super("NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
