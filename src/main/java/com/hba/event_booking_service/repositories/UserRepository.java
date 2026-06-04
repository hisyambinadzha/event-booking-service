package com.hba.event_booking_service.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hba.event_booking_service.models.entities.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
