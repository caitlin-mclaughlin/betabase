package com.betabase.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import com.betabase.controllers.MemberController;
import com.betabase.models.Member;
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

    public static void switchToMemberWindow(Stage stage, Member member) {
        try {
            // Load fxml scene and show next stage
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/com/betabase/views/member.fxml"));
            Parent root = loader.load();

            // Set member and api Service
            MemberController controller = loader.getController();
            if (controller != null) {
                controller.setMember(member);
                controller.setApiService(apiService);
            } else {
                System.out.println("\nDEBUG: Null member controller\n");
            }
            
            // Show stage
            stage.setTitle("Member Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace(); // Or show an error dialog
        }
    }
}
