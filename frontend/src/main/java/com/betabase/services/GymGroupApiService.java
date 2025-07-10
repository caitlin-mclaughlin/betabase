package com.betabase.services;

import com.betabase.dtos.simple.GymGroupNameDto;
import com.betabase.models.GymGroup;
import com.betabase.utils.AuthSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GymGroupApiService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl;

    public GymGroupApiService(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    /**
     * Create a new GymGroup via POST /gym-groups
     * @param gymGroup GymGroup object with at least a name set
     * @return GymGroup created with assigned ID from backend
     * @throws IOException
     * @throws InterruptedException
     */
    public GymGroup createGymGroup(GymGroup gymGroup) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(gymGroup);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/gym-groups"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + AuthSession.getToken())
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), GymGroup.class);
        } else {
            throw new IOException("Failed to create GymGroup: " + response.statusCode() + " " + response.body());
        }
    }

    /**
     * Fetch a GymGroup by ID
     * @param id GymGroup ID
     * @return GymGroup or null if not found
     * @throws IOException
     * @throws InterruptedException
     */
    public GymGroup getGymGroupById(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/gym-groups/" + id))
            .header("Authorization", "Bearer " + AuthSession.getToken())
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), GymGroup.class);
        } else if (response.statusCode() == 404) {
            return null;
        } else {
            throw new IOException("Failed to fetch GymGroup: " + response.statusCode() + " " + response.body());
        }
    }

    /**
     * Fetch all GymGroups (optional)
     * @return List of GymGroups
     * @throws IOException
     * @throws InterruptedException
     */
    public List<GymGroup> getAllGymGroups() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/gym-groups"))
            .header("Authorization", "Bearer " + AuthSession.getToken())
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<GymGroup>>() {});
        } else {
            throw new IOException("Failed to fetch GymGroups: " + response.statusCode() + " " + response.body());
        }
    }

    public List<GymGroup> getPublicGroupNames() throws IOException, InterruptedException  {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/public-names"))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch public gym group names: " + response.statusCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        GymGroupNameDto[] dtos = mapper.readValue(response.body(), GymGroupNameDto[].class);

        return Arrays.stream(dtos)
            .map(dto -> {
                GymGroup g = new GymGroup();
                g.setId(dto.id());
                g.setName(dto.name());
                return g;
            })
            .collect(Collectors.toList());
    }
}
