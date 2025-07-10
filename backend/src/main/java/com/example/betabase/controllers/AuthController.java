package com.example.betabase.controllers;

import com.example.betabase.dtos.GymLoginRequestDto;
import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.dtos.GymRegistrationResponseDto;
import com.example.betabase.dtos.JwtResponse;
import com.example.betabase.dtos.simple.GymRegistrationRequestDto;
import com.example.betabase.enums.GymLoginRole;
import com.example.betabase.mappers.AddressMapper;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymGroup;
import com.example.betabase.models.GymLogin;
import com.example.betabase.security.JwtService;
import com.example.betabase.services.GymGroupService;
import com.example.betabase.services.GymLoginService;
import com.example.betabase.services.GymService;

import jakarta.validation.Valid;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final GymLoginService gymLoginService;
    private final GymService gymService;
    private final GymGroupService gymGroupService;

    public AuthController(
            AuthenticationManager authManager,
            JwtService jwtService,
            GymLoginService gymLoginService,
            GymService gymService,
            GymGroupService gymGroupService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.gymLoginService = gymLoginService;
        this.gymService = gymService;
        this.gymGroupService = gymGroupService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody GymLoginRequestDto request) {
        // Authenticate credentials
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Load user and generate token
        GymLogin login = gymLoginService.getByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Login not found"));

        String jwt = jwtService.generateToken(login);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<GymRegistrationResponseDto> register(@RequestBody @Valid GymRegistrationRequestDto dto) {
        System.out.println("\nDEBUG: Received registration for gym: " + dto.gymName()+"\n");

        GymLogin login = gymLoginService.registerNewGymLogin(dto);

        String jwt = jwtService.generateToken(login);
        Gym gym = login.getGym();
        GymGroup group = login.getGroup();

        GymRegistrationResponseDto response = new GymRegistrationResponseDto(
            group.getId(),
            gym.getId(),
            login.getId(),
            gym.getName(),
            login.getUsername(),
            jwt
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
