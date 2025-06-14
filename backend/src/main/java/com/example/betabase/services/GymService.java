package com.example.betabase.services;


import com.example.betabase.models.Gym;
import com.example.betabase.repositories.GymRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GymService {

    @Autowired
    private final GymRepository repo;

    public GymService(GymRepository repo) {
        this.repo = repo;
    }

    public Gym save(Gym gym) {
        return repo.save(gym);
    }

    public List<Gym> getAllGyms() {
        return repo.findAll();
    }

    public Optional<Gym> getById(Long id) {
        return repo.findById(id);
    }

    public Optional<Gym> getByName(String name) {
        return repo.findByName(name);
    }

    // You can later add methods to check-in history, logs, etc.
}
