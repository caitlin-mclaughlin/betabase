package com.example.betabase.dtos;

import java.time.LocalDate;

import com.example.betabase.models.Address;

public record GymDto(
    Long id,
    String name, 
    Address address, 
    LocalDate userSince,
    Long groupId
) {}
