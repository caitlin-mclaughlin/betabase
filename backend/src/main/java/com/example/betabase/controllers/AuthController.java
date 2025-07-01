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
    private final GymLoginService gymUserService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService,
                          GymLoginService gymUserService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.gymUserService = gymUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        // Use Spring Security to authenticate
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authManager.authenticate(authToken); // Will throw if invalid

        // Get full user info to generate JWT
        GymLogin user = gymUserService.getByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody GymRegistrationRequest request) {
        GymLogin newUser = gymUserService.register(request);
        String token = jwtService.generateToken(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(token));
    }
}
