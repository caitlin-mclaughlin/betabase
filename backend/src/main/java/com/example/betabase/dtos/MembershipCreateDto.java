package com.example.betabase.dtos;

import java.time.LocalDate;

import com.example.betabase.enums.MemberType;

public record MembershipCreateDto(
    Long userId, 
    Long gymId, 
    MemberType memberType,
    LocalDate memberSince,
    boolean active
) {}
