package com.example.betabase.repositories;

import com.example.betabase.models.GymLogin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GymLoginRepository extends JpaRepository<GymLogin, Long> {
    Optional<GymLogin> findByUsername(String username);
}
