package com.muni.rcp.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedRoleException extends RcpException {
    public UnauthorizedRoleException(String message) {
        super("UNAUTHORIZED_ROLE", message, HttpStatus.FORBIDDEN);
    }
}
