package com.betabase.dtos;

import com.betabase.dtos.simple.AddressDto;

import java.time.LocalDate;

public record GymRegistrationRequestDto(
    String username,
    String password,
    String gymName,
    AddressDto address,
    LocalDate gymSince,
    String groupName
) {}
