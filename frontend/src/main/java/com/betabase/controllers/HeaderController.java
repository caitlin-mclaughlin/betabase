package com.betabase.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class HeaderController {

    @FXML private Label titleLabel;
    @FXML private Label appTitleLabel;
    @FXML private ImageView logoImage;

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    @FXML
    private void handleTitleClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Get the current stage using any node in the scene (e.g., the label)
            Stage stage = (Stage) appTitleLabel.getScene().getWindow();

            // Set new scene
            Scene scene = new Scene(dashboardRoot, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
