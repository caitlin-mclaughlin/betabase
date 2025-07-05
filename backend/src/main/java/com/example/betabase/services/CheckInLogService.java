package com.example.betabase.services;


import com.example.betabase.models.CheckInLogEntry;
import com.example.betabase.models.DailyCheckInLog;
import com.example.betabase.models.Gym;
import com.example.betabase.models.User;
import com.example.betabase.repositories.CheckInLogRepository;
import com.example.betabase.repositories.DailyCheckInLogRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class CheckInLogService {

    private final DailyCheckInLogRepository dailyLogRepo;
    private final CheckInLogRepository logEntryRepo;

    public CheckInLogService(DailyCheckInLogRepository dailyLogRepo,
                             CheckInLogRepository logEntryRepo) {
        this.dailyLogRepo = dailyLogRepo;
        this.logEntryRepo = logEntryRepo;
    }

    public CheckInLogEntry logUserCheckIn(Gym gym, User user) {
        LocalDate today = LocalDate.now();
        DailyCheckInLog dailyLog = dailyLogRepo.findByGymAndDate(gym, today)
                .orElseGet(() -> {
                    DailyCheckInLog newLog = new DailyCheckInLog();
                    newLog.setGym(gym);
                    newLog.setDate(today);
                    return dailyLogRepo.save(newLog);
                });

        CheckInLogEntry entry = new CheckInLogEntry();
        entry.setUser(user);
        entry.setCheckInTime(LocalDateTime.now());
        entry.setDailyLog(dailyLog);
        return logEntryRepo.save(entry);
    }

    public List<CheckInLogEntry> getLogsForGymAndDate(Gym gym, LocalDate date) {
        return dailyLogRepo.findByGymAndDate(gym, date)
                .map(dailyLog -> logEntryRepo.findByDailyLog_Id(dailyLog.getId()))
                .orElse(Collections.emptyList());
    }
}
