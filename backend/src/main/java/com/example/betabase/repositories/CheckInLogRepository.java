package com.example.betabase.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.betabase.models.CheckInLogEntry;

@Repository
public interface CheckInLogRepository extends JpaRepository<CheckInLogEntry, Long> {

    List<CheckInLogEntry> findByGymIdAndCheckInTimeBetween(Long gymId, LocalDateTime start, LocalDateTime end);
    
}
