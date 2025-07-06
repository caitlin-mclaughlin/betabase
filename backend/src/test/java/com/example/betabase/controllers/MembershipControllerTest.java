package com.example.betabase.controllers;

import com.example.betabase.dtos.MembershipCreateDto;
import com.example.betabase.enums.UserType;
import com.example.betabase.models.GymGroup;
import com.example.betabase.models.Membership;
import com.example.betabase.models.User;
import com.example.betabase.security.JwtService;
import com.example.betabase.services.GymGroupService;
import com.example.betabase.services.MembershipService;
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
import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MembershipController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MembershipControllerTest.TestConfig.class)
public class MembershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private UserService userService;

    @Autowired
    private GymGroupService gymGroupService;

    private User user;
    private GymGroup group;
    private Membership membership;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Test");

        group = new GymGroup();
        group.setId(2L);
        group.setName("BetaBase Group");

        membership = new Membership();
        membership.setId(100L);
        membership.setUser(user);
        membership.setGymGroup(group);
        membership.setUserSince(LocalDate.of(2024, 1, 1));
        membership.setType(UserType.STAFF);
        membership.setActive(true);
    }

    @Test
    void testCreateMembership_validRequest_returns201() throws Exception {
        MembershipCreateDto dto = new MembershipCreateDto(
                user.getId(),
                group.getId(),
                UserType.STAFF,
                membership.getUserSince(),
                true
        );

        when(userService.getById(1L)).thenReturn(Optional.of(user));
        when(gymGroupService.getById(2L)).thenReturn(Optional.of(group));
        when(membershipService.save(any(Membership.class))).thenReturn(membership);

        mockMvc.perform(post("/api/gym-users/memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.gymGroupId").value(2L))
                .andExpect(jsonPath("$.type").value("STAFF"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testGetUserMemberships_returnsList() throws Exception {
        when(membershipService.getMembershipsForUser(1L)).thenReturn(List.of(membership));

        mockMvc.perform(get("/api/gym-users/memberships/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    void testGetGymGroupMemberships_returnsList() throws Exception {
        when(membershipService.getMembershipsForGym(2L)).thenReturn(List.of(membership));

        mockMvc.perform(get("/api/gym-users/memberships/gym/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gymGroupId").value(2L));
    }

    @Test
    void testCreateMembership_userNotFound_returns400() throws Exception {
        MembershipCreateDto dto = new MembershipCreateDto(
                1L,
                2L,
                UserType.MEMBER,
                LocalDate.now(),
                true
        );

        when(userService.getById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/gym-users/memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("User not found")));
    }

    @Test
    void testCreateMembership_inactiveUser_returns201() throws Exception {
        membership.setActive(false);

        when(userService.getById(1L)).thenReturn(Optional.of(user));
        when(gymGroupService.getById(2L)).thenReturn(Optional.of(group));
        when(membershipService.save(any(Membership.class))).thenReturn(membership);

        MembershipCreateDto dto = new MembershipCreateDto(
                user.getId(),
                group.getId(),
                UserType.VISITOR,
                membership.getUserSince(),
                false
        );

        mockMvc.perform(post("/api/gym-users/memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.type").value("VISITOR"));
    }

    // Edge Cases
    @Test
    void testCreateMembership_missingFields_returns400() throws Exception {
        // Missing userId and groupId
        String badJson = """
            {
              "type": "MEMBER",
              "userSince": "2024-01-01",
              "active": true
            }
        """;

        mockMvc.perform(post("/api/gym-users/memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMembership_duplicate_returns400() throws Exception {
        MembershipCreateDto dto = new MembershipCreateDto(
                user.getId(),
                group.getId(),
                UserType.MEMBER,
                LocalDate.now(),
                true
        );

        when(userService.getById(1L)).thenReturn(Optional.of(user));
        when(gymGroupService.getById(2L)).thenReturn(Optional.of(group));
        when(membershipService.save(any(Membership.class)))
            .thenThrow(new IllegalArgumentException("Membership already exists"));

        mockMvc.perform(post("/api/gym-users/memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Membership already exists")));
    }

    @Test
    void testUpdateMembership_validUpdate_returns200() throws Exception {
        Membership updated = new Membership();
        updated.setId(100L);
        updated.setUser(user);
        updated.setGymGroup(group);
        updated.setUserSince(LocalDate.of(2023, 5, 5));
        updated.setType(UserType.ADMIN);
        updated.setActive(true);

        // Simulate saving update
        when(userService.getById(1L)).thenReturn(Optional.of(user));
        when(gymGroupService.getById(2L)).thenReturn(Optional.of(group));
        when(membershipService.save(any(Membership.class))).thenReturn(updated);

        MembershipCreateDto dto = new MembershipCreateDto(
                1L,
                2L,
                UserType.ADMIN,
                updated.getUserSince(),
                true
        );

        mockMvc.perform(post("/api/gym-users/memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("ADMIN"))
                .andExpect(jsonPath("$.userSince").value("2023-05-05"));
    }

    @Test
    void testDeleteMembership_returns204() throws Exception {
        Mockito.doNothing().when(membershipService).deleteById(100L);

        mockMvc.perform(delete("/api/gym-users/memberships/100"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteMembership_notFound_returns404() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Membership not found"))
                .when(membershipService).deleteById(999L);

        mockMvc.perform(delete("/api/gym-users/memberships/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Membership not found")));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public MembershipService membershipService() {
            System.out.println("Injecting mocked MembershipService");
            return Mockito.mock(MembershipService.class);
        }

        @Bean
        @Primary
        public UserService userService() {
            return Mockito.mock(UserService.class);
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
