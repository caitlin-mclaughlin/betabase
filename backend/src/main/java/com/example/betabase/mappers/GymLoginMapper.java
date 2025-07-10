package com.example.betabase.mappers;

import com.example.betabase.dtos.GymLoginDto;
import com.example.betabase.enums.GymLoginRole;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymGroup;
import com.example.betabase.models.GymLogin;

public class GymLoginMapper {

    public static GymLoginDto toDto(GymLogin model) {
        return new GymLoginDto(
            model.getId(),
            model.getUsername(),
            model.getPassword(),
            model.getGym().getId(),
            model.getGroup().getId(),
            model.getRole() != null ? model.getRole().name() : null
        );
    }

    public static GymLogin toModel(GymLoginDto dto, Gym gym, GymGroup group) {
        GymLogin model = new GymLogin();
        model.setId(dto.id());
        model.setUsername(dto.username());
        model.setPasswordHash(dto.passwordHash());
        model.setGym(gym);
        model.setGroup(group);
        if (dto.role() != null) {
            model.setRole(GymLoginRole.valueOf(dto.role()));
        }
        return model;
    }
}

