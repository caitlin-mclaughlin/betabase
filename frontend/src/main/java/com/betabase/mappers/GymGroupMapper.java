package com.betabase.mappers;

import com.betabase.dtos.GymGroupDto;
import com.betabase.models.GymGroup;

public class GymGroupMapper {

    public static GymGroupDto toDto(GymGroup model) {
        return new GymGroupDto(model.getId(), model.getName());
    }

    public static GymGroup toModel(GymGroupDto dto) {
        GymGroup model = new GymGroup();
        model.setId(dto.id());
        model.setName(dto.name());
        return model;
    }

}

