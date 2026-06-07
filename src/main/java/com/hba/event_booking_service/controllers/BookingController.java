package com.hba.event_booking_service.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hba.event_booking_service.components.ApiResponseBuilder;
import com.hba.event_booking_service.components.ErrorCatalog;
import com.hba.event_booking_service.dtos.BookingRequest;
import com.hba.event_booking_service.enums.BookingStatus;
import com.hba.event_booking_service.enums.EventStatus;
import com.hba.event_booking_service.exceptions.BookingExceptionHandler.SeatsNotAvailableException;
import com.hba.event_booking_service.exceptions.BookingExceptionHandler.UpdateBookingException;
import com.hba.event_booking_service.exceptions.EventExceptionHandler.EventClosedException;
import com.hba.event_booking_service.exceptions.EventExceptionHandler.EventExpiredException;
import com.hba.event_booking_service.exceptions.EventExceptionHandler.EventFullException;
import com.hba.event_booking_service.exceptions.GlobalExceptionHandler.InternalServerException;
import com.hba.event_booking_service.models.entities.Booking;
import com.hba.event_booking_service.models.entities.Event;
import com.hba.event_booking_service.models.entities.User;
import com.hba.event_booking_service.services.BookingService;
import com.hba.event_booking_service.services.EventService;
import com.hba.event_booking_service.services.JwtService;
import com.hba.event_booking_service.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final EventService eventService;
    private final UserService userService;
    private final ApiResponseBuilder apiResponseBuilder;
    private final JwtService jwtService;

    @Value("${app.jwt.secret}")
    private String secretKey;

    public BookingController(BookingService bookingService, EventService eventService, UserService userService,
            ApiResponseBuilder apiResponseBuilder, JwtService jwtService) {
        this.bookingService = bookingService;
        this.eventService = eventService;
        this.userService = userService;
        this.apiResponseBuilder = apiResponseBuilder;
        this.jwtService = jwtService;
    }

    @GetMapping("/admin")
    public ResponseEntity<Object> getBookings() {
        try {
            // Get all bookings
            List<Booking> bookings = bookingService.getBookings();

            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, bookings));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBooking(@PathVariable String id) {
        try {
            // Get booking
            Booking booking = bookingService.getBookingById(id);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, booking));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getBookings(@PathVariable String id) {
        try {
            // Get bookings
            List<Booking> bookings = bookingService.getBookingsByUserId(id);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, bookings));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("Authorization") String bearerToken,
            @Valid @RequestBody BookingRequest request) {
        try {
            // Remove "Bearer " prefix
            String token = bearerToken.replace("Bearer ", "");
            String email = jwtService.extractUsername(token);

            // Validate request
            Event event = eventService.getEventById(request.getEventId());
            if (event.getEventDate().compareTo(request.getBookingDate()) < 0) {
                throw new EventExpiredException();
            } else if (event.getStatus().equals(EventStatus.CLOSED)) {
                throw new EventClosedException();
            } else if (event.getSeatsAvailable() == 0) {
                throw new EventFullException();
            } else if (event.getSeatsAvailable() < request.getNumberOfSeats()) {
                throw new SeatsNotAvailableException(String.valueOf(event.getSeatsAvailable()));
            }

            // Minus number of seats
            event.setSeatsAvailable(event.getSeatsAvailable() - request.getNumberOfSeats());

            // Update event
            eventService.updateEventById(request.getEventId(), event);

            // Search for user
            User user = userService.getUserByEmail(email);

            // Create booking
            Booking booking = new Booking();
            booking.setBookingDate(request.getBookingDate());
            booking.setNumberOfSeats(request.getNumberOfSeats());
            booking.setUserId(user.getId());
            booking.setEventId(event.getId());
            booking.setTotalPrice(event.getPrice().multiply(BigDecimal.valueOf(booking.getNumberOfSeats())));
            booking.setBookingStatus(BookingStatus.PENDING);

            // Save booking
            Booking newBooking = bookingService.createBooking(booking);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(apiResponseBuilder.result(ErrorCatalog._000, newBooking));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader("Authorization") String bearerToken,
            @PathVariable String id) {
        try {
            // Remove "Bearer " prefix
            String token = bearerToken.replace("Bearer ", "");
            String role = jwtService.extractRole(token);

            // Get booking
            Booking booking = bookingService.getBookingById(id);

            // Validate process flow
            switch (booking.getBookingStatus()) {
                case BookingStatus.PENDING:
                    // Check if user is admin
                    if (role.equals("ADMIN")) {
                        booking.setBookingStatus(BookingStatus.APPROVED);
                    } else {
                        booking.setBookingStatus(BookingStatus.CANCELLED);
                    }
                    break;
                case BookingStatus.CANCELLED:
                case BookingStatus.REJECTED:
                    throw new UpdateBookingException("Cannot update a cancelled or rejected booking.");
                case BookingStatus.APPROVED:
                    // Check if user is admin
                    if (role.equals("ADMIN")) {
                        booking.setBookingStatus(BookingStatus.REJECTED);
                    } else {
                        throw new UpdateBookingException("Cannot update a approved booking.");
                    }
                    break;
                default:
            }

            // Update booking
            Booking updatedBooking = bookingService.updateBookingById(id, booking);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(apiResponseBuilder.result(ErrorCatalog._000, updatedBooking));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<Object> getReports() {
        Map<String, Object> reports = bookingService.generateReports();
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiResponseBuilder.result(ErrorCatalog._000, reports));
    }
}
