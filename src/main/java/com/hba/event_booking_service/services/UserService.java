package com.hba.event_booking_service.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hba.event_booking_service.dtos.LoginRequest;
import com.hba.event_booking_service.dtos.RegisterRequest;
import com.hba.event_booking_service.enums.Role;
import com.hba.event_booking_service.exceptions.UserExceptionHandler.EmailAlreadyInUseException;
import com.hba.event_booking_service.exceptions.UserExceptionHandler.EmailNotFoundException;
import com.hba.event_booking_service.exceptions.UserExceptionHandler.UnauthorizedException;
import com.hba.event_booking_service.models.UserSession;
import com.hba.event_booking_service.models.entities.User;
import com.hba.event_booking_service.repositories.UserRepository;

@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyInUseException();
        }
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String createdAt = now.format(formatter);

        User user = new User();
        user.setFullname(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(createdAt);
        userRepository.save(user);

        logger.info("Created user email: {}", user.getEmail());

        return user;
    }

    public UserSession login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UnauthorizedException());

        logger.info("Found user email: {}", user.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.error("Invalid password for user email: {}", user.getEmail());
            throw new UnauthorizedException();
        }
        String token = jwtService.generateToken(user);

        logger.info("Generated token for user email: {}", user.getEmail());

        UserSession userSession = new UserSession();
        userSession.setToken(token);
        userSession.setType("Bearer");
        userSession.setEmail(user.getEmail());
        userSession.setRole(user.getRole().name());

        return userSession;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException());
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new EmailNotFoundException());
    }
}
