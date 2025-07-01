package com.example.betabase.repositories;

import com.example.betabase.models.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NonNull
    Optional<User> findById(@NonNull Long id);

    @Query("SELECT m FROM User m JOIN FETCH m.gym WHERE m.gym.id = :gymId AND (" +
        "LOWER(m.userId) LIKE LOWER(CONCAT(:query, '%')) OR " +
        "LOWER(m.firstName) LIKE LOWER(CONCAT(:query, '%')) OR " +
        "LOWER(m.lastName) LIKE LOWER(CONCAT(:query, '%')) OR " +
        "LOWER(m.prefName) LIKE LOWER(CONCAT(:query, '%')))")
    List<User> findByQueryAndGymId(@Param("query") String query, @Param("gymId") Long gymId);

}
