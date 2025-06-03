package com.betabase.controllers;
import com.betabase.models.Member;

import javafx.event.ActionEvent;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DashboardController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private ImageView logoImage;
    @FXML private TextField search;
    @FXML private ListView<String> memberList;

    SidebarController sidebarController;

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            search.requestFocus();
            setupActivityListeners(search.getScene());

            search.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    performSearch(newValue);
                } else {
                    memberList.getItems().clear();
                }
            });

        });
    }

    private final PauseTransition inactivityTimer = new PauseTransition(Duration.seconds(20));

    private void resetInactivityTimer() {
        inactivityTimer.stop();
        inactivityTimer.setOnFinished(event -> {
            if (!search.isFocused()) {
                search.requestFocus();
            }
        });
        inactivityTimer.playFromStart();
    }

    public void setupActivityListeners(Scene scene) {
        scene.addEventFilter(MouseEvent.ANY, e -> resetInactivityTimer());
        scene.addEventFilter(KeyEvent.ANY, e -> search.requestFocus());
    }

    @FXML
    private void handleCheckInClick(ActionEvent event) {
        // your handling code here, e.g.:
        System.out.println("Check In button clicked!");
    }

    @FXML
    private void handleCalendarClick(MouseEvent event) {
        // your handling code here, e.g.:
        if (sidebarController != null) {
            sidebarController.handleCalendarClick(event);
        }
    }

    @FXML
    private void handleCancelClick(MouseEvent event) {
        // your handling code here, e.g.:
        search.clear();
    }

    private void performSearch(String query) {
        new Thread(() -> {
            try {
                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
                URL url = new URL("http://localhost:8080/api/members/search?query=" + encodedQuery);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    List<Member> members = mapper.readValue(conn.getInputStream(), new TypeReference<>() {});
                    Platform.runLater(() -> updateListView(members));
                } else {
                    System.err.println("Search failed with code: " + conn.getResponseCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateListView(List<Member> members) {
        memberList.getItems().clear();
        for (Member member : members) {
            String display = String.format("%s, %s (%s)", 
                member.getLastName(), 
                member.getFirstName(), 
                member.getPrefName() != null ? member.getPrefName() : "");
            memberList.getItems().add(display);
        }
    }

}
