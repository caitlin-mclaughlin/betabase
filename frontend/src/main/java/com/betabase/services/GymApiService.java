package com.betabase.services;

import com.betabase.models.Gym;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GymApiService {

    private static final String BASE_URL = "http://localhost:8080/api/gyms";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public boolean registerGym(Gym gym, String username, String password) throws Exception {
        String body = mapper.writeValueAsString(new GymRegistrationRequest(gym, username, password));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 201;
    }

    // Nested static DTO class
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
}
