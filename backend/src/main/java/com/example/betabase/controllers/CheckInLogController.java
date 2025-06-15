package com.example.betabase.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.betabase.models.CheckInLogEntry;
import com.example.betabase.models.Gym;
import com.example.betabase.models.Member;
import com.example.betabase.services.CheckInLogService;
import com.example.betabase.services.MemberService;

@RestController
@RequestMapping("/api/logs")
public class CheckInLogController {
    
    private final CheckInLogService logService;
    private final MemberService memberService;

    public CheckInLogController(CheckInLogService logService, MemberService memberService) {
        this.logService = logService;
        this.memberService = memberService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<CheckInLogEntry> logCheckIn(@RequestParam Gym gym,
                                                      @RequestParam Long memberId) {
        // Fetch or pass member through some service
        Member member = memberService.getById(memberId).orElse(null);
        CheckInLogEntry entry = logService.logMemberCheckIn(gym, member);
        return ResponseEntity.ok(entry);
    }

    @GetMapping
    public ResponseEntity<List<CheckInLogEntry>> getLogForDay(
            @RequestParam Gym gym,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<CheckInLogEntry> logs = logService.getLogsForGymAndDate(gym, date);
        return ResponseEntity.ok(logs);
    }
}
