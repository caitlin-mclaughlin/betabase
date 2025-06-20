package com.betabase;

import com.betabase.controllers.DashboardController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the root FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/gymLogin.fxml"));
        Parent root = loader.load();

        // Get screen bounds
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double width = screenBounds.getWidth() * 0.8;
        double height = screenBounds.getHeight() * 0.8;

        // Set scene with calculated dimensions
        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle("BetaBase");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(width - 150);
        primaryStage.setMinHeight(height - 100);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
