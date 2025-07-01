package com.betabase.services;

import com.betabase.models.Gym;
import com.betabase.models.Member;
import com.betabase.utils.AuthSession;
import com.betabase.utils.JwtUtils;
import com.betabase.utils.TokenStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

public class GymApiService {

    private static final String BASE_URL = "http://localhost:8080/api/auth";
    
    private final HttpClient client;
    private final ObjectMapper mapper;

    public GymApiService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String registerGym(Gym gym, String username, String password) throws Exception {
        String body = mapper.writeValueAsString(new GymRegistrationRequest(gym, username, password));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
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

    public Optional<String> login(String username, String password) throws IOException, InterruptedException, LoginException {
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(Map.of("username", username, "password", password));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode json = mapper.readTree(response.body());
            return Optional.of(json.get("token").asText());
        } else if (response.statusCode() == 401) {
            throw new LoginException("Invalid username or password");
        } else {
            System.out.println("Login failed: " + response.statusCode());
            return Optional.empty();
        }
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

    public Gym getGymById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), Gym.class);
        } else {
            return null;
        }
    }

    public static class GymRegistrationRequest {
        public Gym gym;
        public String username;
        public String password;

        public GymRegistrationRequest(Gym gym, String username, String password) {
            this.gym = gym;
            this.username = username;
            this.password = password;
        }
    }

    public static class LoginRequest {
        public String username;
        public String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class LoginException extends Exception {
        public LoginException(String message) {
            super(message);
        }
    }
}
