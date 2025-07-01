package com.betabase.controllers;

import com.betabase.interfaces.ServiceAware;
import com.betabase.services.GymApiService;
import com.betabase.services.MemberApiService;
import com.betabase.utils.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GymLoginController implements Initializable, ServiceAware {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label loginError;

    private MemberApiService memberService;
    private GymApiService gymService;

    @Override
    public void setServices(MemberApiService memberService, GymApiService gymService) {
        this.memberService = memberService;
        this.gymService = gymService;
    }

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
                String savedToken = TokenStorage.getAnyValidToken();
                if (savedToken != null && !JwtUtils.isTokenExpired(savedToken)) {
                    AuthSession.setToken(savedToken);

                    String username = JwtUtils.getUsername(savedToken);
                    System.out.println("Auto-login success for: " + username);

                    SceneManager.switchScene(
                        (Stage) usernameField.getScene().getWindow(),
                        (DashboardController controller) -> {
                            controller.setMenuOpen(true);
                        },
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
            loginError.setVisible(false);
        if (!checkFields()) {
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            Optional<String> tokenOpt = gymService.login(username, password);

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
                System.out.println("Login failed: token not present");
            }
        } catch (GymApiService.LoginException e) {
            // Display backend-driven messages
            loginError.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccountCreation(ActionEvent event) {
        SceneManager.switchScene(
                (Stage) usernameField.getScene().getWindow(),
                (CreateAccountController controller) -> {},
                "/com/betabase/views/createAccount.fxml",
                false);
    }

    private boolean checkFields() {
        boolean valid = true;

        if (usernameField.getText().isBlank()) {
            markInvalid(usernameField);
            valid = false;
        } else {
            clearInvalid(usernameField);
        }

        if (passwordField.getText().isBlank()) {
            markInvalid(passwordField);
            valid = false;
        } else {
            clearInvalid(passwordField);
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

    private record LoginRequest(String username, String password) {}
}
