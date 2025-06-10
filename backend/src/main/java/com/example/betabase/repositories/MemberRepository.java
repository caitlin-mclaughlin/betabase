package com.example.betabase.repositories;

import com.example.betabase.models.Member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Optional<Member> findByMemberId(String memberId);

    Optional<Member> findById(Long id);

    @Query("SELECT m FROM Member m WHERE " +
           "LOWER(m.memberId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.prefName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Member> findByQuery(String query);
}
