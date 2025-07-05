package com.example.betabase.controllers;

import com.example.betabase.dtos.GymLoginCreateDto;
import com.example.betabase.dtos.GymLoginDto;
import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.enums.GymLoginRole;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/gym-logins")
public class GymLoginController {

    private final GymLoginService loginService;
    private final GymService gymService;
    private final GymGroupService groupService;

    public GymLoginController(GymLoginService loginService, GymService gymService, GymGroupService groupService) {
        this.loginService = loginService;
        this.gymService = gymService;
        this.groupService = groupService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymLoginDto> getLoginById(@PathVariable Long id) {
        return loginService.getById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GymLoginDto> createLogin(@RequestBody @Valid GymLoginCreateDto dto) {
        Optional<Gym> gym = gymService.getById(dto.gymId());
        Optional<GymGroup> group = groupService.getById(dto.gymGroupId());

        if (gym.isEmpty() || group.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        GymLogin login = new GymLogin();
        login.setUsername(dto.username());
        login.setPasswordHash(dto.passwordHash());
        login.setGym(gym.get());
        login.setGroup(group.get());
        login.setRole(dto.role());

        GymLogin saved = loginService.save(login);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymLoginDto> updateLogin(
            @PathVariable Long id,
            @RequestBody GymLoginCreateDto updateDto,
            @AuthenticationPrincipal GymLogin requester) {

        if (!isAdmin(requester)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<GymLogin> existingOpt = loginService.getById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Gym> gym = gymService.getById(updateDto.gymId());
        Optional<GymGroup> group = groupService.getById(updateDto.gymGroupId());

        if (gym.isEmpty() || group.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        GymLogin login = existingOpt.get();
        login.setUsername(updateDto.username());
        login.setPasswordHash(updateDto.passwordHash());
        login.setGym(gym.get());
        login.setGroup(group.get());
        login.setRole(updateDto.role());

        GymLogin updated = loginService.update(id, login);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogin(
            @PathVariable Long id,
            @AuthenticationPrincipal GymLogin requester) {

        if (!isAdmin(requester)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        loginService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<GymLoginDto> registerGym(@RequestBody @Valid GymRegistrationRequest request) {
        Gym savedGym = gymService.save(request.getGym());

        GymLogin login = new GymLogin();
        login.setUsername(request.getUsername());
        login.setPasswordHash(request.getPassword());
        login.setGym(savedGym);
        login.setGroup(savedGym.getGroup());
        login.setRole(GymLoginRole.ADMIN);

        GymLogin savedLogin = loginService.save(login);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(savedLogin));
    }

    private GymLoginDto toDto(GymLogin login) {
        return new GymLoginDto(
                login.getId(),
                login.getUsername(),
                login.getPasswordHash(),
                login.getGym().getId(),
                login.getGroup() != null ? login.getGroup().getId() : null,
                login.getRole()
        );
    }

    private boolean isAdmin(GymLogin login) {
        return login.getRole() == GymLoginRole.ADMIN;
    }
}
