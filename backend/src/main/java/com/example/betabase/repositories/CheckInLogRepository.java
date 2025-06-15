package com.example.betabase.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.betabase.models.CheckInLogEntry;

@Repository
public interface CheckInLogRepository extends JpaRepository<CheckInLogEntry, Long> {

    List<CheckInLogEntry> findByDailyLog_Id(Long dailyLogId);
    
}
