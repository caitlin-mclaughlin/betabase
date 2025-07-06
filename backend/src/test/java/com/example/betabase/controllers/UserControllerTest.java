package com.example.betabase.controllers;

import com.example.betabase.dtos.UserCreateDto;
import com.example.betabase.enums.GenderType;
import com.example.betabase.enums.PronounsType;
import com.example.betabase.models.Address;
import com.example.betabase.models.User;
import com.example.betabase.security.JwtService;
import com.example.betabase.services.UserService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UserControllerTest.TestConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private User user;
    private UserCreateDto createDto;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("1234567890");
        user.setEmail("test@example.com");
        user.setGender(GenderType.UNSET);
        user.setPronouns(PronounsType.UNSET);
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));
        user.setEmergencyContactName("EC");
        user.setEmergencyContactEmail("ec@example.com");
        user.setEmergencyContactPhone("9876543210");
        user.setAddress(new Address("123", "Main", "City", "State", "12345", "Country", "1A"));

        createDto = new UserCreateDto(
            "Test", "", "User", PronounsType.UNSET, GenderType.UNSET,
            "1234567890", "test@example.com", LocalDate.of(2000, 1, 1), user.getAddress(),
            new ArrayList<>(), "EC", "9876543210", "ec@example.com"
        );
    }

    @Test
    void testCreateUser_returns201() throws Exception {
        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Test"));
    }

    @Test
    void testGetUserById_returns200() throws Exception {
        when(userService.getById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetUserById_notFound_returns404() throws Exception {
        when(userService.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser_returns200() throws Exception {
        when(userService.getById(1L)).thenReturn(Optional.of(user));
        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void testSearchPotentialMatches_returnsList() throws Exception {
        when(userService.searchPotentialMatches("test@example.com", "1234567890"))
                .thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/search")
                .param("email", "test@example.com")
                .param("phoneNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        @Primary
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }
}
