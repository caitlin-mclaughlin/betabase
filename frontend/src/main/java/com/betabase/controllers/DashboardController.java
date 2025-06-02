package com.betabase.controllers;

import javafx.event.ActionEvent;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private ImageView logoImage;
    @FXML private TextField search;

    SidebarController sidebarController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/sidebar.fxml"));
            VBox sidebar = loader.load();

            sidebarController = loader.getController();
            
            mainPane.setLeft(sidebar);

            // Bind sidebar width to the smaller of 25% of total width or 300px
            sidebar.prefWidthProperty().bind(
                Bindings.createDoubleBinding(() -> 
                    Math.min(mainPane.getWidth() * 0.25, 300),
                    mainPane.widthProperty()
                )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            search.requestFocus();
            setupActivityListeners(search.getScene());
        });
    }

    private final PauseTransition inactivityTimer = new PauseTransition(Duration.seconds(20));

    private void resetInactivityTimer() {
        inactivityTimer.stop();
        inactivityTimer.setOnFinished(event -> {
            if (!search.isFocused()) {
                search.requestFocus();
            }
        });
        inactivityTimer.playFromStart();
    }

    public void setupActivityListeners(Scene scene) {
        scene.addEventFilter(MouseEvent.ANY, e -> resetInactivityTimer());
        scene.addEventFilter(KeyEvent.ANY, e -> search.requestFocus());
    }

    @FXML
    private void handleCheckInClick(ActionEvent event) {
        // your handling code here, e.g.:
        System.out.println("Check In button clicked!");
    }

    @FXML
    private void handleCalendarClick(MouseEvent event) {
        // your handling code here, e.g.:
        if (sidebarController != null) {
            sidebarController.handleCalendarClick(event);
        }
    }

    @FXML
    private void handleCancelClick(MouseEvent event) {
        // your handling code here, e.g.:
        search.clear();
    }

}
