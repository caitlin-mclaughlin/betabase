package com.example.betabase.dtos;

import java.time.LocalDate;

public record UserDto(
    Long id,
    String firstName, 
    String lastName,
    LocalDate dateOfBirth,
    String phone,
    String email
) {}
