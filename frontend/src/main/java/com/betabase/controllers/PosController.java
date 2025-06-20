package com.betabase.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PosController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private TextField search;
    @FXML private SplitPane splitPane;
    
    private SidebarController sidebarController;

    public void setMenuOpen(boolean menuOpen) {
        sidebarController.setMenuOpen(menuOpen);
    }

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

            // Highlight POS sidebar button
            SidebarController sidebarController = loader.getController();
            sidebarController.setActiveSection("pos");

            Platform.runLater(() -> {

                // Set divider position between 35% and (the lower of 750px or 60%) of the screen
                double minPosition = 0.35;

                splitPane.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) -> {
                    double width = splitPane.getWidth(); // Get current width of the SplitPane
                    double maxPositionByPixel = 750.0 / width;
                    double maxPosition = Math.min(0.6, maxPositionByPixel); // Take the smaller of 60% or 750px

                    if (newVal.doubleValue() < minPosition) {
                        splitPane.setDividerPositions(minPosition);
                    } else if (newVal.doubleValue() > maxPosition) {
                        splitPane.setDividerPositions(maxPosition);
                    }
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleCancelClick(MouseEvent event) {
        // your handling code here, e.g.:
        search.clear();
    }

}
