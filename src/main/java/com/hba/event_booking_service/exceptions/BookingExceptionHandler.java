package com.hba.event_booking_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hba.event_booking_service.components.ApiResponseBuilder;
import com.hba.event_booking_service.components.ErrorCatalog;

@RestControllerAdvice
public class BookingExceptionHandler {

    private final ApiResponseBuilder apiResponseBuilder;

    public BookingExceptionHandler(ApiResponseBuilder apiResponseBuilder) {
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<Object> handleEventNotFoundException(BookingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseBuilder.result(ErrorCatalog._112));
    }

    @ExceptionHandler(SeatsNotAvailableException.class)
    public ResponseEntity<Object> handleEventNotFoundException(SeatsNotAvailableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseBuilder.result(ErrorCatalog._108, ex.getMessage()));
    }

    @ExceptionHandler(UpdateBookingException.class)
    public ResponseEntity<Object> handleUpdateBookingException(UpdateBookingException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponseBuilder.result(ErrorCatalog._109, ex.getMessage()));
    }

    @ExceptionHandler(DuplicateBookingException.class)
    public ResponseEntity<Object> handleDuplicateBookingException(DuplicateBookingException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponseBuilder.result(ErrorCatalog._111, ex.getMessage()));
    }

    public static class BookingNotFoundException extends RuntimeException {
        public BookingNotFoundException() {
            super();
        }
    }

    public static class SeatsNotAvailableException extends RuntimeException {
        public SeatsNotAvailableException(String message) {
            super(message);
        }
    }

    public static class UpdateBookingException extends RuntimeException {
        public UpdateBookingException(String message) {
            super(message);
        }
    }

    public static class DuplicateBookingException extends RuntimeException {
        public DuplicateBookingException() {
            super();
        }
    }
}
