package com.betabase.dtos;

import java.time.LocalDate;

import com.betabase.enums.UserType;

public class MembershipDto {
    private Long id;
    private Long userId;
    private Long gymId;
    private UserType type; // ADMIN, STAFF, MEMBER, etc.
    private LocalDate memberSince;
    private boolean active;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }

    public UserType getType() { return type; }
    public void setType(UserType type) { this.type = type; }

    public LocalDate getMemberSince() { return memberSince; }
    public void setMemberSince(LocalDate memberSince) { this.memberSince = memberSince; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
