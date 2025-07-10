package com.example.betabase.controllers;

import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.exceptions.DuplicateUsernameException;
import com.example.betabase.dtos.GymLoginRequestDto;
import com.example.betabase.models.Address;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymGroup;
import com.example.betabase.models.GymLogin;
import com.example.betabase.repositories.GymGroupRepository;
import com.example.betabase.repositories.GymLoginRepository;
import com.example.betabase.repositories.GymRepository;
import com.example.betabase.security.JwtService;
import com.example.betabase.services.GymGroupService;
import com.example.betabase.services.GymLoginService;
import com.example.betabase.services.GymService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import({AuthControllerTest.TestConfig.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GymLoginService gymLoginService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    private Gym mockGym;
    private GymLogin mockUser;

    @BeforeEach
    void setup() {
        Address address = new Address("123 Address St", "Madison", "WI", "53703", "USA");

        GymGroup mockGroup = new GymGroup();
        mockGroup.setId(100L);
        mockGroup.setName("Test Group");

        mockGym = new Gym();
        mockGym.setId(1L);
        mockGym.setName("BetaBase");
        mockGym.setAddress(address);
        mockGym.setUserSince(LocalDate.of(2023, 1, 1));
        mockGym.setGroup(mockGroup);

        mockUser = new GymLogin();
        mockUser.setId(200L);
        mockUser.setUsername("betabase");
        mockUser.setPasswordHash("hashed-password");
        mockUser.setGym(mockGym);
        mockUser.setGroup(mockGroup); // Optional, but safe

        when(gymLoginService.register(any(GymRegistrationRequest.class))).thenReturn(mockUser);
        when(gymLoginService.getByUsername(eq("betabase"))).thenReturn(java.util.Optional.of(mockUser));
        when(jwtService.generateToken(any())).thenReturn("mock-jwt");
    }

    @Test
    void whenLoginWithBlankFields_thenReturns400() throws Exception {
        GymLoginRequestDto request = new GymLoginRequestDto();
        request.setUsername("");
        request.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Username is required")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Password is required")));
    }

    @Test
    void whenRegisterWithInvalidPassword_thenReturns400() throws Exception {
        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(mockGym);
        request.setUsername("betabase");
        request.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Password must be at least 6 characters")));
    }

    @Test
    void whenValidRegistration_thenReturns201AndJwt() throws Exception {
        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(mockGym);
        request.setUsername("betabase");
        request.setPassword("securepassword");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gymGroupId").value(100))
                .andExpect(jsonPath("$.gymId").value(1))
                .andExpect(jsonPath("$.gymLoginId").value(200))
                .andExpect(jsonPath("$.gymName").value("BetaBase"))
                .andExpect(jsonPath("$.username").value("betabase"))
                .andExpect(jsonPath("$.token").value("mock-jwt"));
    }

    @Test
    void whenValidLogin_thenReturns200AndJwt() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        
        GymLoginRequestDto request = new GymLoginRequestDto();
        request.setUsername("betabase");
        request.setPassword("securepassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt"));
    }

    @Test
    void whenRegisterWithMissingGym_thenReturns400() throws Exception {
        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(null); // missing gym
        request.setUsername("betabase");
        request.setPassword("securepassword");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Gym info is required")));
    }

    @Test
    void whenRegisterWithMissingUsername_thenReturns400() throws Exception {
        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(mockGym);
        request.setUsername(""); // missing username
        request.setPassword("securepassword");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Username is required")));
    }

    @Test
    void whenRegisterWithMissingPassword_thenReturns400() throws Exception {
        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(mockGym);
        request.setUsername("betabase");
        request.setPassword(""); // missing password

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Password is required")));
    }

    @Test
    void whenRegisterWithPasswordTooShort_thenReturns400() throws Exception {
        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(mockGym);
        request.setUsername("betabase");
        request.setPassword("12345"); // too short

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Password must be at least 6 characters")));
    }

    @Test
    void whenRegisterWithExistingUsername_thenReturnsConflict() throws Exception {
        // Simulate gymLoginService.register throwing an exception for duplicate username
        when(gymLoginService.register(any(GymRegistrationRequest.class)))
                .thenThrow(new DuplicateUsernameException("Username 'betabase' already exists"));

        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setGym(mockGym);
        request.setUsername("betabase");
        request.setPassword("securepassword");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("already exists")));
    }

    @Test
    void whenLoginWithIncorrectPassword_thenReturns401() throws Exception {
        when(authManager.authenticate(any())).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad Credentials"));

        GymLoginRequestDto request = new GymLoginRequestDto();
        request.setUsername("betabase");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Bad Credentials")));
    }

    @Test
    void whenLoginWithMissingUsername_thenReturns400() throws Exception {
        GymLoginRequestDto request = new GymLoginRequestDto();
        request.setUsername("");
        request.setPassword("securepassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Username is required")));
    }

    @Test
    void whenLoginWithMissingPassword_thenReturns400() throws Exception {
        GymLoginRequestDto request = new GymLoginRequestDto();
        request.setUsername("betabase");
        request.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Password is required")));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        @Primary
        public GymLoginService gymLoginService() {
            return Mockito.mock(GymLoginService.class);
        }

        @Bean
        @Primary
        public GymGroupService gymGroupService() {
            return Mockito.mock(GymGroupService.class);
        }

        @Bean
        @Primary
        public GymService gymService() {
            return Mockito.mock(GymService.class);
        }

        @Bean
        @Primary
        public GymLoginRepository gymLoginRepository() {
            return Mockito.mock(GymLoginRepository.class);
        }

        @Bean
        @Primary
        public GymGroupRepository gymGroupRepository() {
            return Mockito.mock(GymGroupRepository.class);
        }

        @Bean
        @Primary
        public GymRepository gymRepository() {
            return Mockito.mock(GymRepository.class);
        }

        @Bean
        @Primary
        public PasswordEncoder passwordEncoder() {
            return Mockito.mock(PasswordEncoder.class);
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
