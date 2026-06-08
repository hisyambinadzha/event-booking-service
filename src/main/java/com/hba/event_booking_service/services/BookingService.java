package com.hba.event_booking_service.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hba.event_booking_service.dtos.BookingRequest;
import com.hba.event_booking_service.enums.BookingStatus;
import com.hba.event_booking_service.enums.EventStatus;
import com.hba.event_booking_service.exceptions.BookingExceptionHandler.BookingNotFoundException;
import com.hba.event_booking_service.exceptions.BookingExceptionHandler.DuplicateBookingException;
import com.hba.event_booking_service.exceptions.BookingExceptionHandler.SeatsNotAvailableException;
import com.hba.event_booking_service.exceptions.EventExceptionHandler.EventClosedException;
import com.hba.event_booking_service.exceptions.EventExceptionHandler.EventExpiredException;
import com.hba.event_booking_service.exceptions.EventExceptionHandler.EventFullException;
import com.hba.event_booking_service.models.entities.Booking;
import com.hba.event_booking_service.models.entities.Event;
import com.hba.event_booking_service.models.entities.User;
import com.hba.event_booking_service.repositories.BookingRepository;

@Service
public class BookingService {
    private final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final EventService eventService;
    private final UserService userService;

    public BookingService(BookingRepository bookingRepository, EventService eventService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Transactional
    public Booking createBooking(String email, BookingRequest request) {
        LocalDateTime today = LocalDateTime.now();

        // Fetch event
        Event event = eventService.getEventById(request.getEventId());

        // Validate event
        if (event.getEventDate().compareTo(today) < 0) {
            throw new EventExpiredException();
        } else if (event.getStatus().equals(EventStatus.CLOSED)) {
            throw new EventClosedException();
        } else if (event.getSeatsAvailable() == 0) {
            throw new EventFullException();
        } else if (event.getSeatsAvailable() < request.getNumberOfSeats()) {
            throw new SeatsNotAvailableException(String.valueOf(event.getSeatsAvailable()));
        }

        // Find user
        User user = userService.getUserByEmail(email);

        // ✅ Check if user already booked this event
        List<Booking> existingBookings = bookingRepository.findAllByUserIdAndEventId(user.getId(), event.getId());

        boolean hasActiveBooking = existingBookings.stream()
                .anyMatch(b -> b.getBookingStatus() == BookingStatus.PENDING
                        || b.getBookingStatus() == BookingStatus.APPROVED);

        if (hasActiveBooking) {
            throw new DuplicateBookingException();
        }

        // Update seats
        eventService.decreaseEventSeats(event.getId(), request.getNumberOfSeats());

        // Create booking
        Booking booking = new Booking();
        booking.setBookingDate(today);
        booking.setNumberOfSeats(request.getNumberOfSeats());
        booking.setUserId(user.getId());
        booking.setEventId(event.getId());
        booking.setTotalPrice(event.getPrice().multiply(BigDecimal.valueOf(request.getNumberOfSeats())));
        booking.setBookingStatus(BookingStatus.PENDING);

        logger.info("Created booking for event: {}", booking.getEventId());

        // Save booking
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookings() {
        logger.info("Getting all bookings");

        List<Booking> bookings = bookingRepository.findAll();
        if (bookings.isEmpty()) {
            throw new BookingNotFoundException();
        }

        logger.info("Found {} bookings", bookings.size());

        return bookings;
    }

    public List<Booking> getBookingsByUserId(String userId) {
        logger.info("Getting bookings for user: {}", userId);

        List<Booking> bookings = bookingRepository.findAllByUserId(userId);
        if (bookings.isEmpty()) {
            throw new BookingNotFoundException();
        }

        logger.info("Found {} bookings for user: {}", bookings.size(), userId);

        return bookings;
    }

    public Booking getBookingById(String id) {
        logger.info("Getting booking with id: {}", id);

        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException());

        logger.info("Found booking with id: {}", id);

        return booking;
    }

    public Booking updateBookingById(String id, Booking booking) {
        logger.info("Updating booking with id: {}", id);

        Booking updatedBooking = bookingRepository.save(booking);

        logger.info("Updated booking with id: {}", id);

        return updatedBooking;
    }

    public Map<String, Object> generateReports() {
        Map<String, Object> reports = new HashMap<>();
        reports.put("totalBookings", bookingRepository.getTotalBookingsPerEvent());
        reports.put("popularEvents", bookingRepository.getMostPopularEvents());
        reports.put("revenue", bookingRepository.getRevenuePerEvent());
        reports.put("monthlyTotals", bookingRepository.getMonthlyBookingTotals());
        reports.put("seatsByCategory", bookingRepository.getSeatsSoldByCategory());
        return reports;
    }
}
