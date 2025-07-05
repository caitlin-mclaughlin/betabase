package com.betabase.controllers;

import com.betabase.interfaces.ServiceAware;
import com.betabase.models.Address;
import com.betabase.models.Gym;
import com.betabase.services.CompositeMemberService;
import com.betabase.services.GymApiService;
import com.betabase.services.MembershipApiService;
import com.betabase.services.UserApiService;
import com.betabase.utils.AuthSession;
import com.betabase.utils.SceneManager;
import com.betabase.utils.TokenStorage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class CreateAccountController implements Initializable, ServiceAware {

    @FXML private TextField nameField;
    @FXML private TextField streetField1;
    @FXML private TextField streetField2;
    @FXML private TextField cityField;
    @FXML private TextField zipField;
    @FXML private TextField stateField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private GymApiService gymService;

    @Override
    public void setServices(UserApiService userService, MembershipApiService membershipService,
                            CompositeMemberService compositeMemberService, GymApiService gymService) {
        this.gymService = gymService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        confirmPasswordField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> handleCreateAccount(new ActionEvent());
            }
        });
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        if (!validateFields()) return;

        String name = nameField.getText();
        String street1 = streetField1.getText();
        String street2 = streetField2.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String zip = zipField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        Address address = formAddress(street1, street2, city, state, zip);

        Gym gym = new Gym();
        gym.setName(name);
        gym.setAddress(address);
        gym.setUserSince(LocalDate.now());

        try {
            String token = gymService.registerGym(gym, username, password);
            if (token != null) {
                AuthSession.setToken(token);
                TokenStorage.saveToken(username, token);

                SceneManager.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    (DashboardController controller) -> controller.setMenuOpen(true),
                    "/com/betabase/views/dashboard.fxml",
                    false
                );
            } else {
                System.out.println("Registration failed.");
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

    private Address formAddress(String street1, String street2, String city, String state, String zip) {
        String[] parts = street1.trim().split(" ", 2);
        String number = parts.length > 0 ? parts[0] : "";
        String name = parts.length > 1 ? parts[1] : "";

        return street2.isBlank()
            ? new Address(number, name, city, state, zip, "USA")
            : new Address(number, name, street2, city, state, zip, "USA");
    }

    private boolean validateFields() {
        boolean valid = true;

        valid &= validateField(nameField);
        valid &= validateField(streetField1);
        valid &= validateField(cityField);
        valid &= validateField(stateField);
        valid &= validateField(zipField);
        valid &= validateField(usernameField);
        valid &= validateField(passwordField);

        if (confirmPasswordField.getText().isBlank() || !confirmPasswordField.getText().equals(passwordField.getText())) {
            markInvalid(confirmPasswordField);
            valid = false;
        } else {
            clearInvalid(confirmPasswordField);
        }

        return valid;
    }

    private boolean validateField(Control field) {
        if (((TextField) field).getText().isBlank()) {
            markInvalid(field);
            return false;
        } else {
            clearInvalid(field);
            return true;
        }
    }

    private void markInvalid(Control field) {
        field.getStyleClass().remove("field");
        field.getStyleClass().add("invalid-field");
    }

    private void clearInvalid(Control field) {
        field.getStyleClass().remove("invalid-field");
        field.getStyleClass().add("field");
    }
}
