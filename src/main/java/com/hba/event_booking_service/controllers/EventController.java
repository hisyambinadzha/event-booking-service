package com.hba.event_booking_service.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hba.event_booking_service.components.ApiResponseBuilder;
import com.hba.event_booking_service.components.ErrorCatalog;
import com.hba.event_booking_service.dtos.EventRequest;
import com.hba.event_booking_service.enums.EventStatus;
import com.hba.event_booking_service.exceptions.GlobalExceptionHandler.InternalServerException;
import com.hba.event_booking_service.models.entities.Event;
import com.hba.event_booking_service.services.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;
    private final ApiResponseBuilder apiResponseBuilder;

    public EventController(EventService eventService, ApiResponseBuilder apiResponseBuilder) {
        this.eventService = eventService;
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @GetMapping("/categories")
    public ResponseEntity<Object> getCategories() {
        try {
            List<String> events = eventService.getAllCategories();

            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, events));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<Object> getEvents() {
        try {
            List<Event> events = eventService.getEvents();

            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, events));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @GetMapping("/page")
    public ResponseEntity<Object> getEvents(Pageable pageable, @RequestParam(required = false, defaultValue = "all") String category) {
        try {
            Page<Event> events = eventService.getEvents(pageable, category);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(apiResponseBuilder.result(ErrorCatalog._000, events));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEvent(@PathVariable String id) {
        try {
            Event event = eventService.getEventById(id);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, event));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<Object> createEvent(@Valid @RequestBody EventRequest request) {
        try {
            // Get current date
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            LocalDateTime createdAt = now.toLocalDateTime();
            logger.info("Current date: {}", now.format(formatter));

            // Create event
            Event event = new Event();
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setCategory(request.getCategory());
            event.setVenue(request.getVenue());
            event.setEventDate(request.getEventDate());
            event.setPrice(request.getPrice());
            event.setCapacity(request.getCapacity());
            event.setStatus(EventStatus.OPEN);
            event.setSeatsAvailable(request.getCapacity());
            event.setCreatedAt(createdAt);
            event.setImage(request.getImage());

            // Save event
            Event newEvent = eventService.createEvent(event);

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponseBuilder.result(ErrorCatalog._000, newEvent));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateEvent(@PathVariable String id, @Valid @RequestBody EventRequest request) {
        try {
            // Get existing event
            Event event = eventService.getEventById(id);

            // Update event
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setCategory(request.getCategory());
            event.setVenue(request.getVenue());
            event.setEventDate(request.getEventDate());
            event.setPrice(request.getPrice());
            event.setCapacity(request.getCapacity());

            // Update event
            Event updatedEvent = eventService.updateEventById(id, event);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, updatedEvent));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteEvent(@PathVariable String id) {
        try {
            // Get existing event
            Event event = eventService.getEventById(id);

            // Delete event
            eventService.deleteEventById(event.getId());
            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000));
        } catch (InternalServerException e) {
            throw e;
        }
    }
}
