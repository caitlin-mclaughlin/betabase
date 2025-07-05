package com.example.betabase.repositories;

import com.example.betabase.models.GymGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GymGroupRepository extends JpaRepository<GymGroup, Long> {
    List<GymGroup> findByNameContaining(String name);
}
