package com.hba.event_booking_service.models.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hba.event_booking_service.enums.EventStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "events")
public class Event {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;
    @Indexed(unique = true)
    private String title;
    private String description;
    private String category;
    private String venue;
    private LocalDateTime eventDate;
    private BigDecimal price;
    private int capacity;
    private int seatsAvailable;
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String image;
    @Version
    private Long version;
}
