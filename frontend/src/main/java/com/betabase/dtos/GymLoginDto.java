package com.betabase.dtos;

public record GymLoginDto (
    Long id,
    String username,
    String password,
    Long gymId,
    Long gymGroupId,
    String role // e.g. "ADMIN", "STAFF"
) {}