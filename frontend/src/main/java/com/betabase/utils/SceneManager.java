package com.betabase.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

import java.net.URL;

import com.betabase.services.MemberApiService;

public class SceneManager {

    private static final MemberApiService apiService= new MemberApiService();
    private static String defaultTitle = "Betabase";

    public static void switchScene(Stage stage, String fxmlPath) {
        try {
            // Load fxml scene and show next stage
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));

            // Show stage
            stage.setTitle(defaultTitle);
            stage.setScene(new Scene(loader.load(), stage.getWidth(), stage.getHeight()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Or show an error dialog
        }
    }

    public static <T> void switchToAPIWindow(Stage stage, Consumer<T> controllerConfigurator, String fxmlPath) {
        try {
            URL location = SceneManager.class.getResource(fxmlPath);
            if (location == null) {
                throw new IllegalArgumentException("FXML file not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(location);

            // Defer controller config until after instance is created by FXMLLoader
            Parent root = loader.load();
            T controller = loader.getController();
            if (controllerConfigurator != null) {
                controllerConfigurator.accept(controller);
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
