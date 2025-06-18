package com.betabase.services;

import com.betabase.models.Gym;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

public class GymApiService {

    private static final String BASE_URL = "http://localhost:8080/api/auth";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

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

    public Optional<String> login(String username, String password) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(Map.of("username", username, "password", password));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode json = mapper.readTree(response.body());
            return Optional.of(json.get("token").asText());
        } else {
            return Optional.empty();
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
}
