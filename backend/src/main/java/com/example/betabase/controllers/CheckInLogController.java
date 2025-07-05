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
import com.example.betabase.models.User;
import com.example.betabase.services.CheckInLogService;
import com.example.betabase.services.UserService;

@RestController
@RequestMapping("/api/checkins")
public class CheckInLogController {
    
    private final CheckInLogService logService;
    private final UserService userService;

    public CheckInLogController(CheckInLogService logService, UserService userService) {
        this.logService = logService;
        this.userService = userService;
    }

    @PostMapping("/log")
    public ResponseEntity<CheckInLogEntry> logCheckIn(@RequestParam Gym gym,
                                                      @RequestParam Long userId) {
        // Fetch or pass user through some service
        User user = userService.getById(userId).orElse(null);
        CheckInLogEntry entry = logService.logUserCheckIn(gym, user);
        return ResponseEntity.ok(entry);
    }

    @GetMapping
    public ResponseEntity<List<CheckInLogEntry>> getTodayLog(
            @RequestParam Gym gym,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<CheckInLogEntry> logs = logService.getLogsForGymAndDate(gym, date);
        return ResponseEntity.ok(logs);
    }
}
