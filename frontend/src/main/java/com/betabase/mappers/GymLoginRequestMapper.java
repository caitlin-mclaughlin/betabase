package com.betabase.mappers;

import com.betabase.dtos.GymLoginRequestDto;
import com.betabase.models.GymLoginRequest;

public class GymLoginRequestMapper {
    
    public static GymLoginRequestDto toDto(GymLoginRequest model) {
        return new GymLoginRequestDto(model.getUsername(), model.getPassword());
    }

    public static GymLoginRequest toModel(GymLoginRequestDto dto) {
        GymLoginRequest model = new GymLoginRequest();
        model.setUsername(dto.username());
        model.setPassword(dto.password());
        return model;
    }

}
