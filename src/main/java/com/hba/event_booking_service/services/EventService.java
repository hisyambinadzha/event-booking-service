package com.hba.event_booking_service.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.hba.event_booking_service.dtos.CreateEventRequest;
import com.hba.event_booking_service.exceptions.EventExceptionHandler.EventNotFoundException;
import com.hba.event_booking_service.exceptions.GlobalExceptionHandler.NotFoundException;
import com.hba.event_booking_service.models.entities.Event;
import com.hba.event_booking_service.repositories.EventRepository;

@Service
public class EventService {
    private final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<String> getAllCategories() {
        logger.info("Getting all event categories");

        List<String> categories = eventRepository.findDistinctCategories();
        if (categories.isEmpty()) {
            throw new NotFoundException("Event categories not found");
        }

        logger.info("Found {} event categories", categories.size());

        return categories;
    }

    public List<Event> getEvents() {
        logger.info("Getting all events");

        List<Event> events = eventRepository.findAll();
        if (events.isEmpty()) {
            throw new EventNotFoundException();
        }

        logger.info("Found {} events", events.size());

        return events;
    }

    public Event getEventById(String id) {
        logger.info("Getting event with id: {}", id);

        Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException());

        logger.info("Found event with id: {}", id);

        return event;
    }

    public Event updateEventById(String id, Event event) {
        logger.info("Updating event with id: {}", id);

        Event updatedEvent = eventRepository.save(event);

        logger.info("Updated event with id: {}", id);

        return updatedEvent;
    }

    public void deleteEventById(String id) {
        logger.info("Deleting event with id: {}", id);

        eventRepository.deleteById(id);

        logger.info("Deleted event with id: {}", id);
    }

    public Page<Event> getEvents(Pageable pageable, String category) {
        logger.info("Getting events, category filter: {}", category);

        Page<Event> events;
        if (category != null && !category.equalsIgnoreCase("all")) {
            events = eventRepository.findByCategory(category, pageable);
        } else {
            events = eventRepository.findAll(pageable);
        }

        if (events.isEmpty()) {
            throw new EventNotFoundException();
        }

        logger.info("Found {} events", events.getTotalElements());
        return events;
    }

    public Event createEvent(CreateEventRequest request) throws IOException {

        // ✅ Validation
        if (eventRepository.existsByTitle(request.getTitle())) {
            throw new RuntimeException("Event title already exists");
        }

        if (request.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Event date cannot be in the past");
        }

        // ✅ Image handling
        String imagePath = null;

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + request.getImage().getOriginalFilename();

            java.nio.file.Path path = java.nio.file.Paths.get("uploads/" + fileName);

            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.write(path, request.getImage().getBytes());

            imagePath = "/uploads/" + fileName;
        }

        // ✅ Build entity
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setVenue(request.getVenue());
        event.setEventDate(request.getEventDate());
        event.setPrice(request.getPrice());
        event.setCapacity(request.getCapacity());
        event.setSeatsAvailable(request.getCapacity());
        event.setStatus(request.getStatus());
        event.setCreatedAt(LocalDateTime.now());
        event.setImage(imagePath);

        return eventRepository.save(event);
    }

    public Event updateEvent(String eventId, CreateEventRequest request) throws IOException {
        // ✅ Find existing event
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // ✅ Validation
        if (!existingEvent.getTitle().equals(request.getTitle()) &&
                eventRepository.existsByTitle(request.getTitle())) {
            throw new RuntimeException("Event title already exists");
        }

        if (request.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Event date cannot be in the past");
        }

        // ✅ Image handling
        String imagePath = existingEvent.getImage(); // keep old image if not replaced
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + request.getImage().getOriginalFilename();
            java.nio.file.Path path = java.nio.file.Paths.get("uploads/" + fileName);

            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.write(path, request.getImage().getBytes());

            imagePath = "/uploads/" + fileName;
        }

        // ✅ Update entity fields
        existingEvent.setTitle(request.getTitle());
        existingEvent.setDescription(request.getDescription());
        existingEvent.setCategory(request.getCategory());
        existingEvent.setVenue(request.getVenue());
        existingEvent.setEventDate(request.getEventDate());
        existingEvent.setPrice(request.getPrice());
        existingEvent.setCapacity(request.getCapacity());
        existingEvent.setSeatsAvailable(request.getCapacity()); // reset seats if capacity changes
        existingEvent.setStatus(request.getStatus());
        existingEvent.setImage(imagePath);
        existingEvent.setUpdatedAt(LocalDateTime.now()); // add updated timestamp

        return eventRepository.save(existingEvent);
    }

}
