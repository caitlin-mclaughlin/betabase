package com.example.betabase.dtos;

public record GymRegistrationResponseDto(
    Long gymGroupId,
    Long gymId,
    Long gymLoginId,
    String gymName,
    String username,
    String token // for JWT login
) {}
