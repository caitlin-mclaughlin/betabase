package com.example.betabase.controllers;

import com.example.betabase.models.Member;
import com.example.betabase.services.MemberService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @GetMapping("/checkin/{memberId}")
    public ResponseEntity<Member> checkIn(@PathVariable String memberId) {
        return service.checkInMember(memberId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return service.save(member);
    }

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(service.getAllMembers());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Member>> searchMembers(@RequestParam String query) {
        List<Member> results = service.search(query);
        return ResponseEntity.ok(results);
    }


    @PostMapping("/{memberId}")
    public ResponseEntity<Member> updateMember(@PathVariable String memberId, @RequestBody Member updatedMember) {
        Member existing = service.getMemberById(memberId);
        if (existing != null) {
            updatedMember.setId(existing.getId()); // Keep same Member ID
            Member saved = service.save(updatedMember);
            return ResponseEntity.ok(saved);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
