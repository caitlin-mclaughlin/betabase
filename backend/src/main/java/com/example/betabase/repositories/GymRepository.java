package com.example.betabase.repositories;

import com.example.betabase.models.Gym;
import com.example.betabase.models.Member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {

    @NonNull
    Optional<Gym> findById(@NonNull Long id);

    @Query("SELECT g FROM Gym g WHERE " +
           "LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Optional<Gym> findByName(String name);

    @Query("SELECT g FROM Gym g WHERE " +
           "LOWER(g.state) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Member> findByState(String query);

    @Query("SELECT g FROM Gym g WHERE " +
           "LOWER(g.city) LIKE LOWER(CONCAT('%', :query, '%')) AND " +
           "LOWER(g.state) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Member> findByCity(String cityState);
}
