package com.betabase.dtos;

public record CompositeMemberDto (
    UserDto user,
    MembershipDto membership
) {}
