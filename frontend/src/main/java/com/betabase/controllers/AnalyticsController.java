package com.betabase.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AnalyticsController implements Initializable {

    @FXML private BorderPane mainPane;
    
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

            // Highlight Analytics sidebar button
            SidebarController sidebarController = loader.getController();
            sidebarController.setActiveSection("analytics");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}