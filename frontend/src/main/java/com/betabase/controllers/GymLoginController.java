package com.betabase.controllers;

import com.betabase.services.GymApiService;
import com.betabase.utils.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class GymLoginController implements Initializable{

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Keypress shortcut
        passwordField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> handleLogin(new ActionEvent());
            }
        });

        // Delay auto-login until scene is loaded
        Platform.runLater(() -> {
            try {
                String savedToken = TokenStorage.getTokenForUser("testgym1");
                if (savedToken != null && !JwtUtils.isTokenExpired(savedToken)) {
                    AuthSession.setToken(savedToken);
                    System.out.println("Auto-login success");
                    SceneManager.switchScene(
                        (Stage) usernameField.getScene().getWindow(),
                        (DashboardController controller) -> controller.setMenuOpen(true),
                        "/com/betabase/views/dashboard.fxml",
                        false
                    );
                } else {
                    System.out.println("Auto-login failed: No valid token");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleLogin(ActionEvent event) {
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
                AuthSession.setToken(token);
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
    private void handleAccountCreation(ActionEvent event) {
        SceneManager.switchScene(
                (Stage) usernameField.getScene().getWindow(),
                (CreateAccountController controller) -> { },
                "/com/betabase/views/createAccount.fxml",
                false);
    }

    private record LoginRequest(String username, String password) {}
}
