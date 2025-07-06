package com.example.betabase.services;

import com.example.betabase.models.User;
import com.example.betabase.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepo;
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        userRepo = mock(UserRepository.class);
        userService = new UserService(userRepo);

        user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
    }

    @Test
    void testSaveUser() {
        when(userRepo.save(user)).thenReturn(user);
        User saved = userService.save(user);
        assertNotNull(saved);
        assertEquals("Test", saved.getFirstName());
    }

    @Test
    void testGetById() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getFirstName());
    }

    @Test
    void testGetById_NotFound() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        Optional<User> result = userService.getById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByEmail() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        Optional<User> result = userService.getByEmail("test@example.com");
        assertTrue(result.isPresent());
    }

    @Test
    void testSearchUsers() {
        when(userRepo.findByQueryAndGymGroupId("Test", 1L)).thenReturn(List.of(user));
        List<User> results = userService.searchUsers("Test", 1L);
        assertEquals(1, results.size());
        assertEquals("Test", results.get(0).getFirstName());
    }

    @Test
    void testSearchPotentialMatches() {
        when(userRepo.findByEmailOrPhoneNumber("test@example.com", "1234567890"))
            .thenReturn(List.of(user));
        List<User> matches = userService.searchPotentialMatches("test@example.com", "1234567890");
        assertEquals(1, matches.size());
    }
}
