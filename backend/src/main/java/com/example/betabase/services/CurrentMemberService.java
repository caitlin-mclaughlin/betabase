package com.example.betabase.services;

import com.example.betabase.models.GymLogin;
import com.example.betabase.repositories.GymLoginRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentMemberService {

    private final GymLoginRepository userRepo;

    public CurrentMemberService(GymLoginRepository userRepo) {
        this.userRepo = userRepo;
    }

    public GymLogin getCurrentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // default: principal.getUsername()
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
