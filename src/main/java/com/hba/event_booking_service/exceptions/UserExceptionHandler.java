package com.hba.event_booking_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hba.event_booking_service.components.ApiResponseBuilder;
import com.hba.event_booking_service.components.ErrorCatalog;

@RestControllerAdvice
public class UserExceptionHandler {

    private final ApiResponseBuilder apiResponseBuilder;

    public UserExceptionHandler(ApiResponseBuilder apiResponseBuilder) {
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public  ResponseEntity<Object> handleEmailAlreadyInUseException(EmailAlreadyInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponseBuilder.result(ErrorCatalog._101));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public  ResponseEntity<Object> handleInvalidTokenException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseBuilder.result(ErrorCatalog._102));
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public  ResponseEntity<Object> handleEmailNotFoundException(EmailNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseBuilder.result(ErrorCatalog._103));
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException() {
            super();
        }
    }

    public static class EmailAlreadyInUseException extends RuntimeException {
        public EmailAlreadyInUseException() {
            super();
        }
    }

    public static class EmailNotFoundException extends RuntimeException {
        public EmailNotFoundException() {
            super();
        }
    }
}
