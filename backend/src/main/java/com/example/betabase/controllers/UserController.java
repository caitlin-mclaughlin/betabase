package com.example.betabase.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.betabase.dtos.UserCreateDto;
import com.example.betabase.dtos.UserDto;
import com.example.betabase.models.GymLogin;
import com.example.betabase.models.Membership;
import com.example.betabase.models.User;
import com.example.betabase.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id, @AuthenticationPrincipal GymLogin login) {
        User saved = userService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(toDto(saved));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserCreateDto createDto) {
        User user = fromCreateDto(createDto);
        User saved = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserCreateDto updateDto) {
        Optional<User> existingOpt = userService.getById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User existing = existingOpt.get();
        User updatedUser = fromCreateDto(updateDto);
        updatedUser.setId(existing.getId());
        User saved = userService.save(updatedUser);
        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchPotentialMatches(
            @RequestParam String email,
            @RequestParam String phoneNumber) {
        List<User> users = userService.searchPotentialMatches(email, phoneNumber);
        return ResponseEntity.ok(users.stream().map(this::toDto).toList());
    }

    private User fromCreateDto(UserCreateDto dto) {
        User user = new User();
        user.setFirstName(dto.firstName());
        user.setPrefName(dto.prefName());
        user.setLastName(dto.lastName());
        user.setPronouns(dto.pronouns());
        user.setGender(dto.gender());
        user.setPhoneNumber(dto.phoneNumber());
        user.setEmail(dto.email());
        user.setDateOfBirth(dto.dateOfBirth());
        user.setAddress(dto.address());
        user.setEmergencyContactName(dto.emergencyContactName());
        user.setEmergencyContactPhone(dto.emergencyContactPhone());
        user.setEmergencyContactEmail(dto.emergencyContactEmail());
        return user;
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getPrefName(),
                user.getLastName(),
                user.getPronouns(),
                user.getGender(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getAddress(),
                getMembershipIds(user.getMemberships()),
                user.getEmergencyContactName(),
                user.getEmergencyContactPhone(),
                user.getEmergencyContactEmail()
        );
    }

    private List<Long> getMembershipIds(List<Membership> memberships) {
        List<Long> ids = new ArrayList<>();
        if (memberships != null) {
            for (Membership m : memberships) {
                ids.add(m.getId());
            }
        }
        return ids;
    }
}
