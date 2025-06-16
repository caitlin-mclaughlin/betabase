package com.example.betabase.repositories;

import com.example.betabase.models.GymUser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GymUserRepository extends JpaRepository<GymUser, Long> {
    Optional<GymUser> findByUsername(String username);
}
