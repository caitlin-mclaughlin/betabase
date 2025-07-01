package com.example.betabase.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.betabase.models.GymLogin;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String base64SecretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;
    
    @PostConstruct
    public void debug() {
        System.out.println("Injected secret: " + base64SecretKey);
        System.out.println("Injected expirationMs: " + expirationMs);
    }

    private Key getSignKey() {
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(GymLogin user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("gymId", user.getGym().getId())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid JWT: " + e.getMessage());
        }
    }
}
