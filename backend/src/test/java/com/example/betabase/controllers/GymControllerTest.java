package com.example.betabase.controllers;

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

import com.example.betabase.models.Address;
import com.example.betabase.models.Gym;
import com.example.betabase.security.JwtService;
import com.example.betabase.services.GymGroupService;
import com.example.betabase.services.GymLoginService;
import com.example.betabase.services.GymService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GymController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GymControllerTest.TestConfig.class)
class GymControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private GymService gymService;

    private Gym gym;

    @BeforeEach
    void setUp() {
        gym = new Gym();
        gym.setId(1L);
        gym.setName("Downtown Beta");
        gym.setAddress(new Address());
    }

    @Test
    void testGetGymById_found_returns200() throws Exception {
        when(gymService.getById(1L)).thenReturn(Optional.of(gym));

        mockMvc.perform(get("/api/gyms/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Downtown Beta"));
    }

    @Test
    void testGetGymById_notFound_returns404() throws Exception {
        when(gymService.getById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/gyms/1"))
            .andExpect(status().isNotFound());
    }

    @TestConfiguration
    static class TestConfig {
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
        public GymLoginService gymLoginService() {
            return Mockito.mock(GymLoginService.class);
        }

        @Bean
        @Primary
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }
}
