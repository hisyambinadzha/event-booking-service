package com.hba.event_booking_service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hba.event_booking_service.components.ApiResponseBuilder;
import com.hba.event_booking_service.components.ErrorCatalog;
import com.hba.event_booking_service.dtos.LoginRequest;
import com.hba.event_booking_service.dtos.RegisterRequest;
import com.hba.event_booking_service.exceptions.GlobalExceptionHandler.InternalServerException;
import com.hba.event_booking_service.models.UserSession;
import com.hba.event_booking_service.models.entities.User;
import com.hba.event_booking_service.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService authService;
    private final ApiResponseBuilder apiResponseBuilder;

    public UserController(UserService authService, ApiResponseBuilder apiResponseBuilder) {
        this.authService = authService;
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponseBuilder.result(ErrorCatalog._000, user));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
        try {
            UserSession userSession = authService.login(request);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, userSession));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @GetMapping("/profile/me")
    public ResponseEntity<Object> getUserByEmail(@RequestParam String email) {
        try {
            User user = authService.getUserByEmail(email);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, user));
        } catch (InternalServerException e) {
            throw e;
        }
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable String id) {
        try {
            User user = authService.getUserById(id);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponseBuilder.result(ErrorCatalog._000, user));
        } catch (InternalServerException e) {
            throw e;
        }
    }

}
