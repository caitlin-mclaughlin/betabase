package com.betabase.interfaces;

import com.betabase.services.UserApiService;
import com.betabase.services.CompositeMemberService;
import com.betabase.services.GymApiService;
import com.betabase.services.MembershipApiService;

public interface ServiceAware {
    void setServices(UserApiService userService,
                     MembershipApiService membershipService,
                     CompositeMemberService compositeMemberService,
                     GymApiService gymService);
}
