package com.betabase.controllers;

import com.betabase.models.Member;
import com.betabase.services.MemberApiService;

import javafx.scene.control.Label;

import java.io.IOException;

public class CheckInController {

    MemberApiService apiService = new MemberApiService();
    private Label memberId;
    private Label name;

    public void checkIn() {
        try {
            Member member = apiService.getMemberById("memberId.getText()");
            if (member != null) {
                // populate UI
                name.setText(member.getFirstName() + " " + member.getLastName());
                // etc.
            } else {
                // show error popup
            }
        } catch (Exception e) {
            e.printStackTrace();
            // handle connection error
        }
    }

}
