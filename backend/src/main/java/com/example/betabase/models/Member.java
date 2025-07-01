package com.example.betabase.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import com.example.betabase.enums.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name="gym_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Gym gym;

    @Enumerated(EnumType.STRING)
    private MemberType type;
    @Enumerated(EnumType.STRING)
    private PronounsType pronouns;
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @NotBlank
    private String firstName;
    private String prefName;
    @NotBlank
    private String lastName;
    @NotNull
    private LocalDate dateOfBirth;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String email;
    @NotNull
    @Embedded
    private Address address;

    private String photoUrl;
    private String memberId;
    @NotNull
    private LocalDate memberSince;
    private Boolean clocked;
    private Boolean checked;

    // Emergency Contact
    @NotBlank
    private String emergencyContactName;
    @NotBlank
    private String emergencyContactPhone;
    @NotBlank
    private String emergencyContactEmail;

    /*public boolean hasMembershipAt(Long gymId) {
        return this.getGymIds().contains(gymId);
    }*/
}
