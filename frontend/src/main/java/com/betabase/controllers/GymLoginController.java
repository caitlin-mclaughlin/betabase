package com.betabase.controllers;

import com.betabase.utils.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;

public class GymLoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        // Add validation/auth here
        SceneManager.switchScene(
            (Stage) usernameField.getScene().getWindow(),
            (SettingsController controller) -> {
                controller.setMenuOpen(true);
            },
            "/com/betabase/views/dashboard.fxml",
            false);
    }

    @FXML
    private void handleCreateAccount() {
        
    }
}
