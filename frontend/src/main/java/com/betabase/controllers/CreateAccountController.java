package com.betabase.controllers;

import com.betabase.interfaces.ServiceAware;
import com.betabase.models.Address;
import com.betabase.models.Gym;
import com.betabase.services.GymApiService;
import com.betabase.services.MemberApiService;
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

    private MemberApiService memberService;
    private GymApiService gymService;

    @Override
    public void setServices(MemberApiService memberService, GymApiService gymService) {
        this.memberService = memberService;
        this.gymService = gymService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        confirmPasswordField.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER -> {
                        handleLogin(new ActionEvent());
                    }
                }
            }
        );
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        String name = nameField.getText();
        String streetAddress1 = streetField1.getText();
        String streetAddress2 = streetField2.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String zip = zipField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        Address address = formAddress(streetAddress1, streetAddress2, city, state, zip);
        Gym gym = new Gym();
        gym.setName(name);
        gym.setAddress(address);
        gym.setUserSince(LocalDate.now());

        GymApiService gymService = new GymApiService();
        try {
            String token = gymService.registerGym(gym, username, password);
            if (token != null) {
                // Save the JWT in memory (or preferences for now, later use secure storage)
                AuthSession.setToken(token);
                TokenStorage.saveToken(username, token);

                SceneManager.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    (DashboardController controller) -> {
                        controller.setMenuOpen(true);
                    },
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
            (GymLoginController controller) -> { },
            "/com/betabase/views/gymLogin.fxml",
            false);
    }

    /**
     * 
     * NOTE: HARDCODED COUNTRY
     * 
     */
    private Address formAddress(String streetAddress1, String streetAddress2, String city, String state, String zip) {
        String[] parsed = streetAddress1.split(" ");
        String streetNumber = parsed[0];
        String streetName = parsed[1];
        return streetAddress2.isBlank() ? new Address(streetNumber, streetName, city, state, zip, "USA") :
                                          new Address(streetNumber, streetName, streetAddress2, city, state, zip, "USA");
    }

    private boolean validateFields() {
        boolean valid = true;
        if (nameField.getText() .isBlank()) {
            markInvalid(nameField);
            valid =  false;
        } else {
            clearInvalid(nameField);
        }

        if (streetField1.getText() .isBlank()) {
            markInvalid(streetField1);
            valid = false;
        } else {
            clearInvalid(streetField1);
        }

        if (cityField.getText() .isBlank()) {
            markInvalid(cityField);
            valid = false;
        } else {
            clearInvalid(cityField);
        }

        if (stateField.getText() .isBlank()) {
            markInvalid(stateField);
            valid = false;
        } else {
            clearInvalid(stateField);
        }

        if (zipField.getText() .isBlank()) {
            markInvalid(zipField);
            valid = false;
        } else {
            clearInvalid(zipField);
        }

        if (usernameField.getText() .isBlank()) {
            markInvalid(usernameField);
            valid = false;
        } else {
            clearInvalid(usernameField);
        }

        if (passwordField.getText() .isBlank()) {
            markInvalid(passwordField);
            valid = false;
        } else {
            clearInvalid(passwordField);
        }

        if (confirmPasswordField.getText() .isBlank() || 
                !passwordField.getText().equals(confirmPasswordField.getText())) {
            markInvalid(confirmPasswordField);
            valid = false;
        } else {
            clearInvalid(confirmPasswordField);
        }

        return valid;
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
