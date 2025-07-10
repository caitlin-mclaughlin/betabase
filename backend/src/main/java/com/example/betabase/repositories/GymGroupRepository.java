package com.example.betabase.repositories;

import com.example.betabase.models.GymGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymGroupRepository extends JpaRepository<GymGroup, Long> {
    Optional<GymGroup> findByName(String name);
    List<GymGroup> findByNameContaining(String name);
}
