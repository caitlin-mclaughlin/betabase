package com.example.betabase.services;

import com.example.betabase.enums.UserType;
import com.example.betabase.models.GymGroup;
import com.example.betabase.models.Membership;
import com.example.betabase.models.User;
import com.example.betabase.repositories.MembershipRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MembershipServiceTest {

    private MembershipRepository membershipRepo;
    private MembershipService membershipService;

    private User user;
    private GymGroup group;
    private Membership membership;

    @BeforeEach
    void setup() {
        membershipRepo = mock(MembershipRepository.class);
        membershipService = new MembershipService(membershipRepo);

        user = new User();
        user.setId(1L);
        user.setFirstName("Alice");

        group = new GymGroup();
        group.setId(1L);
        group.setName("BetaBase Group");

        membership = new Membership();
        membership.setId(10L);
        membership.setUser(user);
        membership.setGymGroup(group);
        membership.setType(UserType.MEMBER);
        membership.setUserSince(LocalDate.now());
        membership.setActive(true);
    }

    @Test
    void testSaveMembership() {
        when(membershipRepo.save(any(Membership.class))).thenReturn(membership);

        Membership result = membershipService.save(membership);

        assertNotNull(result);
        assertEquals(membership.getId(), result.getId());
        assertEquals(UserType.MEMBER, result.getType());
        assertEquals(user, result.getUser());
        assertEquals(group, result.getGymGroup());
        assertTrue(result.isActive());
    }

    @Test
    void testGetMembershipsForUser() {
        when(membershipRepo.findByUserId(1L)).thenReturn(List.of(membership));

        List<Membership> result = membershipService.getMembershipsForUser(1L);

        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    void testGetMembershipsForGymGroup() {
        when(membershipRepo.findByGymGroupId(1L)).thenReturn(List.of(membership));

        List<Membership> result = membershipService.getMembershipsForGym(1L);

        assertEquals(1, result.size());
        assertEquals(group.getId(), result.get(0).getGymGroup().getId());
    }

    @Test
    void testGetForUserAndGymGroup() {
        when(membershipRepo.findByUserIdAndGymGroupId(1L, 1L)).thenReturn(Optional.of(membership));

        Optional<Membership> result = membershipService.getByUserIdAndGymGroupId(1L, 1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get().getUser());
        assertEquals(group, result.get().getGymGroup());
    }

    @Test
    void testGetForUserAndGymGroup_NotFound() {
        when(membershipRepo.findByUserIdAndGymGroupId(1L, 1L)).thenReturn(Optional.empty());

        Optional<Membership> result = membershipService.getByUserIdAndGymGroupId(1L, 1L);

        assertFalse(result.isPresent());
    }
}
