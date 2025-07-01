package com.example.betabase.controllers;

import com.example.betabase.dtos.MemberDto;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymLogin;
import com.example.betabase.models.Member;
import com.example.betabase.services.GymLoginService;
import com.example.betabase.services.MemberService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users/gym/members")
public class MemberController {

    private final MemberService service;
    private final GymLoginService gymUserService;

    public MemberController(MemberService service, GymLoginService gymUserService) {
        this.service = service;
        this.gymUserService = gymUserService;

    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id, @AuthenticationPrincipal GymLogin user) {
        Member member = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(toDto(member));
    }

    @PostMapping
    public ResponseEntity<MemberDto> createMember(@RequestBody Member member) {
        Gym gym = gymUserService.getCurrentAuthenticatedUser().getGym();
        member.setGym(gym);
        Member saved = service.save(member);
        return saved != null ? ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved)) : 
                            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        List<MemberDto> dtos = service.getAllMembers().stream()
            .map(this::toDto)
            .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MemberDto>> searchMembers(
            @RequestParam String query,
            @AuthenticationPrincipal GymLogin user) {

        if (user.getGym() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gym not set");
        }

        Long gymId = user.getGym().getId();
        List<Member> members = service.searchMembers(query, gymId);

        List<MemberDto> dtoList = members.stream()
            .map(this::toDto)
            .toList();

        return ResponseEntity.ok(dtoList);
    }

    private MemberDto toDto(Member m) {
        return new MemberDto(
            m.getId(),
            m.getFirstName(),
            m.getPrefName(),
            m.getLastName(),
            m.getPronouns(),
            m.getGender(),
            m.getPhoneNumber(),
            m.getEmail(),
            m.getDateOfBirth(),
            m.getAddress(),
            m.getMemberId(),
            m.getType(),
            m.getMemberSince()
        );
    }

    @PostMapping("/{id}")
    public ResponseEntity<MemberDto> updateMember(@PathVariable Long id, @RequestBody Member updatedMember) {
        Member existing = service.getById(id).orElse(null);
        if (existing != null) {
            updatedMember.setId(existing.getId()); // preserve ID
            Member saved = service.save(updatedMember);
            return ResponseEntity.ok(toDto(saved));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
