package com.example.betabase.controllers;

import com.example.betabase.dtos.GymGroupCreateDto;
import com.example.betabase.dtos.GymGroupDto;
import com.example.betabase.enums.GymLoginRole;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymGroup;
import com.example.betabase.models.GymLogin;
import com.example.betabase.services.GymGroupService;
import com.example.betabase.services.GymService;
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

    private final GymGroupService groupService;
    private final GymService gymService;

    public GymGroupController(GymGroupService groupService, GymService gymService) {
        this.groupService = groupService;
        this.gymService = gymService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymGroupDto> getGroup(@PathVariable Long id) {
        return groupService.getById(id)
            .map(this::toDto)
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

        List<Gym> gyms = gymService.getByIds(createDto.gymIds());
        group.setGyms(gyms);

        GymGroup saved = groupService.save(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymGroupDto> updateGroup(
            @PathVariable Long id,
            @RequestBody GymGroupCreateDto updateDto,
            @AuthenticationPrincipal GymLogin login) {

        if (!isAdmin(login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<GymGroup> existing = groupService.getById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        GymGroup group = existing.get();
        group.setName(updateDto.name());
        group.setGyms(gymService.getByIds(updateDto.gymIds()));

        GymGroup updated = groupService.save(group);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal GymLogin login) {

        if (!isAdmin(login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<GymGroupDto>> search(@RequestParam String name) {
        List<GymGroupDto> results = groupService.searchByName(name).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    private GymGroupDto toDto(GymGroup group) {
        List<Long> gymIds = group.getGyms().stream()
                .map(Gym::getId)
                .collect(Collectors.toList());

        return new GymGroupDto(
            group.getId(),
            group.getName(),
            gymIds
        );
    }

    private boolean isAdmin(GymLogin login) {
        return login.getRole() == GymLoginRole.ADMIN;
    }

    private boolean isKiosk(GymLogin login) {
        return login.getRole() == GymLoginRole.KIOSK;
    }
}
