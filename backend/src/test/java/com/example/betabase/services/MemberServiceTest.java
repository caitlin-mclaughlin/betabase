package com.example.betabase.services;

import com.example.betabase.models.Gym;
import com.example.betabase.models.GymLogin;
import com.example.betabase.models.Member;
import com.example.betabase.repositories.MemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MemberServiceTest {

    private MemberRepository memberRepository;
    private MemberService memberService;
    private Gym gym;
    private Member member;

    @BeforeEach
    void setup() {
        memberRepository = mock(MemberRepository.class);
        memberService = new MemberService(memberRepository);

        gym = new Gym();
        gym.setId(1L);
        gym.setName("Test Gym");
        gym.setUserSince(LocalDate.now());
        
        member = new Member();
        member.setGym(gym);
        member.setFirstName("Test");
        member.setLastName("Member");
        member.setPhoneNumber("0123456789");
        member.setEmail("email@gmail.com");
        member.setDateOfBirth(LocalDate.of(2000, 1, 1));
        member.setEmergencyContactName("eTest");
        member.setEmergencyContactPhone("eTest");
        member.setEmergencyContactEmail("eTest");
        member.setMemberSince(LocalDate.now());
    }

    @Test
    void testSaveMember() {

        when(memberRepository.save(member)).thenReturn(member);

        Member saved = memberService.save(member);
        assertNotNull(saved);
        assertEquals("Test", saved.getFirstName());
        assertEquals(1L, saved.getGym().getId());
    }

    @Test
    void testGetMemberById() {
        member.setId(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Optional<Member> result = memberService.getById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getFirstName());
    }

    @Test
    void testGetMemberById_NotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Member> result = memberService.getById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void testSearchMembers_OneMatch() {
        when(memberRepository.findByQueryAndGymId("Test", 1L)).thenReturn(List.of(member));

        List<Member> memList = memberService.searchMembers("Test",1L);
        assertTrue(memList.contains(member));
    }

    @Test
    void testSearchMembers_SeparateGyms() {
        Gym gym2 = new Gym();
        gym2.setId(2L);
        gym2.setName("Test Gym2");
        gym2.setUserSince(LocalDate.now());

        Member member2 = new Member();
        member2.setGym(gym2);
        member2.setFirstName("Test2");
        member2.setLastName("Member2");
        member2.setPhoneNumber("1234567890");
        member2.setEmail("email2@gmail.com");
        member2.setDateOfBirth(LocalDate.of(2000, 1, 1));
        member2.setEmergencyContactName("eTest2");
        member2.setEmergencyContactPhone("eTest2");
        member2.setEmergencyContactEmail("eTest2");
        member2.setMemberSince(LocalDate.now());

        when(memberRepository.findByQueryAndGymId("Test", 1L)).thenReturn(List.of(member));
        when(memberRepository.findByQueryAndGymId("Test", 2L)).thenReturn(List.of(member2));

        List<Member> memList1 = memberService.searchMembers("Test",1L);
        List<Member> memList2 = memberService.searchMembers("Test",2L);
        assertTrue(memList1.size() == 1 && memList2.size() == 1);
        assertTrue(memList1.contains(member) && memList2.contains(member2));
    }
    
    @Test
    void testSearchMembers_TwoMatches() {
        Member member2 = new Member();
        member2.setGym(gym);
        member2.setFirstName("Test2");
        member2.setLastName("Member2");
        member2.setPhoneNumber("1234567890");
        member2.setEmail("email2@gmail.com");
        member2.setDateOfBirth(LocalDate.of(2000, 1, 1));
        member2.setEmergencyContactName("eTest2");
        member2.setEmergencyContactPhone("eTest2");
        member2.setEmergencyContactEmail("eTest2");
        member2.setMemberSince(LocalDate.now());

        when(memberRepository.findByQueryAndGymId("Test", 1L)).thenReturn(List.of(member, member2));

        List<Member> memList = memberService.searchMembers("Test",1L);
        assertTrue(memList.size() == 2);
        assertTrue(memList.contains(member) && memList.contains(member2));
    }

    @Test
    void testSearchMembers_WrongGymId() {
        when(memberRepository.findByQueryAndGymId("Test", 2L)).thenReturn(List.of());

        List<Member> memList = memberService.searchMembers("Test",2L);
        assertTrue(memList.size() == 0);
    }

    @Test
    void testSearchMembers_NotFound() {
        when(memberRepository.findByQueryAndGymId("Test", 1L)).thenReturn(List.of());

        List<Member> memList = memberService.searchMembers("Test",1L);
        assertTrue(memList.size() == 0);
    }
}
