package com.betabase.controllers;

import com.betabase.models.Member;
import com.betabase.services.MemberApiService;
import com.betabase.utils.SceneManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private ImageView logoImage;
    @FXML private TextField search;
    @FXML private ListView<Member> memberList;

    private SidebarController sidebarController;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/sidebar.fxml"));
            VBox sidebar = loader.load();
            sidebarController = loader.getController();
            mainPane.setLeft(sidebar);

            sidebar.prefWidthProperty().bind(
                Bindings.createDoubleBinding(() ->
                    Math.min(mainPane.getWidth() * 0.25, 300),
                    mainPane.widthProperty()
                )
            );

            // ListView setup to maintain style during backend integration
            memberList.setCellFactory(lv -> new ListCell<>() {
                private final Label nameLabel = new Label();
                private final Label memberIdLabel = new Label();
                private final Label phoneLabel = new Label();
                private final Label emailLabel = new Label();
                private final Region separator = new Region();
                private final HBox content = new HBox(50);
                private final VBox container = new VBox();

                {
                    // Add style classes
                    nameLabel.getStyleClass().add("list-label");
                    memberIdLabel.getStyleClass().add("list-label");
                    phoneLabel.getStyleClass().add("list-label");
                    emailLabel.getStyleClass().add("list-label");

                    // Consistent column widths
                    nameLabel.setMinWidth(150);
                    nameLabel.setMaxWidth(200);
                    memberIdLabel.setMinWidth(100);
                    memberIdLabel.setMinWidth(150);
                    phoneLabel.setMinWidth(125);
                    phoneLabel.setMinWidth(175);
                    emailLabel.setMinWidth(200);
                    emailLabel.setMinWidth(250);

                    // Add labels to row
                    content.getChildren().addAll(nameLabel, memberIdLabel, phoneLabel, emailLabel);
                    content.setPadding(new Insets(5));

                    separator.getStyleClass().add("separator");
                    container.getChildren().addAll(content, separator);
                }

                @Override
                protected void updateItem(Member member, boolean empty) {
                    super.updateItem(member, empty);
                    if (empty || member == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        nameLabel.setText(member.getLastName() + ", " + member.getFirstName()
                                          + " (" + member.getPrefName() + ")");
                        phoneLabel.setText(member.getPhoneNumber());
                        emailLabel.setText(member.getEmail());

                        setText(null);
                        setGraphic(container);
                    }
                }
            });
            memberList.setFixedCellSize(50); // Adjust based on your layout

        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            search.requestFocus();
            setupActivityListeners(search.getScene());

            search.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    performSearch(newVal);
                } else {
                    memberList.getItems().clear();
                }
            });

            memberList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { // double-click
                    Member selected = memberList.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        openMemberWindow(selected);
                    }
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
    private void handleCheckInClick(MouseEvent event) {
        System.out.println("\nDEBUG: Check In button clicked!\n");
    }

    @FXML
    private void handleCalendarClick(MouseEvent event) {
        if (sidebarController != null) {
            sidebarController.handleCalendarClick(event);
        }
    }

    @FXML
    private void handleCancelClick(MouseEvent event) {
        search.clear();
    }

    private void performSearch(String query) {
        new Thread(() -> {
            try {
                String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
                URI uri = URI.create("http://localhost:8080/api/members/search?query=" + encoded);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    List<Member> members = mapper.readValue(response.body(), new TypeReference<>() {});
                    Platform.runLater(() -> updateListView(members));
                } else {
                    System.err.println("Search failed with code: " + response.statusCode());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        search.clear();
    }

    private void updateListView(List<Member> members) {
        memberList.getItems().clear();
        memberList.getItems().addAll(members);
    }

    private void openMemberWindow(Member member) {
        SceneManager.switchToMemberWindow(new Stage(), member);
    }
}
