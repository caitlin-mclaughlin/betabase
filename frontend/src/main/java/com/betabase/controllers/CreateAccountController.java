package com.betabase.controllers;

import com.betabase.dtos.GymRegistrationRequestDto;
import com.betabase.dtos.GymRegistrationResponseDto;
import com.betabase.dtos.simple.AddressDto;
import com.betabase.dtos.simple.GymGroupCreateDto;
import com.betabase.interfaces.ServiceAware;
import com.betabase.models.Address;
import com.betabase.models.Gym;
import com.betabase.models.GymGroup;
import com.betabase.models.GymLogin;
import com.betabase.services.*;
import com.betabase.utils.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CreateAccountController implements Initializable, ServiceAware {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> groupField;
    @FXML private TextField streetField1;
    @FXML private TextField streetField2;
    @FXML private TextField cityField;
    @FXML private TextField zipField;
    @FXML private ComboBox<String> stateField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private Gym gym = new Gym();
    private GymGroup gymGroup = new GymGroup();
    private GymLogin gymLogin = new GymLogin();

    private List<GymGroup> existingGroups;
    private GymGroupApiService gymGroupService;
    private GymLoginApiService gymLoginService;

    private static final String NEW_GYM_GROUP_OPTION = "New Gym Group";

    @Override
    public void setServices(UserApiService userService, MembershipApiService membershipService,
                            CompositeMemberService compositeMemberService, GymApiService gymService,
                            GymGroupApiService gymGroupService, GymLoginApiService gymLoginService) {
        this.gymGroupService = gymGroupService;
        this.gymLoginService = gymLoginService;

        try {
            existingGroups = gymGroupService.getPublicGroupNames();
            List<String> groupNames = existingGroups.stream()
                .map(GymGroup::getName)
                .collect(Collectors.toList());
            groupNames.add(NEW_GYM_GROUP_OPTION);
            groupField.getItems().setAll(groupNames);
            groupField.setValue(NEW_GYM_GROUP_OPTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gym.setAddress(new Address());
        gym.setGroup(gymGroup);
        gym.setUserSince(LocalDate.now());

        // Bind text fields to model
        streetField1.textProperty().bindBidirectional(gym.getAddress().streetAddressProperty());
        streetField2.textProperty().bindBidirectional(gym.getAddress().apartmentNumberProperty());
        cityField.textProperty().bindBidirectional(gym.getAddress().cityProperty());
        zipField.textProperty().bindBidirectional(gym.getAddress().zipCodeProperty());
        stateField.getItems().setAll(StateUtils.getStateAbbreviations());
        stateField.valueProperty().bindBidirectional(gym.getAddress().stateProperty());

        nameField.textProperty().bindBidirectional(gym.nameProperty());
        groupField.valueProperty().bindBidirectional(gymGroup.nameProperty());
        usernameField.textProperty().bindBidirectional(gymLogin.usernameProperty());
        passwordField.textProperty().bindBidirectional(gymLogin.passwordProperty());

        FieldValidator.attach(usernameField, text -> !text.isBlank());
        FieldValidator.attach(passwordField, text -> !text.isBlank());
/*
        confirmPasswordField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> handleCreateAccount(new ActionEvent());
            }
        }); */
    }

    @FXML
    private void handleCreateAccount(MouseEvent event) {
        
        System.out.println("\nDEBUG: create account pressed\n");

        boolean valid = true;
        valid &= FieldValidator.validate(usernameField, text -> !text.isBlank());
        valid &= FieldValidator.validate(passwordField, text -> !text.isBlank());

        if (!valid) return;

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            FieldValidator.markInvalid(confirmPasswordField);
            return;
        }

        // Determine GymGroup name
        String selectedGroup = groupField.getValue();
        String groupName;

        if (NEW_GYM_GROUP_OPTION.equals(selectedGroup)) {
            groupName = nameField.getText();
        } else {
            groupName = selectedGroup;
        }

        // Prepare Address DTO
        Address address = gym.getAddress();
        AddressDto addressDto = new AddressDto(
            address.getStreetAddress(),
            address.getApartmentNumber(),
            address.getCity(),
            address.getState(),
            address.getZipCode()
        );

        GymGroupCreateDto groupDto = new GymGroupCreateDto(groupName);

        GymRegistrationRequestDto request = new GymRegistrationRequestDto(
            gymLogin.getUsername(),
            gymLogin.getPassword(),
            gym.getName(),
            addressDto,
            gym.getUserSince(),
            groupName
        );

        try {        
            System.out.println("\nDEBUG: trying to register\n");
            GymRegistrationResponseDto response = gymLoginService.registerGymLogin(request);
            System.out.println("\nDEBUG: registered\n");

            if (response != null) {
                AuthSession.setSession(
                    response.token(),
                    response.gymId(),
                    response.gymName(),
                    response.username()
                );
                TokenStorage.saveToken(response.username(), response.token());

                SceneManager.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    (DashboardController controller) -> controller.setMenuOpen(true),
                    "/com/betabase/views/dashboard.fxml",
                    false
                );
            } else {
                System.out.println("Registration failed: No response.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturnToLogin(MouseEvent event) {
        SceneManager.switchScene(
            (Stage) usernameField.getScene().getWindow(),
            (GymLoginController controller) -> {},
            "/com/betabase/views/gymLogin.fxml",
            false
        );
    }
}
