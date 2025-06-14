package com.example.betabase.services;


import com.example.betabase.models.CheckInLogEntry;
import com.example.betabase.models.Gym;
import com.example.betabase.models.Member;
import com.example.betabase.repositories.CheckInLogRepository;
import com.example.betabase.repositories.GymRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInLogService {

    @Autowired
    private CheckInLogRepository repository;

    public List<CheckInLogEntry> getLogsForDay(Long gymId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return repository.findByGymIdAndCheckInTimeBetween(gymId, start, end);
    }

    public CheckInLogEntry checkIn(Member member, Gym gym) {
        if (member == null || gym == null) {
            return null;
        }
        CheckInLogEntry entry = new CheckInLogEntry();
        entry.setMember(member);
        entry.setGym(gym);
        entry.setCheckInTime(LocalDateTime.now());
        return repository.save(entry);
    }

    public void checkOut(Long entryId) {
        CheckInLogEntry entry = repository.findById(entryId)
            .orElseThrow(() -> new RuntimeException("Log not found"));
        entry.setCheckOutTime(LocalDateTime.now());
        repository.save(entry);
    }
}
