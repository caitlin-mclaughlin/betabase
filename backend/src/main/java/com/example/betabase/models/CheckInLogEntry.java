package com.example.betabase.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @ManyToOne
    private DailyCheckInLog dailyLog;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getLogDate() {
        return checkInTime.toLocalDate();
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Gym getGym() { return gym; }
    public void setGym(Gym gym) { this.gym = gym; }

    public DailyCheckInLog getDailyLog() { return dailyLog; }
    public void setDailyLog(DailyCheckInLog dailyLog) { this.dailyLog = dailyLog; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }
    
}
