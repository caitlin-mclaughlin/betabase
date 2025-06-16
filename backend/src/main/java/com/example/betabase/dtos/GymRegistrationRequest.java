package com.example.betabase.dtos;

import com.example.betabase.models.Gym;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GymRegistrationRequest {

    @Valid
    @NotNull(message = "Gym info is required")
    private Gym gym;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
