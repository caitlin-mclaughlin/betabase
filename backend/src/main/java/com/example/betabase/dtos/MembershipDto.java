package com.example.betabase.dtos;

import java.time.LocalDate;

import com.example.betabase.enums.UserType;

public record MembershipDto(
    Long id, 
    Long userId, 
    Long gymGroupId, 
    UserType type,
    LocalDate userSince,
    boolean active
) {}
