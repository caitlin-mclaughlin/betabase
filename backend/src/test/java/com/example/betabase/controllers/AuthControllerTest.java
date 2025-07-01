package com.example.betabase.controllers;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.dtos.LoginRequest;
import com.example.betabase.models.Address;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymLogin;
import com.example.betabase.security.JwtService;
import com.example.betabase.services.GymLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.hamcrest.Matchers;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import({AuthControllerTest.TestConfig.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GymLoginService gymUserService;

    @Autowired
    private JwtService jwtService;

    private Gym mockGym;

    @BeforeEach
    void defineGym() {
        Address address = new Address("123", "Address St", "Madison", "WI", "53703", "USA");
        mockGym = new Gym();
        mockGym.setId(1L);
        mockGym.setName("BetaBase");
        mockGym.setAddress(address);
        mockGym.setUserSince(LocalDate.of(2023, 1, 1));

        GymLogin mockUser = new GymLogin();
        mockUser.setId(1L);
        mockUser.setUsername("betabase");
        mockUser.setPasswordHash("hashed-password");
        mockUser.setGym(mockGym);

        when(gymUserService.register(any(GymRegistrationRequest.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any())).thenReturn("mock-jwt");
    }

    @Test
    void whenLoginWithBlankFields_thenReturns400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("");  // Invalid
        request.setPassword("");  // Invalid

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("username: Username is required")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("password: Password is required")));

    }

    @Test
    void whenRegisterWithInvalidPassword_thenReturns400() throws Exception {
        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(mockGym);
        request.setUsername("betabase");
        request.setPassword("123"); // too short

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Password must be at least 6 characters")));

    }

    @Test
    void whenValidRegistration_thenReturns201() throws Exception {
        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(mockGym);
        request.setUsername("betabase");
        request.setPassword("securepassword");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // Nested static test config to provide mocks instead of @MockBean
    @TestConfiguration
    static class TestConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        @Primary  // override any existing bean of this type
        public GymLoginService gymUserService() {
            return Mockito.mock(GymLoginService.class);
        }

        @Bean
        @Primary
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }

        @Bean
        @Primary
        public AuthenticationManager authenticationManager() {
            return Mockito.mock(AuthenticationManager.class);
        }
    }
}
