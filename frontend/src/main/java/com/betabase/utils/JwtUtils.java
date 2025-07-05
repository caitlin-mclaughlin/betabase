package com.betabase.utils;

import com.betabase.dtos.JwtResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

public class JwtUtils {

    // Decode the JWT payload as a Map
    public static Map<String, Object> decodePayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT structure");
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payloadJson, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    // Check if the token is expired based on the "exp" claim
    public static boolean isTokenExpired(String token) {
        Map<String, Object> payload = decodePayload(token);
        if (payload.containsKey("exp")) {
            long expSeconds = ((Number) payload.get("exp")).longValue();
            return expSeconds * 1000 < System.currentTimeMillis();
        }
        return true; // If no "exp" claim, treat as expired
    }

    // Get username from the token
    public static String getUsername(String token) {
        Map<String, Object> payload = decodePayload(token);
        Object sub = payload.get("sub"); // "sub" is typically the username
        return sub != null ? sub.toString() : null;
    }

    // Optional: get custom claim like gymId from JWT if your backend includes it
    public static Long getGymId(String token) {
        Map<String, Object> payload = decodePayload(token);
        Object gymId = payload.get("gymId");
        return gymId instanceof Number ? ((Number) gymId).longValue() : null;
    }

    public static JwtResponseDto extractJwtDetails(String token) {
        Map<String, Object> payload = decodePayload(token);

        JwtResponseDto dto = new JwtResponseDto();
        dto.setToken(token);
        dto.setUsername((String) payload.get("sub"));
        dto.setGymId(payload.get("gymId") instanceof Number ? ((Number) payload.get("gymId")).longValue() : null);
        dto.setGymName((String) payload.get("gymName"));
        dto.setExpiresAt(payload.get("exp") instanceof Number ? ((Number) payload.get("exp")).longValue() * 1000 : null);
        return dto;
    }

}
