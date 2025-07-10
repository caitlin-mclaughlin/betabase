package com.betabase.dtos;

import java.time.LocalDate;

public record UserDto (
    Long id,
    String firstName,
    String lastName,
    String phoneNumber,
    String email,
    LocalDate dateOfBirth
) {}