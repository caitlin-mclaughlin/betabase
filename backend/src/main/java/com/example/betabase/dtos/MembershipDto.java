package com.example.betabase.dtos;

import java.time.LocalDate;

import com.example.betabase.enums.MemberType;

public record MembershipDto(
    Long id, 
    Long userId, 
    Long gymId, 
    MemberType memberType,
    LocalDate memberSince,
    boolean active
) {}
