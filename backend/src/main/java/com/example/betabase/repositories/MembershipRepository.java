package com.example.betabase.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.example.betabase.models.Membership;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    @NonNull
    Optional<Membership> findById(@NonNull Long id);

    List<Membership> findByUserId(Long userId);
    List<Membership> findByGymGroupId(Long gymGroupId);
    Optional<Membership> findByUserIdAndGymGroupId(Long userId, Long gymGroupId);
}
