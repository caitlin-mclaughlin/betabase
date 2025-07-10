package com.example.betabase.controllers;

import com.example.betabase.dtos.GymGroupDto;
import com.example.betabase.dtos.simple.GymGroupCreateDto;
import com.example.betabase.dtos.simple.GymGroupNameDto;
import com.example.betabase.enums.GymLoginRole;
import com.example.betabase.mappers.GymGroupMapper;
import com.example.betabase.models.GymGroup;
import com.example.betabase.models.GymLogin;
import com.example.betabase.repositories.GymGroupRepository;
import com.example.betabase.services.GymGroupService;
import com.example.betabase.services.GymService;

import jakarta.annotation.security.PermitAll;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gym-groups")
public class GymGroupController {

    private final GymGroupRepository gymGroupRepository;

    private final GymGroupService gymGroupService;
    private final GymService gymService;

    public GymGroupController(GymGroupService gymGroupService, GymService gymService, GymGroupRepository gymGroupRepository) {
        this.gymGroupService = gymGroupService;
        this.gymService = gymService;
        this.gymGroupRepository = gymGroupRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymGroupDto> getGroup(@PathVariable Long id) {
        return gymGroupService.getById(id)
            .map(GymGroupMapper::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GymGroupDto> createGroup(
            @RequestBody GymGroupCreateDto createDto,
            @AuthenticationPrincipal GymLogin login) {

        if (!isAdmin(login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        GymGroup group = new GymGroup();
        group.setName(createDto.name());

        //List<Gym> gyms = gymService.getByIds(createDto.gymIds());
        //group.setGyms(gyms);

        GymGroup saved = gymGroupService.save(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(GymGroupMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymGroupDto> updateGroup(
            @PathVariable Long id,
            @RequestBody GymGroupCreateDto updateDto,
            @AuthenticationPrincipal GymLogin login) {

        if (!isAdmin(login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<GymGroup> existing = gymGroupService.getById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        GymGroup group = existing.get();
        group.setName(updateDto.name());
        //group.setGyms(gymService.getByIds(updateDto.gymIds()));

        GymGroup updated = gymGroupService.save(group);
        return ResponseEntity.ok(GymGroupMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal GymLogin login) {

        if (!isAdmin(login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        gymGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<GymGroupDto>> search(@RequestParam String name) {
        List<GymGroupDto> results = gymGroupService.searchByName(name).stream()
                .map(GymGroupMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/public-names")
    @PermitAll  // <- allows public access
    public List<GymGroupNameDto> getPublicGroupNames() {
        return gymGroupRepository.findAll().stream()
            .map(g -> new GymGroupNameDto(g.getId(), g.getName()))
            .toList();
    }


    private boolean isAdmin(GymLogin login) {
        return login.getRole() == GymLoginRole.ADMIN;
    }
}
