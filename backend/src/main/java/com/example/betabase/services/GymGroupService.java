package com.example.betabase.services;

import com.example.betabase.models.GymGroup;
import com.example.betabase.repositories.GymGroupRepository;
import com.example.betabase.repositories.GymRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class GymGroupService {

    private final GymGroupRepository groupRepository;
    private final GymRepository gymRepository;

    public GymGroupService(GymGroupRepository groupRepository, GymRepository gymRepository) {
        this.groupRepository = groupRepository;
        this.gymRepository = gymRepository;
    }

    public Optional<GymGroup> getById(Long id) {
        return groupRepository.findById(id);
    }

    public GymGroup save(GymGroup group) {
        return groupRepository.save(group);
    }

    public void delete(Long id) {
        // Prevent deletion if any gyms still belong to the group
        Long gymCount = gymRepository.countByGroupId(id);
        if (gymCount > 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Cannot delete group with associated gyms.");
        }
        groupRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return groupRepository.existsById(id);
    }

    public Optional<GymGroup> findByName(String name) {
        return groupRepository.findByName(name);
    }

    public List<GymGroup> searchByName(String name) {
        return groupRepository.findByNameContaining(name);
    }
}
