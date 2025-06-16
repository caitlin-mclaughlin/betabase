package com.example.betabase.dtos;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtResponse {
    @NotBlank
    String token;

    public JwtResponse(String token) { this.token = token; }
}
