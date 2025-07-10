package com.example.betabase.dtos;

public record GymLoginDto(
    Long id,
    String username,
    String passwordHash,
    Long gymId,
    Long gymGroupId,
    String role
) {}
