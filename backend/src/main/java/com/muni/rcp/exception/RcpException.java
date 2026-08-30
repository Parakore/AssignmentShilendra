package com.muni.rcp.exception;

import org.springframework.http.HttpStatus;

public class RcpException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;

    public RcpException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
