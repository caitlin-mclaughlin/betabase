package com.example.betabase.controllers;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.dtos.LoginRequest;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymUser;
import com.example.betabase.services.GymService;
import com.example.betabase.services.GymUserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gyms")
public class GymController {

    private final GymService service;
    private final GymUserService gymUserService;

    public GymController(GymService service, GymUserService gymUserService) {
        this.service = service;
        this.gymUserService = gymUserService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gym> getGymById(@PathVariable Long id) {
        return service.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerGym(@Valid @RequestBody GymRegistrationRequest request) {
        GymUser user = gymUserService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return gymUserService.authenticate(request.getUsername(), request.getPassword())
            .map(user -> ResponseEntity.ok("Login successful"))
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
    }
}
