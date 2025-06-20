package com.example.betabase.services;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymUser;
import com.example.betabase.repositories.GymUserRepository;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class GymUserService implements UserDetailsService {

    private final GymUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final GymService gymService;

    public GymUserService(GymUserRepository repository,
                          PasswordEncoder passwordEncoder,
                          GymService gymService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.gymService = gymService;
    }

    public GymUser register(GymRegistrationRequest request) {
        // Persist the gym first (if it’s new)
        Gym gym = request.getGym();
        Gym savedGym;
        Optional<Gym> existingGym = gymService.getByName(gym.getName());
        if (existingGym.isPresent()) {
            savedGym = existingGym.get();
            System.out.println("Using existing gym: " + savedGym.getId());
        } else {
            savedGym = gymService.save(gym);
            System.out.println("Saving new gym: " + savedGym.getId());
        }

        GymUser user = new GymUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setGym(savedGym);

        return repository.save(user);
    }

    public Optional<GymUser> authenticate(String username, String rawPassword) {
        return repository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }

    public Optional<GymUser> getByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
