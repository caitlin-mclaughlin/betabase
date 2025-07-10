package com.example.betabase.dtos.simple;

public record AddressDto(
    String streetAddress,
    String apartmentNumber,
    String city,
    String state,
    String zipCode
) {}
