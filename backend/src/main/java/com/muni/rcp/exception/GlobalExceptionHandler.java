package com.muni.rcp.exception;

import com.muni.rcp.dto.ErrorDTO;
import com.muni.rcp.dto.ResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RcpException.class)
    public ResponseEntity<Map<String, Object>> handleRcpException(RcpException ex) {
        log.warn("RCP Domain Exception: code={}, message={}", ex.getCode(), ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ResponseInfo", ResponseInfo.failed(null));
        body.put("Errors", List.of(new ErrorDTO(ex.getCode(), ex.getMessage())));

        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation failure: {}", ex.getMessage());

        List<ErrorDTO> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorDTO(
                        "INVALID_INPUT",
                        String.format("Field '%s': %s", fieldError.getField(), fieldError.getDefaultMessage())
                ))
                .collect(Collectors.toList());

        if (errors.isEmpty()) {
            errors = List.of(new ErrorDTO("INVALID_INPUT", "Request validation failed"));
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ResponseInfo", ResponseInfo.failed(null));
        body.put("Errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ResponseInfo", ResponseInfo.failed(null));
        body.put("Errors", List.of(new ErrorDTO("INVALID_INPUT", ex.getMessage())));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unhandled server exception", ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ResponseInfo", ResponseInfo.failed(null));
        body.put("Errors", List.of(new ErrorDTO("INTERNAL_SERVER_ERROR", "An unexpected error occurred: " + ex.getMessage())));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
