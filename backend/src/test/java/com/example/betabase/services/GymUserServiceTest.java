package com.example.betabase.services;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymLogin;
import com.example.betabase.repositories.GymLoginRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GymUserServiceTest {

    private GymLoginRepository gymUserRepository;
    private PasswordEncoder passwordEncoder;
    private GymService gymService;
    private GymLoginService gymUserService;

    private Gym gym;

    @BeforeEach
    void setup() {
        gymUserRepository = mock(GymLoginRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        gymService = mock(GymService.class);
        gymUserService = new GymLoginService(gymUserRepository, passwordEncoder, gymService);


        gym = new Gym();
        gym.setId(1L);
        gym.setName("Test Gym");
        gym.setUserSince(LocalDate.now());
    }

    @Test
    void testRegister_GymExists() {
        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(gym);
        request.setUsername("username");
        request.setPassword("password");

        when(gymService.getByName(gym.getName())).thenReturn(Optional.of(gym));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("password");
        when(gymUserRepository.save(any(GymLogin.class))).then(new Answer<GymLogin>() {
            public GymLogin answer(InvocationOnMock invocation) throws Throwable {
                GymLogin user = new GymLogin();
                user.setGym(gym);
                return user;
            }
        });

        GymLogin registered = gymUserService.register(request);
        assertNotNull(registered);
        assertEquals(1L, registered.getGym().getId());
    }

    @Test
    void testRegister_NewGym() {
        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(gym);
        request.setUsername("username");
        request.setPassword("password");

        when(gymService.getByName(gym.getName())).thenReturn(Optional.empty());
        when(gymService.save(any(Gym.class))).thenReturn(gym);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("password");
        when(gymUserRepository.save(any(GymLogin.class))).then(new Answer<GymLogin>() {
            public GymLogin answer(InvocationOnMock invocation) throws Throwable {
                GymLogin user = new GymLogin();
                user.setGym(gym);
                return user;
            }
        });

        GymLogin registered = gymUserService.register(request);
        assertNotNull(registered);
        assertEquals(1L, registered.getGym().getId());
    }

    @Test
    void testAuthenticate_Valid() {
        String username = "username";
        String password = "password";

        GymLogin user = new GymLogin();
        user.setPasswordHash(password);

        when(gymUserRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(password), anyString())).thenReturn(true);

        Optional<GymLogin> result = gymUserService.authenticate(username, password);
        assertTrue(result.isPresent());
        assertEquals("password", result.get().getPasswordHash());
    }

    @Test
    void testAuthenticate_InvalidUsername() {
        String username = "username";
        String password = "password";

        when(gymUserRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        Optional<GymLogin> result = gymUserService.authenticate(username, password);
        assertFalse(result.isPresent());
        // Verify no password check was done
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        String username = "user";
        String password = "wrongPass";

        GymLogin user = new GymLogin();
        user.setUsername(username);
        user.setPasswordHash("hashedPass");

        when(gymUserRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "hashedPass")).thenReturn(false);

        Optional<GymLogin> result = gymUserService.authenticate(username, password);
        assertFalse(result.isPresent());
    }

/*
    @Test
    void testGetGymById_NotFound() {
        when(gymUserRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Gym> result = gymUserService.getById(99L);
        assertFalse(result.isPresent());
    }*/
}
