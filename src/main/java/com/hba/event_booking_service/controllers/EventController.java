package com.hba.event_booking_service.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.hba.event_booking_service.components.ApiResponseBuilder;
import com.hba.event_booking_service.components.ErrorCatalog;
import com.hba.event_booking_service.dtos.CreateEventRequest;
import com.hba.event_booking_service.exceptions.GlobalExceptionHandler.InternalServerException;
import com.hba.event_booking_service.models.entities.Event;
import com.hba.event_booking_service.services.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {
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
    public ResponseEntity<Object> getEvents(Pageable pageable,
            @RequestParam(required = false, defaultValue = "all") String category) {
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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Object> createEvent(@Valid @ModelAttribute CreateEventRequest request) throws IOException {
        try {
            Event event = eventService.createEvent(request);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, event));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Object> updateEvent(@PathVariable String id, @ModelAttribute CreateEventRequest request)
            throws IOException {
        try {
            Event updatedEvent = eventService.updateEvent(id, request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(apiResponseBuilder.result(ErrorCatalog._000, updatedEvent));
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
