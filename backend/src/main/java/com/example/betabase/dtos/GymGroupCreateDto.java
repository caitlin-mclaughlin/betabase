package com.example.betabase.dtos;

import java.util.List;

public record GymGroupCreateDto(
    String name,
    List<Long> gymIds
) {}
