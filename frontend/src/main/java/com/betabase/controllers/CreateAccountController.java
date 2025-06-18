package com.betabase.controllers;

import com.betabase.models.Gym;
import com.betabase.services.GymApiService;
import com.betabase.utils.AuthSession;
import com.betabase.utils.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateAccountController {
    @FXML private TextField nameField;
    @FXML private TextField streetField;
    @FXML private TextField cityField;
    @FXML private TextField zipField;
    @FXML private TextField stateField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    private void handleLogin() {
        String name = nameField.getText();
        String address = streetField.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String zip = zipField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (!password.equals(confirm)) {
            System.out.println("Passwords do not match.");
            return;
        }

        Gym gym = new Gym();
        gym.setName(name);
        gym.setAddress(address);
        gym.setCity(city);
        gym.setState(state);
        gym.setZipCode(zip);

        GymApiService gymService = new GymApiService();
        try {
            String jwt = gymService.registerGym(gym, username, password);
            if (jwt != null) {
                // Save the JWT in memory (or preferences for now, later use secure storage)
                AuthSession.setToken(jwt); // <- Make a simple AuthSession class to hold it

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
    private void handleReturnToLogin() {
        SceneManager.switchScene(
            (Stage) usernameField.getScene().getWindow(),
            (GymLoginController controller) -> { },
            "/com/betabase/views/gymLogin.fxml",
            false);
    }
}
