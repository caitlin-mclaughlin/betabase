package com.example.betabase.controllers;

import com.example.betabase.dtos.GymLoginCreateDto;
import com.example.betabase.dtos.GymRegistrationRequest;
import com.example.betabase.enums.GymLoginRole;
import com.example.betabase.models.Address;
import com.example.betabase.models.Gym;
import com.example.betabase.models.GymGroup;
import com.example.betabase.models.GymLogin;
import com.example.betabase.security.JwtService;
import com.example.betabase.services.GymGroupService;
import com.example.betabase.services.GymLoginService;
import com.example.betabase.services.GymService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GymLoginController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GymLoginControllerTest.TestConfig.class)
class GymLoginControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private GymLoginService gymLoginService;
    @Autowired private GymService gymService;
    @Autowired private GymGroupService gymGroupService;

    private GymLogin login;
    private Gym gym;
    private GymGroup group;

    @BeforeEach
    void setUp() {
        login = new GymLogin();
        login.setId(1L);
        login.setUsername("frontdesk");
        login.setPasswordHash("hashedpw");
        login.setRole(GymLoginRole.STAFF);

        gym = new Gym();
        gym.setId(10L);

        group = new GymGroup();
        group.setId(20L);

        login.setGym(gym);
        login.setGroup(group);
    }

    @Test
    void testGetGymLoginById_returns200() throws Exception {
        when(gymLoginService.getById(1L)).thenReturn(Optional.of(login));

        mockMvc.perform(get("/api/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("frontdesk"))
            .andExpect(jsonPath("$.role").value("STAFF"))
            .andExpect(jsonPath("$.gymId").value(10L))
            .andExpect(jsonPath("$.gymGroupId").value(20L));
    }

    @Test
    void testGetGymLoginById_notFound_returns404() throws Exception {
        when(gymLoginService.getById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreateGymLogin_valid_returns201() throws Exception {
        GymLoginCreateDto dto = new GymLoginCreateDto("frontdesk", "hashedpw", 10L, 20L, GymLoginRole.STAFF);

        when(gymService.getById(10L)).thenReturn(Optional.of(gym));
        when(gymGroupService.getById(20L)).thenReturn(Optional.of(group));
        when(gymLoginService.save(Mockito.any())).thenReturn(login);

        mockMvc.perform(post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("frontdesk"))
            .andExpect(jsonPath("$.gymId").value(10L))
            .andExpect(jsonPath("$.gymGroupId").value(20L));
    }

    @Test
    void testCreateGymLogin_missingGroup_returns400() throws Exception {
        GymLoginCreateDto dto = new GymLoginCreateDto("frontdesk", "hashedpw", 10L, 20L, GymLoginRole.STAFF);

        when(gymService.getById(10L)).thenReturn(Optional.of(gym));
        when(gymGroupService.getById(20L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    void testRegisterGym_valid_returns201() throws Exception {
        // Setup valid group and gym
        GymGroup group = new GymGroup();
        group.setId(1L);
        group.setName("BetaBase Group");

        Gym gym = new Gym();
        gym.setId(1L);
        gym.setName("Downtown Beta");
        gym.setUserSince(LocalDate.of(2024, 1, 1));
        gym.setAddress(new Address("123", "Main St", "City", "ST", "12345", "Country"));
        gym.setGroup(group);

        GymLogin login = new GymLogin();
        login.setId(1L);
        login.setUsername("frontdesk");
        login.setPasswordHash("hashedpw");
        login.setGym(gym);
        login.setGroup(group);
        login.setRole(GymLoginRole.ADMIN);

        GymRegistrationRequest request = new GymRegistrationRequest();
        request.setUsername("frontdesk");
        request.setPassword("hashedpw");
        request.setGym(gym);

        when(gymService.save(Mockito.any())).thenReturn(gym);
        when(gymLoginService.save(Mockito.any())).thenReturn(login);

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("frontdesk"))
            .andExpect(jsonPath("$.role").value("ADMIN"))
            .andExpect(jsonPath("$.gymId").value(1));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public GymLoginService gymLoginService() {
            return Mockito.mock(GymLoginService.class);
        }

        @Bean
        @Primary
        public GymService gymService() {
            return Mockito.mock(GymService.class);
        }

        @Bean
        @Primary
        public GymGroupService gymGroupService() {
            return Mockito.mock(GymGroupService.class);
        }

        @Bean
        @Primary
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }
}
