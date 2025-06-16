package com.example.betabase.services;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymUser;
import com.example.betabase.repositories.GymUserRepository;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class GymUserService {

    private final GymUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public GymUserService(GymUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public GymUser register(GymRegistrationRequest request) {

        GymUser user = new GymUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // hash password
        user.setGym(request.getGym());

        return repository.save(user);
    }

    public Optional<GymUser> authenticate(String username, String rawPassword) {
        return repository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }
}
