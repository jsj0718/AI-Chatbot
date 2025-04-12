package com.ai.chatbot.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Map<String, Object>> handleServiceException(ServiceException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", e.getErrorCode().getStatus().value(),
                        "error", e.getErrorCode().getStatus().getReasonPhrase(),
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", e.getMessage()
                ));
    }
}