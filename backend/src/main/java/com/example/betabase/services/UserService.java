package com.example.betabase.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.betabase.models.User;
import com.example.betabase.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User save(User user) {
        return repository.save(user);
    }

    public Optional<User> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<User> getByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<User> searchUsers(String query, Long groupId) {
        return repository.findByQueryAndGymGroupId(query, groupId);
    }

    public List<User> searchPotentialMatches(String email, String phoneNumber) {
        return repository.findByEmailOrPhoneNumber(email, phoneNumber);
    }
}
