package com.example.betabase.security;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymLogin;

import java.lang.reflect.Field;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=${JWT_SECRET}",
    "jwt.expiration-ms=${JWT_EXPIRATION_MS}",
    "spring.jpa.hibernate.ddl-auto=none"
})
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;
    
    private GymLogin mockUser;

    @BeforeEach
    void setUp() throws Exception {
        // Inject dummy key for testing (32 bytes = 256 bits, required by HS256)
        String dummySecret = Base64.getEncoder().encodeToString("testsecretkey-testsecretkey-1234".getBytes());

        Field field = JwtService.class.getDeclaredField("base64SecretKey");
        field.setAccessible(true);
        field.set(jwtService, dummySecret);

        mockUser = new GymLogin();
        mockUser.setUsername("testuser");
        mockUser.setGym(new Gym());
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
