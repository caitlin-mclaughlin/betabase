package com.example.betabase.controllers;

import com.example.betabase.dtos.GymCreateDto;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymGroup;
import com.example.betabase.services.GymService;
import com.example.betabase.services.GymGroupService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/gyms")
public class GymController {

    private final GymService gymService;
    private final GymGroupService gymGroupService;

    public GymController(GymService gymService, GymGroupService gymGroupService) {
        this.gymService = gymService;
        this.gymGroupService = gymGroupService;
    }

    @PostMapping
    public ResponseEntity<Gym> createGym(@RequestBody GymCreateDto dto) {
        GymGroup group = gymGroupService.getById(dto.groupId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "GymGroup not found"));

        Gym gym = new Gym();
        gym.setName(dto.name());
        gym.setGroup(group);
        gym.setAddress(dto.address());
        gym.setUserSince(dto.userSince());

        return ResponseEntity.status(HttpStatus.CREATED).body(gymService.save(gym));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gym> getGymById(@PathVariable Long id) {
        return gymService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
