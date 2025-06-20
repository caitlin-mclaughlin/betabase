package com.betabase.controllers;

import com.betabase.models.Member;
import com.betabase.services.MemberApiService;
import com.betabase.utils.AuthSession;
import com.betabase.utils.SceneManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
    @FXML private Label memberLabel;

    private SidebarController sidebarController;
    private boolean firstLoad;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void setMenuOpen(boolean menuOpen) {
        if (sidebarController != null) {
            sidebarController.setMenuOpen(menuOpen);
        } else {
            firstLoad = true;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/sidebar.fxml"));
            VBox sidebar = loader.load();
            sidebarController = loader.getController();
            mainPane.setLeft(sidebar);
            
            if (firstLoad) {
                sidebarController.setMenuOpen(true);
            }

            sidebar.prefWidthProperty().bind(
                Bindings.createDoubleBinding(() ->
                    Math.min(mainPane.getWidth() * 0.25, 300),
                    mainPane.widthProperty()
                )
            );

            // ListView setup to maintain style during backend integration
            memberList.setCellFactory(lv -> new ListCell<>() {
                private final Button checkInButton = new Button();
                private final Button checkOutButton = new Button();
                private final Region spacer = new Region();
                private final Label nameLabel = new Label();
                private final Label memberIdLabel = new Label();
                private final Label phoneLabel = new Label();
                private final Label emailLabel = new Label();
                private final HBox content = new HBox(30);

                {
                    // Add style classes
                    checkInButton.getStyleClass().add("check-in-narrow");
                    checkOutButton.getStyleClass().add("check-out-narrow");
                    nameLabel.getStyleClass().add("list-label");
                    memberIdLabel.getStyleClass().add("list-label");
                    phoneLabel.getStyleClass().add("list-label");
                    emailLabel.getStyleClass().add("list-label");

                    checkInButton.setText("Check In");
                    checkOutButton.setText("Check Out");

                    // Consistent column widths
                    nameLabel.setPrefWidth(260);
                    memberIdLabel.setPrefWidth(110);
                    phoneLabel.setPrefWidth(145);
                    emailLabel.setPrefWidth(225);

                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    checkInButton.visibleProperty().bind(this.hoverProperty());
                    checkInButton.setOnMouseClicked(event -> handleCheckInOut(null, true));
                    checkInButton.setManaged(true);
                    checkOutButton.visibleProperty().bind(this.hoverProperty());
                    checkOutButton.setManaged(true);
                    checkOutButton.setOnMouseClicked(event -> handleCheckInOut(null, false));
                        
                    // Add labels to row
                    content.getChildren().addAll(nameLabel, memberIdLabel, phoneLabel, emailLabel,
                                                 spacer, checkInButton, checkOutButton);
                    content.setAlignment(Pos.CENTER_LEFT);
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
                        memberIdLabel.setText(member.getMemberId());
                        phoneLabel.setText(member.getPhoneNumber());
                        emailLabel.setText(member.getEmail());

                        setText(null);
                        setGraphic(content);
                    }
                }
            });
            memberList.setFixedCellSize(40);

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
                        openMemberWindow(selected.getId());
                    }
                }
            });

            memberList.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER -> {
                        Member selected = memberList.getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            handleCheckInOut(selected, selected.getChecked());
                            openMemberWindow(selected.getId());
                        }
                    }
                }
            });
        });
    }

    private void handleCheckInOut(Member member, Boolean checked) {
        Member selected;
        if (member == null) {
            selected = memberList.getSelectionModel().getSelectedItem();
        } else {
            selected = member;
        }
        // Only send check in/out call to backend if the member's state is different
        //  i.e. only checkIn if member is currently checked out
        //  Unnecessary calls will bog down the member log
        if (selected.getChecked() ^ checked) selected.setChecked(checked);
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
        scene.addEventFilter(ActionEvent.ANY, e -> resetInactivityTimer());
        scene.addEventFilter(ActionEvent.ANY, e -> search.requestFocus());
    }

    @FXML
    private void handleCalendarClick(MouseEvent event) {
        if (sidebarController != null) {
            sidebarController.handleCalendarClick(event);
        }
    }

    @FXML
    private void handleMemberClick(MouseEvent event) {
        if (sidebarController != null) {
            sidebarController.handleMemberClick(event);
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

                String jwt = AuthSession.getToken();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Authorization", "Bearer " + jwt)
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
    }

    private void updateListView(List<Member> members) {
        memberList.getItems().clear();
        memberList.getItems().addAll(members);
    }

    private void openMemberWindow(Long memberId) {
        SceneManager.switchScene(
            new Stage(), 
            (MemberController controller) -> {
                controller.setApiService(new MemberApiService());
                controller.setMember(memberId);
            },
            "/com/betabase/views/member.fxml",
            true
        );
        search.clear();
    }
    
}
