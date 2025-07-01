package com.example.betabase.services;


import com.example.betabase.models.Member;
import com.example.betabase.repositories.MemberRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository repo;

    public MemberService(MemberRepository repo) {
        this.repo = repo;
    }

    public Optional<Member> checkInMember(Long id) {
        return repo.findById(id);
    }

    public Member save(Member member) {
        return repo.save(member);
    }

    public List<Member> getAllMembers() {
        return repo.findAll();
    }

    public Optional<Member> getById(Long id) {
        return repo.findById(id);
    }

    public List<Member> searchMembers(String query, Long gymId) {
        return repo.findByQueryAndGymId(query, gymId);
    }

    // You can later add methods to check-in history, logs, etc.
}
