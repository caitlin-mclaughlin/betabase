package com.example.betabase.controllers;

import com.example.betabase.dtos.GymLoginCreateDto;
import com.example.betabase.dtos.GymLoginDto;
import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.dtos.GymRegistrationResponseDto;
import com.example.betabase.dtos.simple.GymRegistrationRequestDto;
import com.example.betabase.enums.GymLoginRole;
import com.example.betabase.mappers.AddressMapper;
import com.example.betabase.mappers.GymLoginMapper;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymGroup;
import com.example.betabase.models.GymLogin;
import com.example.betabase.services.GymLoginService;
import com.example.betabase.services.GymService;
import com.example.betabase.services.GymGroupService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/gym-logins")
public class GymLoginController {

    private final GymLoginService gymLoginService;
    private final GymService gymService;
    private final GymGroupService gymGroupService;

    public GymLoginController(GymLoginService gymLoginService, GymService gymService, GymGroupService gymGroupService) {
        this.gymLoginService = gymLoginService;
        this.gymService = gymService;
        this.gymGroupService = gymGroupService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymLoginDto> getLoginById(@PathVariable Long id) {
        return gymLoginService.getById(id)
                .map(GymLoginMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GymLoginDto> createLogin(@RequestBody @Valid GymLoginCreateDto dto) {
        Optional<Gym> gym = gymService.getById(dto.gymId());
        Optional<GymGroup> group = gymGroupService.getById(dto.gymGroupId());

        if (!gym.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!group.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        GymLogin login = new GymLogin();
        login.setUsername(dto.username());
        login.setPasswordHash(dto.passwordHash());
        login.setGym(gym.get());
        login.setGroup(group.get());
        login.setRole(dto.role());

        GymLogin saved = gymLoginService.save(login);
        return ResponseEntity.status(HttpStatus.CREATED).body(GymLoginMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymLoginDto> updateLogin(
            @PathVariable Long id,
            @RequestBody GymLoginCreateDto updateDto,
            @AuthenticationPrincipal GymLogin requester) {

        if (!isAdmin(requester)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Gym> gym = gymService.getById(updateDto.gymId());
        Optional<GymGroup> group = gymGroupService.getById(updateDto.gymGroupId());

        if (!gym.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!group.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        GymLogin login = new GymLogin();
        login.setUsername(updateDto.username());
        login.setPasswordHash(updateDto.passwordHash());
        login.setGym(gym.get());
        login.setGroup(group.get());
        login.setRole(updateDto.role());

        return ResponseEntity.ok(GymLoginMapper.toDto(gymLoginService.update(id, login)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogin(
            @PathVariable Long id,
            @AuthenticationPrincipal GymLogin requester) {

        if (!isAdmin(requester)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        gymLoginService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(GymLogin login) {
        return login.getRole() == GymLoginRole.ADMIN;
    }
}
