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

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GymLoginServiceTest {

    private GymLoginRepository repo;
    private GymService gymService;
    private PasswordEncoder encoder;
    private GymLoginService service;

    @BeforeEach
    void setup() {
        repo = mock(GymLoginRepository.class);
        gymService = mock(GymService.class);
        encoder = mock(PasswordEncoder.class);
        service = new GymLoginService(repo, encoder, gymService);
    }

    @Test
    void testRegisterNewGym() {
        // Arrange
        Address address = new Address("123", "Main St", "Madison", "WI", "53703", "USA");
        GymGroup group = new GymGroup(1L, "Test Group", null);
        Gym gym = new Gym(null, "Climb Gym", group, address, LocalDate.now());
        Gym savedGym = new Gym(10L, "Climb Gym", group, address, LocalDate.now());

        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setUsername("kiosk01");
        request.setPassword("secret123");
        request.setGym(gym);

        when(gymService.getByName("Climb Gym")).thenReturn(Optional.empty());
        when(gymService.save(gym)).thenReturn(savedGym);
        when(encoder.encode("secret123")).thenReturn("hashed_pw");

        GymLogin expectedLogin = new GymLogin(null, "kiosk01", "hashed_pw", savedGym, group, GymLoginRole.KIOSK);
        when(repo.save(any(GymLogin.class))).thenReturn(expectedLogin);

        // Act
        GymLogin result = service.register(request);

        // Assert
        assertEquals("kiosk01", result.getUsername());
        assertEquals("hashed_pw", result.getPasswordHash());
        assertEquals(10L, result.getGym().getId());
        assertEquals(group, result.getGroup());
        verify(gymService).save(gym);
        verify(repo).save(any(GymLogin.class));
    }

    @Test
    void testRegisterExistingGym() {
        // Arrange
        Address address = new Address("123", "Main St", "Madison", "WI", "53703", "USA");
        GymGroup group = new GymGroup(1L, "Test Group", null);
        Gym gym = new Gym(null, "Climb Gym", group, address, LocalDate.now());
        Gym existingGym = new Gym(5L, "Climb Gym", group, address, LocalDate.now());

        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setUsername("admin01");
        request.setPassword("secret456");
        request.setGym(gym);

        when(gymService.getByName("Climb Gym")).thenReturn(Optional.of(existingGym));
        when(encoder.encode("secret456")).thenReturn("hashed_pw_2");

        GymLogin expectedLogin = new GymLogin(null, "admin01", "hashed_pw_2", existingGym, group, GymLoginRole.ADMIN);
        when(repo.save(any(GymLogin.class))).thenReturn(expectedLogin);

        // Act
        GymLogin result = service.register(request);

        // Assert
        assertEquals("admin01", result.getUsername());
        assertEquals("hashed_pw_2", result.getPasswordHash());
        assertEquals(5L, result.getGym().getId());
        assertEquals(group, result.getGroup());
        verify(gymService, never()).save(any());
        verify(repo).save(any(GymLogin.class));
    }
}
