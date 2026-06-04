package com.hba.event_booking_service.models.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hba.event_booking_service.enums.BookingStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "bookings")
public class Booking {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;
    private String userId;
    private String eventId;
    private int numberOfSeats;
    private LocalDateTime bookingDate;
    private BigDecimal totalPrice;
    private BookingStatus bookingStatus;
}
