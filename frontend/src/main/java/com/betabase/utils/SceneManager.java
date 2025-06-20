package com.betabase.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

import java.net.URL;

import com.betabase.services.MemberApiService;

public class SceneManager {

    private static final MemberApiService apiService= new MemberApiService();
    private static String defaultTitle = "Betabase";

    public static <T> void switchScene(Stage stage, Consumer<T> controllerConfigurator, 
                                         String fxmlPath, Boolean newWindow) {
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

            if (newWindow) {
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
                stage.showAndWait();
            } else {
                stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
