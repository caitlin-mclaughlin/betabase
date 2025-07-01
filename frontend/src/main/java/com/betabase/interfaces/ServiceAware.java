package com.betabase.interfaces;

import com.betabase.services.MemberApiService;
import com.betabase.services.GymApiService;

public interface ServiceAware {
    void setServices(MemberApiService memberService, GymApiService gymService);
}
