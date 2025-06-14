package com.betabase.controllers;

import java.io.IOException;
import java.util.List;

import com.betabase.services.MemberApiService;
import com.betabase.utils.SceneManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SidebarController {
    
    @FXML private VBox sidebar;
    @FXML private ImageView logo;
    @FXML private Label analyticsLabel;
    @FXML private Label calLabel;
    @FXML private Label posLabel;
    @FXML private Label memberLabel;
    @FXML private Label settingsLabel;
    @FXML private HBox analytics;
    @FXML private HBox cal;
    @FXML private HBox member;
    @FXML private HBox pos;
    @FXML private HBox settings;

    @FXML
    private void handleLogoClick(MouseEvent event) {
        SceneManager.switchScene((Stage) ((ImageView) event.getSource()).getScene().getWindow(), 
                        "/com/betabase/views/dashboard.fxml");
    }

    public void setActiveSection(String section) {
        switch (section.toLowerCase()) {
            case "member" -> highlightActiveButton(member);
            case "pos" -> highlightActiveButton(pos);
            case "cal" -> highlightActiveButton(cal);
            case "analytics" -> highlightActiveButton(analytics);
            case "settings" -> highlightActiveButton(settings);
        }
    }

    private void highlightActiveButton(HBox activeButton) {
        // Remove "sidebar-active" from all buttons
        List<HBox> buttons = List.of(analytics, cal, pos, settings);
        for (HBox btn : buttons) {
            btn.getStyleClass().remove("sidebar-active");
        }

        // Add "sidebar-active" to the current one
        if (!activeButton.getStyleClass().contains("sidebar-active")) {
            activeButton.getStyleClass().add("sidebar-active");
        }
    }

    @FXML
    protected void handleMemberClick(MouseEvent event) {
        SceneManager.switchToAPIScene(
            (Stage) memberLabel.getScene().getWindow(), 
            (CheckInController controller) -> {
                controller.setApiService(new MemberApiService());
                // Optionally: controller.setInitialMember(someMember);
            },
            "/com/betabase/views/checkIn.fxml",
            false
        );
    }

    @FXML
    private void handlePosClick(MouseEvent event) {
        SceneManager.switchScene((Stage) posLabel.getScene().getWindow(), 
                        "/com/betabase/views/pos.fxml");
    }

    @FXML
    protected void handleCalendarClick(MouseEvent event) {
        SceneManager.switchScene((Stage) calLabel.getScene().getWindow(),
                        "/com/betabase/views/calendar.fxml");
    }

    @FXML
    private void handleAnalyticsClick(MouseEvent event) {
        SceneManager.switchScene((Stage) analyticsLabel.getScene().getWindow(),
                        "/com/betabase/views/analytics.fxml");
    }

    @FXML
    private void handleSettingsClick(MouseEvent event) {
        SceneManager.switchScene((Stage) settingsLabel.getScene().getWindow(),
                        "/com/betabase/views/settings.fxml");
    }

}
