package com.betabase;

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
        Parent root = FXMLLoader.load(getClass().getResource("/com/betabase/views/dashboard.fxml"));

        // Get screen bounds
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double width = screenBounds.getWidth() * 0.75;
        double height = screenBounds.getHeight() * 0.75;

        // Set scene with calculated dimensions
        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle("BetaBase");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
