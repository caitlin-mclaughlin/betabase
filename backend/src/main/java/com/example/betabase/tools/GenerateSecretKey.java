package com.example.betabase.tools;

import io.jsonwebtoken.security.Keys;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Properties;

public class GenerateSecretKey {
    public static void main(String[] args) {
        try {
            Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
            String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

            Properties props = new Properties();
            props.setProperty("jwt.secret", encodedKey);

            try (FileOutputStream fos = new FileOutputStream("secret.properties")) {
                props.store(fos, "JWT Secret Key");
                System.out.println("Secret key saved to secret.properties");
            }
        } catch (IOException e) {
            System.err.println("Could not write key: " + e.getMessage());
        }
    }
}

