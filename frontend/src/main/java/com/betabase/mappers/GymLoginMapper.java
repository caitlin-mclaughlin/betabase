package com.betabase.mappers;

import com.betabase.dtos.GymLoginDto;
import com.betabase.enums.GymLoginRole;
import com.betabase.models.GymLogin;

public class GymLoginMapper {

    public static GymLoginDto toDto(GymLogin model) {
        return new GymLoginDto(
            model.getId(),
            model.getUsername(),
            model.getPassword(),
            model.getGymId(),
            model.getGymGroupId(),
            model.getRole() != null ? model.getRole().name() : null
        );
    }

    public static GymLogin toModel(GymLoginDto dto) {
        GymLogin model = new GymLogin();
        model.setId(dto.id());
        model.setUsername(dto.username());
        model.setPassword(dto.password());
        model.setGymId(dto.gymId());
        model.setGymGroupId(dto.gymGroupId());
        if (dto.role() != null) {
            model.setRole(GymLoginRole.valueOf(dto.role()));
        }
        return model;
    }
}

