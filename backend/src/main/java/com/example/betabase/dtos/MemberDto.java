package com.example.betabase.dtos;

import java.time.LocalDate;

import com.example.betabase.enums.GenderType;
import com.example.betabase.enums.MemberType;
import com.example.betabase.enums.PronounsType;
import com.example.betabase.models.Address;

public record MemberDto(
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
    String memberId,
    MemberType type,
    LocalDate memberSince
) {}
