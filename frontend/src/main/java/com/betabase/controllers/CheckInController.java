package com.betabase.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import com.betabase.enums.UserType;
import com.betabase.enums.PronounsType;
import com.betabase.interfaces.ServiceAware;
import com.betabase.models.CompositeMember;
import com.betabase.models.Membership;
import com.betabase.models.User;
import com.betabase.models.MemberLogEntry;
import com.betabase.services.CompositeMemberService;
import com.betabase.services.GymApiService;
import com.betabase.services.MembershipApiService;
import com.betabase.services.UserApiService;
import com.betabase.utils.AuthSession;
import com.betabase.utils.SceneManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.ResourceBundle;

public class CheckInController implements Initializable, ServiceAware {

    @FXML private BorderPane mainPane;
    @FXML private ImageView logoImage;
    @FXML private TextField search;
    @FXML private HBox searchBox;
    @FXML private Button checkInHeaderButton, checkOutHeaderButton;
    @FXML private Label userLabel;

    private VBox sidebar;
    private SidebarController sidebarController;

    @FXML private Pane floatingPane;
    @FXML private ListView<CompositeMember> floatingListView;
    @FXML private TableView<MemberLogEntry> checkInTable;

    // Right Sidebar: User Info
    @FXML private VBox userDisplay;
    @FXML private Label nameLabel, phoneLabel, emailLabel, ageLabel, addressLabel, eNameLabel, ePhoneLabel, eEmailLabel;
    @FXML private Label typeLabel, membershipIdLabel, userSinceLabel;
    @FXML private Button checkInButton, checkOutButton;

    private CompositeMember currentMember;
    private final ObservableList<MemberLogEntry> logEntries = FXCollections.observableArrayList();
    private CompositeMemberService compositeMemberService;

    @Override
    public void setServices(UserApiService userService, MembershipApiService membershipService,
                            CompositeMemberService compositeMemberService, GymApiService gymService) {
        this.compositeMemberService = compositeMemberService;
    }

    public void setMenuOpen(boolean menuOpen) {
        sidebarController.setMenuOpen(menuOpen);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/sidebar.fxml"));
            sidebar = loader.load();
            
            mainPane.setLeft(sidebar);

            // Highlight POS sidebar button
            sidebarController = loader.getController();
            sidebarController.setActiveSection("user");

            floatingListView.setCellFactory(lv -> new ListCell<>() {
                private final Label nameLabel = new Label();
                private final Label membershipIdLabel = new Label();
                private final Label phoneLabel = new Label();
                private final HBox content = new HBox(15);

                {
                    // Add style classes
                    nameLabel.getStyleClass().add("list-label");
                    membershipIdLabel.getStyleClass().add("list-label");
                    phoneLabel.getStyleClass().add("list-label");

                    // Consistent column widths
                    nameLabel.setPrefWidth(250);
                    membershipIdLabel.setPrefWidth(115);
                    phoneLabel.setPrefWidth(125);

                    // Add labels to row
                    content.getChildren().addAll(nameLabel, membershipIdLabel, phoneLabel);
                    content.setAlignment(Pos.CENTER_LEFT);
                }
                @Override
                protected void updateItem(CompositeMember member, boolean empty) {
                    super.updateItem(member, empty);
                    if (empty || member == null || member.getUser() == null || member.getMembership() == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        User u = currentMember.getUser();
                        Membership m = currentMember.getMembership();
                        nameLabel.setText(u.getLastName() + ", " + u.getFirstName()
                                          + " (" + u.getPrefName() + ")");
                        membershipIdLabel.setText(m.getMembershipId());
                        phoneLabel.setText(u.getPhoneNumber());

                        setText(null);
                        setGraphic(content);
                    }
                }
            });

            floatingListView.setOnMouseClicked(event -> {
                CompositeMember selected = floatingListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    System.out.println("\nDEBUG: attempting to display user with id: "+ selected.getId()+"\n");
                    showFloatingList(false);
                    displayUser(selected.getId());

                    // Force focus back to the search box
                    Platform.runLater(() -> {
                        search.requestFocus();
                        search.setText(selected.getUser().getPrefName());
                        showFloatingList(false);
                        search.positionCaret(search.getText().length());
                    });
                }
            });

            // Check In Log Table setup
            // Create columns
            TableColumn<MemberLogEntry, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

            TableColumn<MemberLogEntry, String> checkInCol = new TableColumn<>("Check In");
            checkInCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckInTime()));

            TableColumn<MemberLogEntry, String> checkOutCol = new TableColumn<>("Check Out");
            checkOutCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckOutTime()));

            TableColumn<MemberLogEntry, String> membershipCol = new TableColumn<>("Membership");
            membershipCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMembershipType().toString()));

            TableColumn<MemberLogEntry, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhoneNumber()));
            
            // Optional column resizing behavior
            checkInTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            // Add columns to the table
            checkInTable.getColumns().addAll(nameCol, checkInCol, checkOutCol, membershipCol, phoneCol);
            checkInTable.setItems(logEntries);
            Label placeholder = new Label("No one has checked in yet today");
            placeholder.setStyle("-fx-text-fill: -fx-base-color; -fx-font-size: 18px;");
            checkInTable.setPlaceholder(placeholder);

            checkInTable.setOnMouseClicked(event -> {
                MemberLogEntry selectedLog = checkInTable.getSelectionModel().getSelectedItem();
                if (selectedLog != null && selectedLog.getUserId() != null) {
                    displayUser(selectedLog.getUserId());
                }
            });

            checkInTable.getColumns().addListener((ListChangeListener<TableColumn<MemberLogEntry, ?>>) change -> {
                while (change.next()) {
                    if (change.wasPermutated() || change.wasReplaced() || change.wasUpdated()) {
                        // Optional: skip, unless reordering
                    }
                    updateFirstColumnStyle(checkInTable);
                }
            });

            updateFirstColumnStyle(checkInTable); // initial call

            checkInTable.widthProperty().addListener((obs, oldVal, newVal) -> {
                double totalWidth = newVal.doubleValue();
                int columnCount = checkInTable.getColumns().size();
                for (TableColumn<?, ?> column : checkInTable.getColumns()) {
                    column.setPrefWidth((totalWidth - 20) / columnCount); // -25 for borders/padding
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            floatingListView.setFixedCellSize(35);
            
            // Match width to search field dynamically
            search.localToSceneTransformProperty().addListener((obs, oldVal, newVal) -> positionFloatingList());
            searchBox.widthProperty().addListener((obs, oldVal, newVal) -> floatingListView.setPrefWidth(newVal.doubleValue()));
            searchBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    // Wait for layout pass
                    Platform.runLater(this::positionFloatingList);
                    newScene.windowProperty().addListener((wObs, wOld, wNew) -> {
                        if (wNew != null) {
                            wNew.widthProperty().addListener((x, y, z) -> positionFloatingList());
                            wNew.heightProperty().addListener((x, y, z) -> positionFloatingList());
                        }
                    });
                }
            });
        });

        Platform.runLater(() -> {
            search.requestFocus();
            setupActivityListeners(search.getScene());

            search.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    positionFloatingList();
                    performSearch(newVal);
                } else {
                    showFloatingList(false);
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
                search.positionCaret(search.getText().length());
            }
        });
        inactivityTimer.playFromStart();
    }

    public void setupActivityListeners(Scene scene) {
        scene.addEventFilter(MouseEvent.ANY, e -> resetInactivityTimer());
        scene.addEventFilter(KeyEvent.ANY, e -> search.requestFocus());
    }

    @FXML
    private void handleCancelClick(MouseEvent event) {
        search.clear();
        showFloatingList(false);
    }

    private void performSearch(String query) {
        compositeMemberService.searchCompositeMembers(
            query,
            AuthSession.getCurrentGymId(),
            members -> updateListView(members),
            error -> {
                System.out.println("\nDEBUG: Search failed\n");
                error.printStackTrace();
            }
        );
    }

    @FXML
    private void handleCheckIn(MouseEvent event) {
        // Prevent duplicate check-ins
        for (MemberLogEntry entry : logEntries) {
            if (currentMember != null && currentMember.getId().equals(entry.getUserId())
                    && entry.getCheckOutTime().equals(" — ")) {
                return; // Already checked in
            }
        }
        // Create log entry
        MemberLogEntry entry = new MemberLogEntry(currentMember, LocalDateTime.now());
        logEntries.add(0, entry);
        checkInTable.scrollTo(0);

        // Update actively displayed user
        currentMember.setChecked(true);
        handleSave();
    }
    
    @FXML
    private void handleCheckOut(MouseEvent event) {
        MemberLogEntry entry;
        for (int i=0; i < logEntries.size(); i++) {
            entry = logEntries.get(i);
            if (currentMember != null && currentMember.getId().equals(entry.getUserId())
                    && entry.getCheckOutTime().equals(" — ")) {
                entry.setCheckOutTime(LocalDateTime.now());

                // Trigger a table update
                logEntries.set(i, entry);
                checkInTable.scrollTo(i);
                break;
            }
        }

        // Update actively displayed user
        currentMember.setChecked(false);
        handleSave();
    }

    @FXML
    private void handleNewUser(MouseEvent event) {
        SceneManager.switchScene(
            new Stage(), 
            (MemberController controller) -> {},
            "/com/betabase/views/user.fxml",
            true
        );
    }

    private void handleSave() {
        try {
            currentMember = compositeMemberService.updateCompositeMember(currentMember);
            System.out.println("\nDEBUG: SUCCESS - user info saved\n");
            if (currentMember != null) {
                displayUser(currentMember.getId());
            } else {
                System.out.println("\nDEBUG: something went wrong saving user info\n");
            }
        } catch (Exception e) {
            System.out.println("\nDEBUG: user info could not be saved\n");
            e.printStackTrace();
        }
    }

    private void displayUser(Long userId) {
        Long gymId = AuthSession.getCurrentGymId();
        compositeMemberService.getCompositeMemberById(
            userId,
            gymId,
            member -> {
                currentMember = member;
                if (currentMember == null || !currentMember.exists()) {
                    System.out.println("\nDEBUG: attempting to display null user\n");
                    return;
                }
                User u = currentMember.getUser();
                Membership m = currentMember.getMembership();

                // Display sidebar
                userDisplay.setVisible(true);
                userDisplay.setManaged(true);
                
                // Display user info
                String prefName = !u.getPrefName().isBlank() ? " \"" + u.getPrefName() + "\"" : "";
                String pronouns = u.getPronouns() != PronounsType.UNSET  ? " (" + u.getPrefName() + ")" : "";
                
                nameLabel.setText(u.getLastName() + ",  " + u.getFirstName() + prefName + pronouns);
                phoneLabel.setText(u.getPhoneNumber());
                emailLabel.setText(u.getEmail());
                membershipIdLabel.setText(m.getMembershipId());
                userSinceLabel.setText(m.getUserSince() != null ? m.getUserSince().toString()  : "MM/DD/YYYY");
                addressLabel.setText(u.getAddress().toString());
                eNameLabel.setText(u.getEmergencyContactName());
                ePhoneLabel.setText(u.getEmergencyContactPhone());

                // Calculate and display age and user since
                LocalDate dob = u.getDateOfBirth();
                String age = dob != null ? String.format("%s", Year.now().getValue() - dob.getYear()) : "Unknown";
                ageLabel.setText(age);
                LocalDate dom = m.getUserSince();
                String memSince = dom != null ? String.format("%s", Year.now().getValue() - dom.getYear()) : "N/A";
                userSinceLabel.setText(memSince);

                // Style and display user type
                UserType type = m.getType();
                typeLabel.setText(type.toString().toUpperCase());
                String bgColor = switch (type) {
                    case ADMIN -> "-fx-color-pos3";
                    case MEMBER -> "-fx-color-pos1";
                    case STAFF -> "-fx-color-pos4";
                    case VISITOR -> "-fx-color-pos2";
                    default -> "-fx-accent-color";
                };
                typeLabel.setStyle(String.format(
                    "-fx-background-color: %s;", bgColor
                ));

                // Swap visible button
                setEditableState(currentMember.getChecked());
            },
            error -> {
                System.out.println("\nDEBUG: Failed to load composite member by ID: " + userId);
                error.printStackTrace();
                // Optional: show user-facing error popup
            }
        );
    }

    private void setEditableState(boolean editable) {
        for (Button button : new Button[]{
            checkInButton, checkInHeaderButton
        }) {
            button.setVisible(!editable);
            button.setManaged(!editable);
        }
        for (Button button : new Button[]{
            checkOutButton, checkOutHeaderButton
        }) {
            button.setVisible(editable);
            button.setManaged(editable);
        }
    }

    private void positionFloatingList() {
        // Get scene coordinates of the searchBox
        Point2D sceneCoords = searchBox.localToScene(0, 0);
        Point2D containerCoords = floatingListView.getParent().sceneToLocal(sceneCoords);

        // Apply the coordinates
        floatingListView.setLayoutX(containerCoords.getX());
        floatingListView.setLayoutY(containerCoords.getY() + searchBox.getHeight() + 5);
        floatingListView.setPrefWidth(searchBox.getWidth());
    }

    private void updateFirstColumnStyle(TableView<?> tableView) {
        for (TableColumn<?, ?> column : tableView.getColumns()) {
            column.getStyleClass().remove("first-visible-column");
        }
        if (!tableView.getColumns().isEmpty()) {
            tableView.getColumns().get(0).getStyleClass().add("first-visible-column");
        }
    }

    private void updateListView(List<CompositeMember> users) {
        floatingListView.getItems().setAll(users);
        if (!users.isEmpty()) {
            showFloatingList(true);
        } else {
            showFloatingList(false);
        }
    }

    private void showFloatingList(boolean show) {
        floatingPane.setMouseTransparent(!show);
        floatingListView.setVisible(show);
    }
}
