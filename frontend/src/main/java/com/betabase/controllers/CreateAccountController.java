package com.betabase.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.betabase.models.Gym;
import com.betabase.services.GymApiService;
import com.betabase.utils.AuthSession;
import com.betabase.utils.SceneManager;
import com.betabase.utils.TokenStorage;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class CreateAccountController implements Initializable {
    @FXML private TextField nameField;
    @FXML private TextField streetField1;
    @FXML private TextField streetField2;
    @FXML private TextField cityField;
    @FXML private TextField zipField;
    @FXML private TextField stateField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

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
        if (!checkFields()) {
            return;
        }

        String name = nameField.getText();
        String address = streetField2.getText().isBlank() ? streetField1.getText() : 
                         streetField1.getText() + " " + streetField2.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String zip = zipField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        Gym gym = new Gym();
        gym.setName(name);
        gym.setAddress(address);
        gym.setCity(city);
        gym.setState(state);
        gym.setZipCode(zip);
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
            (GymLoginController controller) -> { },
            "/com/betabase/views/gymLogin.fxml",
            false);
    }

    private boolean checkFields() {
        if (nameField.getText() == null) {
            // error message
            return false;
        }
        if (streetField1.getText() == null) {
            // error message
            return false;
        }
        if (cityField.getText() == null) {
            // error message
            return false;
        }
        if (stateField.getText() == null) {
            // error message
            return false;
        }
        if (zipField.getText() == null) {
            // error message
            return false;
        }
        if (usernameField.getText() == null) {
            // error message
            return false;
        }
        if (passwordField.getText() == null) {
            // error message
            return false;
        }
        if (confirmPasswordField.getText() == null || 
            !passwordField.getText().equals(confirmPasswordField.getText())) {
            // error message
            return false;
        }
        return true;
    }
}
