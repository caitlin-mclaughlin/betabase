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

    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("""
        SELECT u FROM User u
        WHERE EXISTS (
            SELECT 1 FROM Membership m
            WHERE m.user.id = u.id AND m.gymGroup.id = :groupId
        )
        AND (
            LOWER(u.firstName) LIKE LOWER(CONCAT(:query, '%')) OR
            LOWER(u.lastName) LIKE LOWER(CONCAT(:query, '%')) OR
            LOWER(u.prefName) LIKE LOWER(CONCAT(:query, '%')) OR
            CAST(u.id AS string) LIKE CONCAT(:query, '%')
        )
    """)
    List<User> findByQueryAndGymGroupId(@Param("query") String query, @Param("groupId") Long groupId);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email) OR u.phoneNumber = :phone")
    List<User> findByEmailOrPhoneNumber(@Param("email") String email, @Param("phone") String phoneNumber);
}
