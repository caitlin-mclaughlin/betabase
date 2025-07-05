package com.example.betabase.services;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.enums.GymLoginRole;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymLogin;
import com.example.betabase.repositories.GymLoginRepository;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class GymLoginService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(GymLoginService.class);

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
        if (request.getGym() == null) {
            throw new IllegalArgumentException("Gym information is required");
        }

        Gym gym = request.getGym();
        Gym savedGym;
        Optional<Gym> existingGym = gymService.getByName(gym.getName());

        if (existingGym.isPresent()) {
            savedGym = existingGym.get();
            logger.info("Using existing gym: {}", savedGym.getId());
        } else {
            savedGym = gymService.save(gym);
            logger.info("Saving new gym: {}", savedGym.getId());
        }

        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        GymLogin login = new GymLogin();
        login.setUsername(request.getUsername());
        login.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        login.setGym(savedGym);
        login.setGroup(savedGym.getGroup());
        login.setRole(GymLoginRole.ADMIN); // if appropriate

        return repository.save(login);
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

    public GymLogin save(GymLogin login) {
        return repository.save(login);
    }
    
    public GymLogin update(Long id, GymLogin update) {
        GymLogin existing = repository.findById(id).orElseThrow(() -> new RuntimeException("Login not found"));
        existing.setUsername(update.getUsername());
        existing.setPasswordHash(update.getPasswordHash());
        existing.setRole(update.getRole());
        existing.setGym(update.getGym());
        existing.setGroup(update.getGroup());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
