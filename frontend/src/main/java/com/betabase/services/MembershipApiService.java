package com.betabase.services;

import com.betabase.models.Membership;
import com.betabase.utils.AuthSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.util.List;

public class MembershipApiService {
    private final String baseUrl;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public MembershipApiService(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
    
    public Membership createMembership(Membership membership) throws Exception {
        String json = mapper.writeValueAsString(membership);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl))
            .header("Authorization", "Bearer " + AuthSession.getToken())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return mapper.readValue(response.body(), Membership.class);
        }

        throw new RuntimeException("Failed to create membership. Status: " + response.statusCode());
    }

    public Membership updateMembership(Membership membership) throws Exception {
        if (membership.getId() == null) {
            throw new IllegalArgumentException("Cannot update membership with null ID");
        }

        String requestBody = mapper.writeValueAsString(membership);

        String token = AuthSession.getToken();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + membership.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), Membership.class);
       
        } else {
            throw new RuntimeException("Failed to update membership");
        }
    }

    public List<Membership> getForGym(Long gymId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/gym/" + gymId))
            .header("Authorization", "Bearer " + AuthSession.getToken())
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<>() {});
        }

        throw new RuntimeException("Failed to fetch memberships for gym ID: " + gymId);
    }

    public Membership getForUserAndGym(Long userId, Long gymId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/gym/" + gymId + "/user/" + userId))
            .header("Authorization", "Bearer " + AuthSession.getToken())
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), Membership.class);
        } else if (response.statusCode() == 404) {
            throw new MembershipNotFoundException("Membership for user: "+userId+" does not exist at gym: "+gymId);
        }

        throw new RuntimeException("Failed to fetch membership for user ID " + userId + " and gym ID " + gymId);
    }

    public static class MembershipNotFoundException extends Exception {
        public MembershipNotFoundException(String message) {
            super(message);
        }
    }
}
