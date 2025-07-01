package com.betabase.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.betabase.interfaces.ServiceAware;
import com.betabase.services.GymApiService;
import com.betabase.services.MemberApiService;
import com.betabase.utils.SceneManager;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsController implements Initializable, ServiceAware {

    @FXML private BorderPane mainPane;

    private SidebarController sidebarController;
    
    private MemberApiService memberService;
    private GymApiService gymService;

    @Override
    public void setServices(MemberApiService memberService, GymApiService gymService) {
        this.memberService = memberService;
        this.gymService = gymService;
    }

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

            // Highlight Settings sidebar button
            SidebarController sidebarController = loader.getController();
            sidebarController.setActiveSection("settings");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        gymService.logout();

        SceneManager.switchScene(
            (Stage) mainPane.getScene().getWindow(),
            (GymLoginController controller) -> {},
            "/com/betabase/views/gymLogin.fxml",
            false
        );
    }   
}