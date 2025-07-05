package com.example.betabase.dtos;

import java.time.LocalDate;
import java.util.List;

import com.example.betabase.enums.GenderType;
import com.example.betabase.enums.PronounsType;
import com.example.betabase.models.Address;

public record UserDto(
    Long id,
    String firstName,
    String prefName,
    String lastName,
    PronounsType pronouns,
    GenderType gender,
    String phoneNumber,
    String email,
    LocalDate dateOfBirth,
    Address address,
    List<Long> membershipIds,
    String emergencyContactName,
    String emergencyContactPhone,
    String emergencyContactEmail
) {}
