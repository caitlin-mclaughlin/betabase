package com.betabase.dtos;

import java.time.LocalDate;

public record UserCreateDto (
    String firstName,
    String lastName,
    String phoneNumber,
    String email,
    LocalDate dateOfBirth
) {}
