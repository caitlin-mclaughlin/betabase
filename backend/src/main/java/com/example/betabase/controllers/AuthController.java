package com.example.betabase.controllers;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.dtos.JwtResponse;
import com.example.betabase.dtos.LoginRequest;
import com.example.betabase.models.GymLogin;
import com.example.betabase.security.JwtService;
import com.example.betabase.services.GymLoginService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final GymLoginService gymLoginService;

    public AuthController(
            AuthenticationManager authManager,
            JwtService jwtService,
            GymLoginService gymLoginService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.gymLoginService = gymLoginService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        // Authenticate credentials
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Load user and generate token
        GymLogin login = gymLoginService.getByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Gym login not found"));

        String jwt = jwtService.generateToken(login);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody GymRegistrationRequest request) {
        // Register the gym and create associated GymLogin
        GymLogin newLogin = gymLoginService.register(request);

        // Return token for immediate login
        String jwt = jwtService.generateToken(newLogin);
        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(jwt));
    }
}
