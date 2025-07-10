package com.betabase.dtos;

import java.time.LocalDate;

import com.betabase.enums.UserType;

public record MembershipCreateDto (
    Long userId,
    Long gymId,
    UserType type, // ADMIN, STAFF, MEMBER, etc.
    LocalDate memberSince
) {}
