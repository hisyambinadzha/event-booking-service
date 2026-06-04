package com.hba.event_booking_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hba.event_booking_service.components.ApiResponseBuilder;
import com.hba.event_booking_service.components.ErrorCatalog;

@RestControllerAdvice
public class EventExceptionHandler {

    private final ApiResponseBuilder apiResponseBuilder;

    public EventExceptionHandler(ApiResponseBuilder apiResponseBuilder) {
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @ExceptionHandler(EventNotFoundException.class)
    public  ResponseEntity<Object> handleEventNotFoundException(EventNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseBuilder.result(ErrorCatalog._104));
    }

    @ExceptionHandler(EventExpiredException.class)
    public  ResponseEntity<Object> handleEventExpiredException(EventExpiredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseBuilder.result(ErrorCatalog._105));
    }

    @ExceptionHandler(EventClosedException.class)
    public  ResponseEntity<Object> handleEventClosedException(EventClosedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseBuilder.result(ErrorCatalog._106));
    }

    @ExceptionHandler(EventFullException.class)
    public  ResponseEntity<Object> handleEventFullException(EventFullException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseBuilder.result(ErrorCatalog._107));
    }

    public static class EventNotFoundException extends RuntimeException {
        public EventNotFoundException() {
            super();
        }
    }

    public static class EventExpiredException extends RuntimeException {
        public EventExpiredException() {
            super();
        }
    }

    public static class EventClosedException extends RuntimeException {
        public EventClosedException() {
            super();
        }
    }

    public static class EventFullException extends RuntimeException {
        public EventFullException() {
            super();
        }
    }
}
