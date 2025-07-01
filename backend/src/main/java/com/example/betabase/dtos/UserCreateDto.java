package com.example.betabase.dtos;

import java.time.LocalDate;

public record UserCreateDto(
    String firstName, 
    String lastName,
    LocalDate dateOfBirth,
    String phone,
    String email
) {}
