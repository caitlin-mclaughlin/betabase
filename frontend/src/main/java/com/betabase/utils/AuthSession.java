package com.betabase.utils;

public class AuthSession {
    private static String token;
    private static Long currentGymId;
    private static String currentGymName;
    private static String currentMembername;

    public static void setSession(String jwt, Long gymId, String gymName, String username) {
        token = jwt;
        currentGymId = gymId;
        currentGymName = gymName;
        currentMembername = username;
    }

    public static String getToken() { return token; }
    
    public static void setToken(String jwtToken) { token = jwtToken; }

    public static boolean isLoggedIn() {
        return token != null && !JwtUtils.isTokenExpired(token);
    }

    public static Long getCurrentGymId() { return currentGymId; }

    public static String getCurrentGymName() { return currentGymName; }

    public static String getCurrentMembername() { return currentMembername; }

    public static void clear() {
        token = null;
        currentGymId = null;
        currentGymName = null;
        currentMembername = null;
    }
}
