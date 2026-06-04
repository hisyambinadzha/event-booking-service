package com.hba.event_booking_service.models;

import lombok.Data;

@Data
public class UserSession {
    private String token;
    private String type;
    private String email;
    private String role;
}
