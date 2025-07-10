package com.betabase.services;

import com.betabase.dtos.GymRegistrationRequestDto;
import com.betabase.dtos.JwtResponseDto;
import com.betabase.dtos.GymLoginRequestDto;
import com.betabase.models.Gym;
import com.betabase.utils.AuthSession;
import com.betabase.utils.JwtUtils;
import com.betabase.utils.TokenStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.util.Map;
import java.util.Optional;

public class GymApiService {
    private final String baseUrl;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public GymApiService(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
/*
    public String registerGym(Gym gym, String username, String password) throws Exception {
        String body = mapper.writeValueAsString(new GymRegistrationRequestDto(username, password, gym));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            // Extract the JWT token from the response
            Map<String, String> json = mapper.readValue(response.body(), new TypeReference<>() {});
            return json.get("token");
        } else {
            System.out.println("Failed registration: " + response.body());
            return null;
        }
    }
*/
    public Optional<JwtResponseDto> login(String username, String password) throws Exception {
        String requestBody = mapper.writeValueAsString(new GymLoginRequestDto(username, password));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/login"))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .header("Content-Type", "application/json")
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JwtResponseDto jwtDto = mapper.readValue(response.body(), JwtResponseDto.class);
            return Optional.of(jwtDto);
        } else if (response.statusCode() == 401) {
            throw new LoginException("Invalid credentials.");
        }
        return Optional.empty();
    }
    
    public void logout() {
        // Clear active token
        try {
            String username = JwtUtils.getUsername(AuthSession.getToken());
            if (username != null) {
                Map<String, String> tokens = TokenStorage.loadTokens();
                tokens.remove(username);
                TokenStorage.saveAllTokens(tokens);
            }

            // Clear in-memory session
            AuthSession.clear();
        } catch (Exception e) {
            System.err.println("Failed to clear stored token: " + e.getMessage());
        }
    }

    public Optional<Gym> getGymById(Long gymId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + gymId))
                .header("Authorization", "Bearer " + AuthSession.getToken())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return Optional.of(mapper.readValue(response.body(), Gym.class));
        }
        return Optional.empty();
    }

    public static class LoginException extends Exception {
        public LoginException(String message) {
            super(message);
        }
    }
}
