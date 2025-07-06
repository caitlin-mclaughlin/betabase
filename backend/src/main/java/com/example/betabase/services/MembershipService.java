package com.example.betabase.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.betabase.models.Membership;
import com.example.betabase.repositories.MembershipRepository;

@Service
public class MembershipService {

    private final MembershipRepository repository;
    public MembershipService(MembershipRepository repository) {
        this.repository = repository;
    }

    public Membership save(Membership membership) {
        return repository.save(membership);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Membership> getMembershipsForUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public List<Membership> getMembershipsForGym(Long gymGroupId) {
        return repository.findByGymGroupId(gymGroupId);
    }

    public Optional<Membership> getByUserIdAndGymGroupId(Long userId, Long gymGroupId) {
        return repository.findByUserIdAndGymGroupId(userId, gymGroupId);
    }
}
