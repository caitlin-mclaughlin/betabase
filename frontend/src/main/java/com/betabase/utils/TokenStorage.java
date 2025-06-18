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
        return mapper.readValue(FILE.toFile(), Map.class);
    }

    public static String getTokenForUser(String username) throws IOException {
        return loadTokens().get(username);
    }
}
