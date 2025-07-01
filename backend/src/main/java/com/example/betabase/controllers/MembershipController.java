package com.example.betabase.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.betabase.dtos.MemberDto;
import com.example.betabase.dtos.MembershipCreateDto;
import com.example.betabase.dtos.MembershipDto;
import com.example.betabase.models.Membership;
import com.example.betabase.services.MembershipService;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MembershipDto>> getMembershipsForPerson(@PathVariable Long personId) {
        List<Membership> memberships = membershipService.getMembershipsForUser(personId);
        List<MembershipDto> dtoList = memberships.stream()
            .map(this::toDto)
            .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/gym/{gymId}")
    public ResponseEntity<List<MembershipDto>> getMembershipsForGym(@PathVariable Long gymId) {
        List<Membership> memberships = membershipService.getMembershipsForGym(gymId);
        List<MembershipDto> dtoList = memberships.stream()
            .map(this::toDto)
            .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping
    public ResponseEntity<MembershipDto> createMembership(@RequestBody Membership membership) {
        return ResponseEntity.ok(toDto(membershipService.createMembership(membership)));
    }

    private MembershipDto toDto(Membership m) {
        return new MembershipDto(
            m.getId(),
            m.getUserId(),
            m.getGymId(),
            m.getType(),
            m.getMemberSince(),
            m.getActive()
        );
    }
}

