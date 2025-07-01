package com.example.betabase.models;

import java.time.LocalDate;

import com.example.betabase.enums.MemberType;

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

    @JoinColumn
    @ManyToOne (fetch = FetchType.LAZY)
    private Long userId;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Long gymId;

    @Enumerated(EnumType.STRING)
    private MemberType type; // ADMIN, STAFF, MEMBER, VISITOR

    private LocalDate memberSince;
    private Boolean active;
    
}
