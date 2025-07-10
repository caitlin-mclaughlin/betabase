package com.betabase.dtos;

public record JwtResponseDto (
    String token,
    String username,
    Long gymId,
    String gymName,
    Long expiresAt
) {}
