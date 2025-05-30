package com.betabase.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import com.betabase.utils.SceneManager;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        // Add validation/auth here
        SceneManager.switchScene((Stage) usernameField.getScene().getWindow(), "/com/betabase/views/dashboard.fxml");
    }
}
