package com.betabase.dtos;

public class JwtResponseDto {
    private String token;
    private String username;
    private Long gymId;
    private String gymName;
    private Long expiresAt; // Optional

    public JwtResponseDto() {}

    public JwtResponseDto(String token, String username, Long gymId, String gymName, Long expiresAt) {
        this.token = token;
        this.username = username;
        this.gymId = gymId;
        this.gymName = gymName;
        this.expiresAt = expiresAt;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }

    public String getGymName() { return gymName; }
    public void setGymName(String gymName) { this.gymName = gymName; }

    public Long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Long expiresAt) { this.expiresAt = expiresAt; }

}
