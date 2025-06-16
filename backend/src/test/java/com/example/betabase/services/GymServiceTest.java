package com.example.betabase.services;

import com.example.betabase.models.Gym;
import com.example.betabase.repositories.GymRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GymServiceTest {

    private GymRepository gymRepository;
    private GymService gymService;

    @BeforeEach
    void setup() {
        gymRepository = mock(GymRepository.class);
        gymService = new GymService(gymRepository);
    }

    @Test
    void testSaveGym() {
        Gym gym = new Gym();
        gym.setName("Test Gym");
        gym.setUserSince(LocalDate.now());

        when(gymRepository.save(gym)).thenReturn(gym);

        Gym saved = gymService.save(gym);
        assertNotNull(saved);
        assertEquals("Test Gym", saved.getName());
    }

    @Test
    void testGetGymById() {
        Gym gym = new Gym();
        gym.setId(1L);
        gym.setName("Found Gym");

        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        Optional<Gym> result = gymService.getById(1L);
        assertTrue(result.isPresent());
        assertEquals("Found Gym", result.get().getName());
    }

    @Test
    void testGetGymById_NotFound() {
        when(gymRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Gym> result = gymService.getById(99L);
        assertFalse(result.isPresent());
    }
}
