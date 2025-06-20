package com.betabase.controllers;

import java.util.List;

import com.betabase.services.MemberApiService;
import com.betabase.utils.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SidebarController {
    
    @FXML private VBox sidebar;
    @FXML private ImageView logo;
    @FXML private Label memberLabel;
    @FXML private Label posLabel;
    @FXML private Label calLabel;
    @FXML private Label analyticsLabel;
    @FXML private Label settingsLabel;
    @FXML private HBox member;
    @FXML private HBox pos;
    @FXML private HBox cal;
    @FXML private HBox analytics;
    @FXML private HBox settings;
    @FXML private HBox menu;

    private boolean menuOpen;

    public void setMenuOpen(boolean menuOpen) {
        this.menuOpen = !menuOpen; // set opposite then toggle immediately
        toggleMenuVisibility();
    }

    public boolean getMenuOpen() { return menuOpen; }

    @FXML
    private void handleLogoClick(MouseEvent event) {
        SceneManager.switchScene(
            (Stage) logo.getScene().getWindow(),
            (DashboardController controller) -> {
                controller.setMenuOpen(menuOpen);
            },
            "/com/betabase/views/dashboard.fxml",
            false
        );
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
        SceneManager.switchScene(
            (Stage) memberLabel.getScene().getWindow(), 
            (CheckInController controller) -> {
                controller.setApiService(new MemberApiService());
                controller.setMenuOpen(menuOpen);
            },
            "/com/betabase/views/checkIn.fxml",
            false
        );
    }

    @FXML
    private void handlePosClick(MouseEvent event) {
        SceneManager.switchScene(
            (Stage) posLabel.getScene().getWindow(),
            (PosController controller) -> {
                controller.setMenuOpen(menuOpen);
            },
            "/com/betabase/views/pos.fxml",
            false
        );
    }

    @FXML
    protected void handleCalendarClick(MouseEvent event) {
        SceneManager.switchScene(
            (Stage) calLabel.getScene().getWindow(),
            (CalendarController controller) -> {
                controller.setMenuOpen(menuOpen);
            },
            "/com/betabase/views/calendar.fxml",
            false
        );
    }

    @FXML
    private void handleAnalyticsClick(MouseEvent event) {
        SceneManager.switchScene(
            (Stage) analyticsLabel.getScene().getWindow(),
            (AnalyticsController controller) -> {
                controller.setMenuOpen(menuOpen);
            },
            "/com/betabase/views/analytics.fxml",
            false
        );
    }

    @FXML
    private void handleSettingsClick() {
        SceneManager.switchScene(
            (Stage) settingsLabel.getScene().getWindow(),
            (SettingsController controller) -> {
                controller.setMenuOpen(menuOpen);
            },
            "/com/betabase/views/settings.fxml",
            false
        );
    }

    @FXML
    private void handleMenuClick(MouseEvent event) {
        toggleMenuVisibility();
    }

    private void toggleMenuVisibility() {
        if (menuOpen) {
            sidebar.setStyle("-fx-min-width: 75px; -fx-max-width: 75px;");
            menuOpen = false;
        } else {
            sidebar.setStyle("-fx-min-width: 275px; -fx-max-width: 275px;");
            menuOpen = true;
        }

        logo.setVisible(menuOpen);
        logo.setManaged(menuOpen);
        memberLabel.setVisible(menuOpen);
        memberLabel.setManaged(menuOpen);
        posLabel.setVisible(menuOpen);
        posLabel.setManaged(menuOpen);
        calLabel.setVisible(menuOpen);
        calLabel.setManaged(menuOpen);
        analyticsLabel.setVisible(menuOpen);
        analyticsLabel.setManaged(menuOpen);
        settingsLabel.setVisible(menuOpen);
        settingsLabel.setManaged(menuOpen);
    }

}
