package com.betabase.services;

import com.betabase.models.Member;
import com.betabase.utils.AuthSession;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class MemberApiService {

    private static final String BASE_URL = "http://localhost:8080/api/members";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public MemberApiService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public Member getMemberById(Long id) throws Exception {

        String token = AuthSession.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .header("Accept", "application/json")
            .   header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), Member.class);
        } else {
            return null;
        }
    }

    public boolean createMember(Member member) throws Exception {
        String body = mapper.writeValueAsString(member);

        String token = AuthSession.getToken();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200 || response.statusCode() == 201;
    }

    public boolean updateMember(Member member) throws Exception {
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

}

