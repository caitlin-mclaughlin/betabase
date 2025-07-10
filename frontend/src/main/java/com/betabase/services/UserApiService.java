package com.betabase.services;

import com.betabase.models.User;
import com.betabase.utils.AuthSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.application.Platform;

import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.net.URI;
import java.net.URLEncoder;

public class UserApiService {
    private final String baseUrl;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public UserApiService(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public User getMemberById(Long id) throws Exception {

        String token = AuthSession.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + id))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), User.class);
        } else {
            return null;
        }
    }

    public User createUser(User user) throws Exception {
        String json = mapper.writeValueAsString(user);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl))
            .header("Authorization", "Bearer " + AuthSession.getToken())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return mapper.readValue(response.body(), User.class);
       
        } else {
            throw new RuntimeException("Failed to create user");
        }
    }

    public User updateUser(User user) throws Exception {
        if (user.getId() == null) {
            throw new IllegalArgumentException("Cannot update user with null ID");
        }

        String requestBody = mapper.writeValueAsString(user);

        String token = AuthSession.getToken();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + user.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), User.class);
       
        } else {
            throw new RuntimeException("Failed to update user");
        }
    }

    public List<User> searchUsers(String query) throws Exception {
        
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/search?query=" + encoded))
                .header("Authorization", "Bearer " + AuthSession.getToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<>() {});
        } else {
            System.err.println("Search failed with code: " + response.statusCode());
            return null;
        }
    }

    public void searchUsersAsync(String query, Consumer<List<User>> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                List<User> result = searchUsers(query);
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }
}
