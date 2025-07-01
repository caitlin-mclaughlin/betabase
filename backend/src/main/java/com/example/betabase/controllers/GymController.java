package com.example.betabase.controllers;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.dtos.LoginRequest;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymLogin;
import com.example.betabase.services.GymService;
import com.example.betabase.services.GymLoginService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class GymController {

    private final GymService service;
    private final GymLoginService userService;

    public GymController(GymService service, GymLoginService gymUserService) {
        this.service = service;
        this.userService = gymUserService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymLogin> getGymUserById(@PathVariable Long id) {
        return userService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/gym/{id}")
    public ResponseEntity<Gym> getGymById(@PathVariable Long id) {
        return service.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
