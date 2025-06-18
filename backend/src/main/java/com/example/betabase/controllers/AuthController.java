package com.example.betabase.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.betabase.dtos.*;
import com.example.betabase.models.GymUser;
import com.example.betabase.security.JwtService;
import com.example.betabase.services.GymUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final GymUserService gymUserService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService,
                          GymUserService gymUserService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.gymUserService = gymUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Authenticate using Spring Security
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            Authentication auth = authManager.authenticate(authToken); // throws if invalid

            // Fetch full GymUser by username
            GymUser user = gymUserService.getByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT
            String jwt = jwtService.generateToken(user);

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody GymRegistrationRequest request) {
        try {
            GymUser user = gymUserService.register(request);
            String jwt = jwtService.generateToken(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(jwt));
        } catch (Exception e) {
            // Log error here if needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }
}

