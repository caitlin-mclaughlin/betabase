package com.example.betabase.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.betabase.dtos.MembershipCreateDto;
import com.example.betabase.dtos.MembershipDto;
import com.example.betabase.exceptions.DuplicateMembershipException;
import com.example.betabase.models.Membership;
import com.example.betabase.services.GymGroupService;
import com.example.betabase.services.MembershipService;
import com.example.betabase.services.UserService;

@RestController
@RequestMapping("/api/gym-users/memberships")
public class MembershipController {

    private final MembershipService membershipService;
    private final UserService userService;
    private final GymGroupService gymGroupService;

    public MembershipController(MembershipService membershipService, UserService userService, GymGroupService gymGroupService) {
        this.membershipService = membershipService;
        this.userService = userService;
        this.gymGroupService = gymGroupService;
    }

    @PostMapping
    public ResponseEntity<MembershipDto> createMembership(@RequestBody MembershipCreateDto dto) {
        Optional<Membership> membershipOpt = membershipService.getByUserIdAndGymGroupId(dto.userId(), dto.gymGroupId());
        if (membershipOpt.isPresent()) {
            throw new DuplicateMembershipException("Membership already exists");
        }
        
        Membership membership = new Membership();
        membership.setUser(userService.getById(dto.userId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found")));
        membership.setGymGroup(gymGroupService.getById(dto.gymGroupId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gym group not found")));
        membership.setType(dto.type());
        membership.setUserSince(dto.userSince());
        membership.setActive(dto.active());
        membershipService.save(membership);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(membership));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MembershipDto>> getUserMemberships(@PathVariable Long userId) {
        List<Membership> memberships = membershipService.getMembershipsForUser(userId);
        if (memberships == null || memberships.size() == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return ResponseEntity.ok(memberships.stream().map(this::toDto).toList());
    }

    @GetMapping("/gym/{gymId}")
    public ResponseEntity<List<MembershipDto>> getGymMemberships(@PathVariable Long gymId) {
        List<Membership> memberships = membershipService.getMembershipsForGym(gymId);
        if (memberships == null || memberships.size() == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gym not found");
        }
        return ResponseEntity.ok(memberships.stream().map(this::toDto).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMembership(@PathVariable Long id) {
        membershipService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/gym/{gymGroupId}")
    public ResponseEntity<MembershipDto> getMembershipByUserAndGym(
            @PathVariable Long userId,
            @PathVariable Long gymGroupId) {
        Membership membership = membershipService.getByUserIdAndGymGroupId(userId, gymGroupId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));

        return ResponseEntity.ok(toDto(membership));
    }

    private MembershipDto toDto(Membership m) {
        return new MembershipDto(
                m.getId(),
                m.getUser().getId(),
                m.getGymGroup().getId(),
                m.getType(),
                m.getUserSince(),
                m.isActive()
        );
    }
}
