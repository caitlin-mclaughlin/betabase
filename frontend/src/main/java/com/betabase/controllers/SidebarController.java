package com.betabase.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SidebarController {
    
    @FXML private VBox sidebar;
    @FXML private ImageView logo;
    @FXML private Label cal_label;
    @FXML private Label pos_label;

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
    private void handleAnalyticsClick(ActionEvent event) {
        // your handling code here, e.g.:
        System.out.println("Analytics button clicked!");
    }

    @FXML
    private void handleSettingsClick(ActionEvent event) {
        // your handling code here, e.g.:
        System.out.println("Settings button clicked!");
    }

}
