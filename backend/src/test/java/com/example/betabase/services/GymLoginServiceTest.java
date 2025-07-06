package com.example.betabase.services;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.enums.GymLoginRole;
import com.example.betabase.models.Address;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymGroup;
import com.example.betabase.models.GymLogin;
import com.example.betabase.repositories.GymLoginRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GymLoginServiceTest {

    private Address address;
    private Gym gym;
    private GymGroup group;
    private GymLoginRepository repository;
    private GymService gymService;
    private GymLoginService service;
    private PasswordEncoder encoder;

    @BeforeEach
    void setup() {
        repository = mock(GymLoginRepository.class);
        gymService = mock(GymService.class);
        encoder = mock(PasswordEncoder.class);
        service = new GymLoginService(repository, encoder, gymService);

        address = new Address("123", "Main St", "Madison", "WI", "53703", "USA");
        group = new GymGroup(1L, "Test Group", null);
        gym = new Gym(null, "Climb Gym", group, address, LocalDate.now());
    }

    @Test
    void testRegisterNewGym() {
        // Arrange
        Gym savedGym = new Gym(10L, "Climb Gym", group, address, LocalDate.now());

        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setUsername("kiosk01");
        request.setPassword("secret123");
        request.setGym(gym);

        when(gymService.getByName("Climb Gym")).thenReturn(Optional.empty());
        when(gymService.save(gym)).thenReturn(savedGym);
        when(encoder.encode("secret123")).thenReturn("hashed_pw");

        GymLogin expectedLogin = new GymLogin(null, "kiosk01", "hashed_pw", savedGym, group, GymLoginRole.KIOSK);
        when(repository.save(any(GymLogin.class))).thenReturn(expectedLogin);

        // Act
        GymLogin result = service.register(request);

        // Assert
        assertEquals("kiosk01", result.getUsername());
        assertEquals("hashed_pw", result.getPasswordHash());
        assertEquals(10L, result.getGym().getId());
        assertEquals(group, result.getGroup());
        verify(gymService).save(gym);
        verify(repository).save(any(GymLogin.class));
    }

    @Test
    void testRegisterExistingGym() {
        // Arrange
        Gym existingGym = new Gym(5L, "Climb Gym", group, address, LocalDate.now());

        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setUsername("admin01");
        request.setPassword("secret456");
        request.setGym(gym);

        when(gymService.getByName("Climb Gym")).thenReturn(Optional.of(existingGym));
        when(encoder.encode("secret456")).thenReturn("hashed_pw_2");

        GymLogin expectedLogin = new GymLogin(null, "admin01", "hashed_pw_2", existingGym, group, GymLoginRole.ADMIN);
        when(repository.save(any(GymLogin.class))).thenReturn(expectedLogin);

        // Act
        GymLogin result = service.register(request);

        // Assert
        assertEquals("admin01", result.getUsername());
        assertEquals("hashed_pw_2", result.getPasswordHash());
        assertEquals(5L, result.getGym().getId());
        assertEquals(group, result.getGroup());
        verify(gymService, never()).save(any());
        verify(repository).save(any(GymLogin.class));
    }

    @Test
    void testAuthenticate_Valid() {
        String username = "username";
        String password = "password";

        GymLogin login = new GymLogin();
        login.setPasswordHash(password);

        when(repository.findByUsername(username)).thenReturn(Optional.of(login));
        when(encoder.matches(eq(password), anyString())).thenReturn(true);

        Optional<GymLogin> result = service.authenticate(username, password);
        assertTrue(result.isPresent());
        assertEquals("password", result.get().getPasswordHash());
    }

    @Test
    void testAuthenticate_InvalidUsername() {
        String username = "username";
        String password = "password";

        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        
        Optional<GymLogin> result = service.authenticate(username, password);
        assertFalse(result.isPresent());
        // Verify no password check was done
        verify(encoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        String username = "user";
        String password = "wrongPass";

        GymLogin login = new GymLogin();
        login.setUsername(username);
        login.setPasswordHash("hashedPass");

        when(repository.findByUsername(username)).thenReturn(Optional.of(login));
        when(encoder.matches(password, "hashedPass")).thenReturn(false);

        Optional<GymLogin> result = service.authenticate(username, password);
        assertFalse(result.isPresent());
    }
}
