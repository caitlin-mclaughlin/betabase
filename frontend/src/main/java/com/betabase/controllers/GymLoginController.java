package com.betabase.controllers;

import com.betabase.dtos.JwtResponseDto;
import com.betabase.interfaces.ServiceAware;
import com.betabase.services.CompositeMemberService;
import com.betabase.services.GymApiService;
import com.betabase.services.MembershipApiService;
import com.betabase.services.UserApiService;
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

    private UserApiService userService;
    private GymApiService gymService;

    @Override
    public void setServices(UserApiService userService, MembershipApiService membershipService,
                            CompositeMemberService compositeMemberService, GymApiService gymService) {
        this.userService = userService;
        this.gymService = gymService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> handleLogin(new ActionEvent());
            }
        });

        Platform.runLater(() -> {
            try {
                String savedToken = TokenStorage.getAnyValidToken();
                if (savedToken != null && !JwtUtils.isTokenExpired(savedToken)) {
                    JwtResponseDto dto = JwtUtils.extractJwtDetails(savedToken);
                    AuthSession.setSession(savedToken, dto.getGymId(), dto.getGymName(), dto.getUsername());

                    System.out.println("Auto-login success for: " + dto.getUsername());

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
        loginError.setVisible(false);
        if (!checkFields()) return;

        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            Optional<JwtResponseDto> responseOpt = gymService.login(username, password);

            if (responseOpt.isPresent()) {
                JwtResponseDto dto = responseOpt.get();
                if (JwtUtils.isTokenExpired(dto.getToken())) {
                    System.out.println("Login failed: Token expired");
                    return;
                }

                AuthSession.setSession(dto.getToken(), dto.getGymId(), dto.getGymName(), dto.getUsername());
                TokenStorage.saveToken(dto.getUsername(), dto.getToken());

                SceneManager.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    (DashboardController controller) -> controller.setMenuOpen(true),
                    "/com/betabase/views/dashboard.fxml",
                    false
                );
            } else {
                System.out.println("Login failed: No token returned");
            }
        } catch (GymApiService.LoginException e) {
            loginError.setText("Invalid credentials.");
            loginError.setVisible(true);
        } catch (Exception e) {
            loginError.setText("Error logging in.");
            loginError.setVisible(true);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccountCreation(ActionEvent event) {
        SceneManager.switchScene(
            (Stage) usernameField.getScene().getWindow(),
            (CreateAccountController controller) -> {},
            "/com/betabase/views/createAccount.fxml",
            false
        );
    }

    private boolean checkFields() {
        boolean valid = true;
        if (usernameField.getText().isBlank()) {
            markInvalid(usernameField); valid = false;
        } else {
            clearInvalid(usernameField);
        }

        if (passwordField.getText().isBlank()) {
            markInvalid(passwordField); valid = false;
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
}
