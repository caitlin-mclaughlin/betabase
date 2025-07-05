package com.betabase.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

import java.net.URL;

import com.betabase.interfaces.ServiceAware;
import com.betabase.services.CompositeMemberService;
import com.betabase.services.GymApiService;
import com.betabase.services.MembershipApiService;
import com.betabase.services.UserApiService;

public class SceneManager {

    private static UserApiService userService;
    private static MembershipApiService membershipService;
    private static CompositeMemberService compositeMemberService;
    private static GymApiService gymService;

    private static final String defaultTitle = "Betabase";

    public static void setServices(UserApiService user, MembershipApiService membership,
                                   CompositeMemberService compositeMember, GymApiService gym) {
        userService = user;
        membershipService = membership;
        compositeMemberService = compositeMember;
        gymService = gym;
    }

    public static <T> void switchScene(Stage stage, Consumer<T> controllerConfigurator, 
                                       String fxmlPath, boolean newWindow) {
        try {
            URL location = SceneManager.class.getResource(fxmlPath);
            if (location == null) {
                throw new IllegalArgumentException("FXML not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();
            T controller = loader.getController();

            // Automatically inject services if setServices() method exists
            if (controller instanceof ServiceAware sa) {
                sa.setServices(userService, membershipService, compositeMemberService, gymService);
            }

            if (controllerConfigurator != null) {
                controllerConfigurator.accept(controller);
            }

            if (newWindow) {
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.showAndWait();
            } else {
                stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

