package com.example.betabase.dtos;

import java.time.LocalDate;

import com.example.betabase.enums.UserType;

public record MembershipCreateDto(
    Long userId, 
    Long gymGroupId, 
    UserType type,
    LocalDate userSince,
    boolean active
) {}
