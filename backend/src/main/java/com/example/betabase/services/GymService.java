package com.example.betabase.services;


import com.example.betabase.models.Gym;
import com.example.betabase.repositories.GymRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GymService {

    private final GymRepository repository;

    public GymService(GymRepository repository) {
        this.repository = repository;
    }

    public Gym save(Gym gym) {
        return repository.save(gym);
    }

    public List<Gym> getAllGyms() {
        return repository.findAll();
    }

    public Optional<Gym> getById(Long id) {
        return repository.findById(id);
    }

    public List<Gym> getByIds(List<Long> ids) {
        return repository.findAllById(ids);
    }

    public Optional<Gym> getByName(String name) {
        return repository.findByName(name);
    }

    // You can later add methods to check-in history, logs, etc.
}
