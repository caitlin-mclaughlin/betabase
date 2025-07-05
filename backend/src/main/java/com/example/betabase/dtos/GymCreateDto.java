package com.example.betabase.dtos;

import java.time.LocalDate;

import com.example.betabase.models.Address;

public record GymCreateDto(
    String name,
    Long groupId,
    Address address, 
    LocalDate userSince
) {}
