package com.hba.event_booking_service.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hba.event_booking_service.models.entities.Booking;

public interface BookingRepository extends MongoRepository<Booking, String> {
    public List<Booking> findAllByUserId(String userId);
}
