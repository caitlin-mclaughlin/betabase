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

    @BeforeEach
    void setup() {
        membershipRepo = mock(MembershipRepository.class);
        membershipService = new MembershipService(membershipRepo);

        user = new User();
        user.setId(1L);
        user.setFirstName("John");

        group = new GymGroup();
        group.setId(1L);
        group.setName("Test Group");
    }

    @Test
    void testSaveMembership() {
        Membership expected = new Membership();
        expected.setUser(user);
        expected.setGymGroup(group);
        expected.setType(UserType.MEMBER);
        expected.setUserSince(LocalDate.now());
        expected.setActive(true);

        when(membershipRepo.save(any(Membership.class))).thenReturn(expected);

        Membership result = membershipService.save(expected);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(group, result.getGymGroup());
        assertEquals(UserType.MEMBER, result.getType());
        assertTrue(result.isActive());
    }

    @Test
    void testGetMembershipsForUser() {
        when(membershipRepo.findByUserId(1L)).thenReturn(List.of(new Membership()));
        List<Membership> result = membershipService.getMembershipsForUser(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetMembershipsForGymGroup() {
        when(membershipRepo.findByGymGroupId(1L)).thenReturn(List.of(new Membership()));
        List<Membership> result = membershipService.getMembershipsForGym(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetForUserAndGymGroup() {
        Membership mock = new Membership();
        when(membershipRepo.findByUserIdAndGymGroupId(1L, 1L)).thenReturn(Optional.of(mock));
        Membership result = membershipService.getForUserAndGym(1L, 1L);
        assertNotNull(result);
    }

    @Test
    void testGetForUserAndGymGroup_notFound() {
        when(membershipRepo.findByUserIdAndGymGroupId(1L, 1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            membershipService.getForUserAndGym(1L, 1L);
        });
    }
}
