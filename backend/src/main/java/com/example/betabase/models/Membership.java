package com.example.betabase.models;

import java.time.LocalDate;

import com.example.betabase.enums.UserType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Membership {
    @Id 
    @GeneratedValue 
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GymGroup gymGroup;

    @Enumerated(EnumType.STRING)
    private UserType type; // ADMIN, STAFF, USER, VISITOR

    private LocalDate userSince;
    private boolean active;
    
}
