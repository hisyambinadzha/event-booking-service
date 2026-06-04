package com.hba.event_booking_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EventRequest {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Category is required")
    private String category;
    @NotBlank(message = "Venue is required")
    private String venue;
    @NotBlank(message = "Event date is required")
    private LocalDateTime eventDate;
    @Min(value = 0, message = "Price must be positive value or 0")
    private BigDecimal price;
    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;
    @Valid
    private String image;
}
