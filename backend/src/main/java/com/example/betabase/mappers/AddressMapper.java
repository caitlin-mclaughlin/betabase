package com.example.betabase.mappers;

import com.example.betabase.dtos.simple.AddressDto;
import com.example.betabase.models.Address;

public class AddressMapper {

    public static AddressDto toDto(Address model) {
        return new AddressDto(
            model.getStreetAdress(),
            model.getApartmentNumber(),
            model.getCity(),
            model.getState(),
            model.getZipCode()
        );
    }

    public static Address toModel(AddressDto dto) {
        Address model = new Address();
        model.setStreetAdress(dto.streetAddress());
        model.setApartmentNumber(dto.apartmentNumber());
        model.setCity(dto.city());
        model.setState(dto.state());
        model.setZipCode(dto.zipCode());
        return model;
    }
}
