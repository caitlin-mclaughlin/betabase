package com.example.betabase.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GymLoginRequestDto {
    
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }
}
