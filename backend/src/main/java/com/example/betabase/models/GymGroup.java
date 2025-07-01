package com.example.betabase.models;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GymGroup {

    @Id
    @GeneratedValue 
    private Long id;
    
    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull
    @OneToMany(mappedBy = "group")
    private List<Gym> gyms;
    
}
