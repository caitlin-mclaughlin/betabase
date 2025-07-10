package com.example.betabase.repositories;

import com.example.betabase.models.Gym;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {

    @NonNull
    Optional<Gym> findById(@NonNull Long id);
    Optional<Gym> findByName(String name);
    Long countByGroupId(Long groupId);
}
