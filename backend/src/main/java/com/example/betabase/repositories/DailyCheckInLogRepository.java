package com.example.betabase.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.betabase.models.DailyCheckInLog;
import com.example.betabase.models.Gym;

public interface DailyCheckInLogRepository extends JpaRepository<DailyCheckInLog, Long> {

    Optional<DailyCheckInLog> findByGymAndDate(Gym gym, LocalDate date);
    
}
