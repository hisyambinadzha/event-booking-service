package com.hba.event_booking_service.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hba.event_booking_service.exceptions.BookingExceptionHandler.BookingNotFoundException;
import com.hba.event_booking_service.models.entities.Booking;
import com.hba.event_booking_service.repositories.BookingRepository;

@Service
public class BookingService {
    private final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(Booking booking) {
        logger.info("Creating booking for event: {}", booking.getEventId());

        Booking newBooking = bookingRepository.save(booking);

        logger.info("Created booking for event: {}", newBooking.getEventId());

        return newBooking;
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
