package com.example.betabase.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.example.betabase.models.Gym;
import com.example.betabase.models.Membership;
import com.example.betabase.models.User;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    
    @NonNull
    Optional<Membership> findById(@NonNull Long id);

    List<Membership> findByUser(@Param("query") User user);

    List<Membership> findByGym(@Param("query") Gym gym);
}
