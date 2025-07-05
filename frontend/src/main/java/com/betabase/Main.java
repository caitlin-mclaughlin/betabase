package com.betabase;

import com.betabase.services.CompositeMemberService;
import com.betabase.services.GymApiService;
import com.betabase.services.MembershipApiService;
import com.betabase.services.UserApiService;
import com.betabase.utils.SceneManager;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        UserApiService userApiService = new UserApiService();
        MembershipApiService membershipApiService = new MembershipApiService();
        CompositeMemberService compositeMemberService = new CompositeMemberService(userApiService, membershipApiService);
        GymApiService gymApiService = new GymApiService();

        // Set services in SceneManager
        SceneManager.setServices(userApiService, membershipApiService, compositeMemberService, gymApiService);

        // Get screen bounds
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double width = screenBounds.getWidth() * 0.8;
        double height = screenBounds.getHeight() * 0.8;

        primaryStage.setMinWidth(width - 150);
        primaryStage.setMinHeight(height - 100);

        // Load initial scene
        SceneManager.switchScene(primaryStage, controller -> {},
        "/com/betabase/views/gymLogin.fxml", false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
