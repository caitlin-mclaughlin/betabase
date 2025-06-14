package com.example.betabase.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.betabase.models.CheckInLogEntry;
import com.example.betabase.models.Gym;
import com.example.betabase.models.Member;
import com.example.betabase.services.CheckInLogService;
import com.example.betabase.services.GymService;
import com.example.betabase.services.MemberService;

@RestController
@RequestMapping("/api/checkins")
public class CheckInLogController {

    @Autowired
    private CheckInLogService logService;

    @Autowired
    private GymService gymService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/gym/{gymId}/date/{date}")
    public List<CheckInLogEntry> getLogsForDay(@PathVariable Long gymId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return logService.getLogsForDay(gymId, date);
    }

    @PostMapping("/checkin")
    public CheckInLogEntry checkIn(@RequestParam Long memberId, @RequestParam Long gymId) {
        // fetch member and gym from their services
        Member member = memberService.getById(memberId).orElse(null);
        Gym gym = gymService.getById(gymId).orElse(null);
        return logService.checkIn(member, gym);
    }

    @PostMapping("/checkout/{entryId}")
    public void checkOut(@PathVariable Long entryId) {
        logService.checkOut(entryId);
    }
}