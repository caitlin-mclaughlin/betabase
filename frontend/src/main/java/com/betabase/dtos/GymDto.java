package com.betabase.dtos;

import java.time.LocalDate;

import com.betabase.models.Address;

public record GymDto (
    Long id,
    String name,
    String groupName,
    Address address,
    LocalDate gymSince
) {}