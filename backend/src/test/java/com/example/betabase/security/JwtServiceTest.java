package com.example.betabase.security;

import com.example.betabase.models.Gym;
import com.example.betabase.models.GymLogin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private GymLogin mockUser;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Inject dummy base64 key
        String dummySecret = Base64.getEncoder().encodeToString("testsecretkey-testsecretkey-1234".getBytes());
        Field secretField = JwtService.class.getDeclaredField("base64SecretKey");
        secretField.setAccessible(true);
        secretField.set(jwtService, dummySecret);

        // Inject expiration manually (1 hour = 3600000 ms)
        Field expirationField = JwtService.class.getDeclaredField("expirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 3600000L);

        mockUser = new GymLogin();
        mockUser.setUsername("testuser");
        Gym gym = new Gym();
        gym.setId(1L); // must not be null
        mockUser.setGym(gym);
    }

    @Test
    void generateAndValidateToken() {
        String token = jwtService.generateToken(mockUser);
        assertNotNull(token);
        
        // These should now work
        assertEquals("testuser", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, "testuser"));
    }

    @Test
    void tokenIsInvalidForWrongUsername() {
        String token = jwtService.generateToken(mockUser);
        assertFalse(jwtService.isTokenValid(token, "wronguser"));
    }
}
