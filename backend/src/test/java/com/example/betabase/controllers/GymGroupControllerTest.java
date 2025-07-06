package com.example.betabase.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import com.example.betabase.models.Gym;
import com.example.betabase.models.GymGroup;
import com.example.betabase.security.JwtService;
import com.example.betabase.services.GymGroupService;
import com.example.betabase.services.GymService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GymGroupController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GymGroupControllerTest.TestConfig.class)
class GymGroupControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private GymGroupService gymGroupService;

    private GymGroup group;

    @BeforeEach
    void setUp() {
        group = new GymGroup();
        group.setId(1L);
        group.setName("BetaBase Group");

        Gym gym = new Gym();
        gym.setId(10L);
        List<Gym> gyms = new ArrayList<>();
        gyms.add(gym);
        group.setGyms(gyms);
    }

    @Test
    void testGetGymGroupById_found_returns200() throws Exception {
        when(gymGroupService.getById(1L)).thenReturn(Optional.of(group));

        mockMvc.perform(get("/api/gym-groups/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("BetaBase Group"));
    }

    @Test
    void testGetGymGroupById_notFound_returns404() throws Exception {
        when(gymGroupService.getById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/gym-groups/1"))
            .andExpect(status().isNotFound());
    }

    @TestConfiguration
    static class TestConfig {
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
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }
}

