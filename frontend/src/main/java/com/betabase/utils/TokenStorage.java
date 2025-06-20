package com.betabase.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenStorage {
    private static final Path FILE = Paths.get(System.getProperty("user.home"), ".betabase_tokens.json");
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void saveToken(String username, String token) throws IOException {
        Map<String, String> tokens = loadTokens();
        tokens.put(username, token);
        mapper.writeValue(FILE.toFile(), tokens);
    }

    public static Map<String, String> loadTokens() throws IOException {
        if (!Files.exists(FILE)) return new HashMap<>();

        Map<String, String> all = mapper.readValue(FILE.toFile(), Map.class);
        Map<String, String> valid = new HashMap<>();

        for (Map.Entry<String, String> entry : all.entrySet()) {
            String token = entry.getValue();
            if (!JwtUtils.isTokenExpired(token)) {
                valid.put(entry.getKey(), token);
            }
        }

        return valid;
    }

    public static String getTokenForUser(String username) throws IOException {
        return loadTokens().get(username);
    }
}
