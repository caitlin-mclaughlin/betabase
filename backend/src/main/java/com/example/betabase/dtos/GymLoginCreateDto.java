package com.example.betabase.dtos;

import com.example.betabase.enums.GymLoginRole;

public record GymLoginCreateDto(
    String username,
    String passwordHash,
    Long gymId,
    Long gymGroupId,
    GymLoginRole role
) {}
