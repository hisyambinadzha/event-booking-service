package com.hba.event_booking_service.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.hba.event_booking_service.models.entities.Event;

public interface EventRepository extends MongoRepository<Event, String> {
    // ✅ Custom aggregation to get distinct categories
    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$category' } }",
            "{ '$sort': { '_id': 1 } }"
    })
    List<String> findDistinctCategories();

    Page<Event> findAll(Pageable pageable);

    Page<Event> findByCategory(String category, Pageable pageable);

    boolean existsByTitle(String title);
}
