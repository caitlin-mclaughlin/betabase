package com.example.betabase.models;

import java.time.LocalDate;
import java.util.List;

import com.example.betabase.enums.GenderType;
import com.example.betabase.enums.PronounsType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String firstName;
    private String prefName;
    @NotBlank
    private String lastName;

    @Enumerated(EnumType.STRING)
    private PronounsType pronouns;
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private Address address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Membership> memberships;
    
    // Emergency Contact
    @NotBlank
    private String emergencyContactName;
    @NotBlank
    private String emergencyContactPhone;
    @NotBlank
    private String emergencyContactEmail;
    
}
