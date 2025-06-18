package com.betabase.controllers;

import com.betabase.services.GymApiService;
import com.betabase.utils.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Optional;
import java.util.prefs.Preferences;

public class GymLoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        GymApiService apiService = new GymApiService();
        try {
            Optional<String> tokenOpt = apiService.login(username, password);

            if (tokenOpt.isPresent()) {
                String token = tokenOpt.get();
                if (JwtUtils.isTokenExpired(token)) {
                    System.out.println("Login failed: Token expired");
                    return;
                }

                // Store token persistently
                TokenStorage.saveToken(username, token);

                // Transition to dashboard
                SceneManager.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    (DashboardController controller) -> controller.setMenuOpen(true),
                    "/com/betabase/views/dashboard.fxml",
                    false
                );
            } else {
                System.out.println("Login failed: Invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccountCreation() {
        SceneManager.switchScene(
                (Stage) usernameField.getScene().getWindow(),
                (CreateAccountController controller) -> { },
                "/com/betabase/views/createAccount.fxml",
                false);
    }

    private record LoginRequest(String username, String password) {}
}
