package com.example.betabase.dtos.simple;

import java.time.LocalDate;

public record GymRegistrationRequestDto(
    String username,
    String password,
    String gymName,
    AddressDto address,
    LocalDate gymSince,
    String groupName
) {}
