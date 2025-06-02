package com.betabase.controllers;

import java.io.IOException;
import java.util.List;

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
    @FXML private Label analytics_label;
    @FXML private Label cal_label;
    @FXML private Label pos_label;
    @FXML private Label settings_label;
    @FXML private HBox analytics;
    @FXML private HBox cal;
    @FXML private HBox pos;
    @FXML private HBox settings;

    @FXML
    private void handleLogoClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/dashboard.fxml"));
            Parent dashRoot = loader.load();

            Stage stage = (Stage) ((ImageView) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(dashRoot, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setActiveSection(String section) {
        switch (section.toLowerCase()) {
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
    private void handlePosClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/pos.fxml"));
            Parent posRoot = loader.load();

            Stage stage = (Stage) pos_label.getScene().getWindow();
            Scene scene = new Scene(posRoot, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleCalendarClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/calendar.fxml"));
            Parent calRoot = loader.load();

            Stage stage = (Stage) cal_label.getScene().getWindow();
            Scene scene = new Scene(calRoot, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnalyticsClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/analytics.fxml"));
            Parent analyticsRoot = loader.load();

            Stage stage = (Stage) analytics_label.getScene().getWindow();
            Scene scene = new Scene(analyticsRoot, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettingsClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/settings.fxml"));
            Parent settingsRoot = loader.load();

            Stage stage = (Stage) settings_label.getScene().getWindow();
            Scene scene = new Scene(settingsRoot, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
