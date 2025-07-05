package com.example.betabase.dtos;

import com.example.betabase.enums.GymLoginRole;

public record GymLoginDto(
    Long id,
    String username,
    String passwordHash,
    Long gymId,
    Long gymGroupId,
    GymLoginRole role
) {}
