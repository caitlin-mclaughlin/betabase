package com.betabase.controllers;

import com.betabase.models.Member;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MemberController {
    
    @FXML private Label nameLabel;
    @FXML private Label genderLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label dobLabel;
    @FXML private Label memberIdLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label billingLabel;
    @FXML private Label addressLabel;
    @FXML private Label eNameLabel;
    @FXML private Label ePhoneLabel;
    @FXML private Label eEmailLabel;

    public void setMember(Member member) {
        // validate and display data
        nameLabel.setText(member.getLastName() + ",  " + member.getFirstName() + "  \"" + 
                          member.getPrefName() + "\"  (" + member.getPronouns() + ")");
        genderLabel.setText(member.getGender());
        phoneLabel.setText(member.getPhoneNumber());
        emailLabel.setText(member.getEmail());
        dobLabel.setText(member.getDateOfBirth() != null ? member.getDateOfBirth().toString()  : " / / ");
        memberIdLabel.setText(member.getMemberId());
        memberSinceLabel.setText(member.getMemberSince() != null ? member.getMemberSince().toString()  : " / / ");
        billingLabel.setText(member.getBillingMethod());
        addressLabel.setText(member.getAddress());
        eNameLabel.setText(member.getEmergencyContactName());
        ePhoneLabel.setText(member.getEmergencyContactPhone());
        eEmailLabel.setText(member.getEmergencyContactEmail());
    }
}
