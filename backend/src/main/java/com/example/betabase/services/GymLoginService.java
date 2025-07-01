package com.example.betabase.services;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymLogin;
import com.example.betabase.repositories.GymLoginRepository;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class GymLoginService implements UserDetailsService {

    private final GymLoginRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final GymService gymService;

    public GymLoginService(GymLoginRepository repository,
                          PasswordEncoder passwordEncoder,
                          GymService gymService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.gymService = gymService;
    }

    public GymLogin getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // This is the username from the JWT

        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));
    }

    public GymLogin register(GymRegistrationRequest request) {
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

        GymLogin user = new GymLogin();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setGym(savedGym);

        return repository.save(user);
    }

    public Optional<GymLogin> authenticate(String username, String rawPassword) {
        return repository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }

    public Optional<GymLogin> getByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Optional<GymLogin> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
