package com.example.betabase.security;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymUser;

import java.lang.reflect.Field;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private GymUser mockUser;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        jwtService.debug();

        // Inject dummy key for testing (32 bytes = 256 bits, required by HS256)
        String dummySecret = Base64.getEncoder().encodeToString("testsecretkey-testsecretkey-1234".getBytes());

        Field field = JwtService.class.getDeclaredField("base64SecretKey");
        field.setAccessible(true);
        field.set(jwtService, dummySecret);

        mockUser = new GymUser();
        mockUser.setUsername("testuser");
        mockUser.setGym(new Gym());
        mockUser.getGym().setId(1L);
    }

    @Test
    void generateAndValidateToken() {
        String token = jwtService.generateToken(mockUser);
        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, "testuser"));
    }

    @Test
    void tokenIsInvalidForWrongUsername() {
        String token = jwtService.generateToken(mockUser);
        assertFalse(jwtService.isTokenValid(token, "wronguser"));
    }
}
