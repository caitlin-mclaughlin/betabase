package com.betabase.controllers;

import com.betabase.interfaces.ServiceAware;
import com.betabase.models.CompositeMember;
import com.betabase.models.Membership;
import com.betabase.models.User;
import com.betabase.services.CompositeMemberService;
import com.betabase.services.GymApiService;
import com.betabase.services.MembershipApiService;
import com.betabase.services.UserApiService;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController implements Initializable, ServiceAware {

    @FXML private BorderPane mainPane;
    @FXML private ImageView logoImage;
    @FXML private TextField search;
    @FXML private ListView<CompositeMember> memberList;
    @FXML private Label userLabel;

    private SidebarController sidebarController;
    private boolean firstLoad;

    private UserApiService userService;
    private MembershipApiService membershipService;
    private CompositeMemberService compositeMemberService;

    @Override
    public void setServices(UserApiService userService, MembershipApiService membershipService,
                            CompositeMemberService compositeMemberService, GymApiService gymService) {
        this.userService = userService;
        this.membershipService = membershipService;
        this.compositeMemberService = compositeMemberService;
    }

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
                private final Label userIdLabel = new Label();
                private final Label phoneLabel = new Label();
                private final Label emailLabel = new Label();
                private final HBox content = new HBox(30);

                {
                    // Add style classes
                    checkInButton.getStyleClass().add("check-in-narrow");
                    checkOutButton.getStyleClass().add("check-out-narrow");
                    nameLabel.getStyleClass().add("list-label");
                    userIdLabel.getStyleClass().add("list-label");
                    phoneLabel.getStyleClass().add("list-label");
                    emailLabel.getStyleClass().add("list-label");

                    checkInButton.setText("Check In");
                    checkOutButton.setText("Check Out");

                    // Consistent column widths
                    nameLabel.setPrefWidth(260);
                    userIdLabel.setPrefWidth(110);
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
                    content.getChildren().addAll(nameLabel, userIdLabel, phoneLabel, emailLabel,
                                                 spacer, checkInButton, checkOutButton);
                    content.setAlignment(Pos.CENTER_LEFT);
                }

                @Override
                protected void updateItem(CompositeMember member, boolean empty) {
                    super.updateItem(member, empty);
                    if (empty || member == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        User u = member.getUser();

                        nameLabel.setText(member.getFullName());
                        userIdLabel.setText(member.getMembership().getMembershipId());
                        phoneLabel.setText(u.getPhoneNumber());
                        emailLabel.setText(u.getEmail());

                        // You can use membership.getType() or .isActive() to style check-in/out

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
                    CompositeMember selected = memberList.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        openUserWindow(selected);
                    }
                }
            });

            memberList.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER -> {
                        CompositeMember selected = memberList.getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            handleCheckInOut(selected, selected.getMembership().getChecked());
                            openUserWindow(selected);
                        }
                    }
                }
            });
        });
    }

    private void handleCheckInOut(CompositeMember member, boolean checked) {
        CompositeMember selected;
        if (member == null) {
            selected = memberList.getSelectionModel().getSelectedItem();
        } else {
            selected = member;
        }
        // Only send check in/out call to backend if the member's state is different
        //  i.e. only checkIn if member is currently checked out
        //  Unnecessary calls will bog down the member log
        if (selected.getMembership().getChecked() ^ checked) selected.getMembership().setChecked(checked);
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
    private void handleUserClick(MouseEvent event) {
        if (sidebarController != null) {
            sidebarController.handleUserClick(event);
        }
    }

    @FXML
    private void handleCancelClick(MouseEvent event) {
        search.clear();
    }

    private void searchCompositeMembersAsync(String query, Long gymId) {
        new Thread(() -> {
            try {
                List<User> users = userService.searchUsers(query);
                List<CompositeMember> results = new ArrayList<>();

                for (User user : users) {
                    Membership membership = membershipService.getForUserAndGym(user.getId(), gymId);
                    if (membership != null) {
                        results.add(new CompositeMember(user, membership));
                    } else {
                        System.out.println("No membership found for the given user and gym.");
                    }
                }

                Platform.runLater(() -> updateListViewWithComposite(results));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void performSearch(String query) {
        Long currentGymId = AuthSession.getCurrentGymId();
        if (currentGymId != null) {
            searchCompositeMembersAsync(query, currentGymId);
        }
    }

    private void updateListViewWithComposite(List<CompositeMember> members) {
        memberList.getItems().clear();
        for (CompositeMember cm : members) {
            cm.getMembership().setChecked(cm.getMembership().getChecked());
            memberList.getItems().add(cm);
        }
    }

    private void updateListView(List<CompositeMember> members) {
        if (members == null || members.size() == 0) {
            System.out.println("\nDEBUG: attempting to display null list of users\n");
            return;
        }
        memberList.getItems().clear();
        memberList.getItems().addAll(members);
    }

    private void openUserWindow(CompositeMember member) {
        SceneManager.switchScene(
            new Stage(), 
            (MemberController controller) -> {
                controller.setMember(member);
            },
            "/com/betabase/views/user.fxml",
            true
        );
        search.clear();
    }
    
}
