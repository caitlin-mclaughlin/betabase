package com.betabase.utils;

public class AuthSession {
    private static String jwtToken;

    public static void setToken(String token) {
        jwtToken = token;
    }

    public static String getToken() {
        return jwtToken;
    }

    public static void clear() {
        jwtToken = null;
    }
}
