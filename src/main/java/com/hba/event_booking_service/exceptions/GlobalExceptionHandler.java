package com.hba.event_booking_service.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hba.event_booking_service.components.ApiResponseBuilder;
import com.hba.event_booking_service.components.ErrorCatalog;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ApiResponseBuilder apiResponseBuilder;

    public GlobalExceptionHandler(ApiResponseBuilder apiResponseBuilder) {
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("details", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiResponseBuilder.result(ErrorCatalog._100, response));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<Object> handleException(InternalServerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiResponseBuilder.result(ErrorCatalog._999, ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(apiResponseBuilder.result(ErrorCatalog._110, ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiResponseBuilder.result(ErrorCatalog._900, ex.getMessage()));
    }

    public static class InternalServerException extends RuntimeException {
        public InternalServerException(String message) {
            super(message);
        }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
}
