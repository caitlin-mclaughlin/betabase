package com.example.betabase.dtos;

import java.util.List;

public record GymGroupDto(
    Long id,
    String name,
    List<Long> gymIds
) {}
