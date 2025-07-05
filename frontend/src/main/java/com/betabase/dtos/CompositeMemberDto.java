package com.betabase.dtos;

public class CompositeMemberDto {
    
    private UserDto user;
    private MembershipDto membership;

    public CompositeMemberDto() {}

    public CompositeMemberDto(UserDto user, MembershipDto membership) {
        this.user = user;
        this.membership = membership;
    }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public MembershipDto getMembership() { return membership; }
    public void setMembership(MembershipDto membership) { this.membership = membership; }
}
