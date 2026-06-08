package com.hba.event_booking_service.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookingRequest {
    @Min(value = 1, message = "Number of seats must be at least 1")
    private int numberOfSeats;
    @NotBlank(message = "Event ID is required")
    private String eventId;
}
