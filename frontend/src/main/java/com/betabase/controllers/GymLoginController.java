package com.betabase.controllers;

import com.betabase.dtos.JwtResponseDto;
import com.betabase.interfaces.ServiceAware;
import com.betabase.models.GymLoginRequest;
import com.betabase.services.CompositeMemberService;
import com.betabase.services.GymApiService;
import com.betabase.services.GymGroupApiService;
import com.betabase.services.GymLoginApiService;
import com.betabase.services.MembershipApiService;
import com.betabase.services.UserApiService;
import com.betabase.utils.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GymLoginController implements Initializable, ServiceAware {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label loginError;

    private GymApiService gymService;
    private GymLoginRequest gymLoginRequest = new GymLoginRequest();

    @Override
    public void setServices(UserApiService userService, MembershipApiService membershipService,
                            CompositeMemberService compositeMemberService, GymApiService gymService,
                            GymGroupApiService gymGroupApiService, GymLoginApiService gymLoginApiService) {
        this.gymService = gymService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        usernameField.textProperty().bind(gymLoginRequest.usernameProperty());
        passwordField.textProperty().bind(gymLoginRequest.passwordProperty());

        FieldValidator.attach(usernameField, text -> !text.isBlank());
        FieldValidator.attach(passwordField, text -> !text.isBlank());

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
                    AuthSession.setSession(savedToken, dto.gymId(), dto.gymName(), dto.username());

                    System.out.println("Auto-login success for: " + dto.username());

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

        boolean valid = true;
        valid &= FieldValidator.validate(usernameField, text -> !text.isBlank());
        valid &= FieldValidator.validate(passwordField, text -> !text.isBlank());

        if (!valid) return;

        try {
            Optional<JwtResponseDto> responseOpt = gymService.login(gymLoginRequest.getUsername(), gymLoginRequest.getPassword());

            if (responseOpt.isPresent()) {
                JwtResponseDto dto = responseOpt.get();
                if (JwtUtils.isTokenExpired(dto.token())) {
                    System.out.println("Login failed: Token expired");
                    return;
                }

                AuthSession.setSession(dto.token(), dto.gymId(), dto.gymName(), dto.username());
                TokenStorage.saveToken(dto.username(), dto.token());

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
    private void handleAccountCreation(MouseEvent event) {
        SceneManager.switchScene(
            (Stage) usernameField.getScene().getWindow(),
            (CreateAccountController controller) -> {},
            "/com/betabase/views/createAccount.fxml",
            false
        );
    }
}
