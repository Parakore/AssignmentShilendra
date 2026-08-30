package com.muni.rcp.exception;

import org.springframework.http.HttpStatus;

public class TenantMismatchException extends RcpException {
    public TenantMismatchException(String message) {
        super("TENANT_MISMATCH", message, HttpStatus.FORBIDDEN);
    }
}
