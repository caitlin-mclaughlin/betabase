package com.betabase.services;

import com.betabase.models.Member;
import com.betabase.utils.AuthSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.application.Platform;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.net.URI;
import java.net.URLEncoder;

public class MemberApiService {

    private static final String BASE_URL = "http://localhost:8080/api/users/gym/members";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public MemberApiService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Member getMemberById(Long id) throws Exception {

        String token = AuthSession.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), Member.class);
        } else {
            return null;
        }
    }

    public Member createMember(Member member) throws Exception {
        String requestBody = mapper.writeValueAsString(member);

        System.out.println("\nREQUEST BODY: " + requestBody.toString());
        
        String token = AuthSession.getToken();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("STATUS: " + response.statusCode());
        System.out.println("RESPONSE BODY: " + response.body()); 

        if (response.statusCode() == 200  || response.statusCode() == 201) {
            return mapper.readValue(response.body(), Member.class);
        } else {
            System.out.println("\nDEBUG: create member failed\n");
            return null;
        }
    }

    public boolean updateMember(Member member) throws Exception {
        if (member.getId() == null) {
            throw new IllegalArgumentException("Cannot update member with null ID");
        }

        String requestBody = mapper.writeValueAsString(member);

        String token = AuthSession.getToken();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + member.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    public List<Member> searchMembers(String query) throws Exception {
        
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);

        String jwt = AuthSession.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/search?query=" + encoded))
                .GET()
                .header("Authorization", "Bearer " + jwt)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<>() {});
        } else {
            System.err.println("Search failed with code: " + response.statusCode());
            return null;
        }
    }

    public void searchMembersAsync(String query, Consumer<List<Member>> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                List<Member> result = searchMembers(query);
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }
}

