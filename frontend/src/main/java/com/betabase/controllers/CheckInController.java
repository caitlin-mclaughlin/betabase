package com.betabase.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.betabase.models.Member;
import com.betabase.models.MemberLogEntry;
import com.betabase.services.MemberApiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import javafx.util.Duration;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.ResourceBundle;

public class CheckInController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private ImageView logoImage;
    @FXML private TextField search;
    @FXML private HBox searchBox;
    @FXML private Button checkInHeaderButton;
    @FXML private Button checkOutHeaderButton;
    @FXML private Label memberLabel;

    private VBox sidebar;
    private SidebarController sidebarController;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML private Pane floatingPane;
    @FXML private ListView<Member> floatingListView;
    @FXML private TableView<MemberLogEntry> checkInTable;

    MemberApiService apiService;

    // Right Sidebar: Member Info
    @FXML private VBox memberDisplay;
    @FXML private Label nameLabel;
    @FXML private Label typeLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label ageLabel;
    @FXML private Label memberIdLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label addressLabel;
    @FXML private Label eNameLabel;
    @FXML private Label ePhoneLabel;
    @FXML private Button checkInButton;
    @FXML private Button checkOutButton;

    private Member currentMember;
    private final ObservableList<MemberLogEntry> logEntries = FXCollections.observableArrayList();

    public void setApiService(MemberApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/sidebar.fxml"));
            sidebar = loader.load();
            
            mainPane.setLeft(sidebar);

            // Highlight POS sidebar button
            sidebarController = loader.getController();
            sidebarController.setActiveSection("member");

            floatingListView.setCellFactory(lv -> new ListCell<>() {
                private final Label nameLabel = new Label();
                private final Label memberIdLabel = new Label();
                private final Label phoneLabel = new Label();
                private final HBox content = new HBox(15);

                {
                    // Add style classes
                    nameLabel.getStyleClass().add("list-label");
                    memberIdLabel.getStyleClass().add("list-label");
                    phoneLabel.getStyleClass().add("list-label");

                    // Consistent column widths
                    nameLabel.setPrefWidth(250);
                    memberIdLabel.setPrefWidth(115);
                    phoneLabel.setPrefWidth(125);

                    // Add labels to row
                    content.getChildren().addAll(nameLabel, memberIdLabel, phoneLabel);
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

                        setText(null);
                        setGraphic(content);
                    }
                }
            });

            floatingListView.setOnMouseClicked(event -> {
                Member selected = floatingListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showFloatingList(false);
                    displayMember(selected);

                    // Force focus back to the search box and highlight text
                    Platform.runLater(() -> {
                        search.requestFocus();
                        search.setText(selected.getPrefName());
                        search.positionCaret(search.getText().length());
                    });
                }
            });

            // Check In Log Table setup
            // Create columns
            TableColumn<MemberLogEntry, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
            nameCol.getStyleClass().add("left-rounded-cell");

            TableColumn<MemberLogEntry, String> checkInCol = new TableColumn<>("Check In");
            checkInCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckInTime()));

            TableColumn<MemberLogEntry, String> checkOutCol = new TableColumn<>("Check Out");
            checkOutCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckOutTime()));

            TableColumn<MemberLogEntry, String> membershipCol = new TableColumn<>("Membership");
            membershipCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMembershipType()));

            TableColumn<MemberLogEntry, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhoneNumber()));
            phoneCol.getStyleClass().add("right-rounded-cell");
            
            // Optional column resizing behavior
            checkInTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            // Add columns to the table
            checkInTable.getColumns().addAll(nameCol, checkInCol, checkOutCol, membershipCol, phoneCol);
            checkInTable.setItems(logEntries);

            checkInTable.getItems()

            if (checkInTable.getItems().isEmpty()) {
                MemberLogEntry nullMember = new MemberLogEntry();
                logEntries.add(nullMember);
            }

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
                } else if (!oldVal.isEmpty()) {
                    search.positionCaret(search.getText().length());
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
    }

    @FXML
    private void handleCheckIn(MouseEvent event) {
        // Prevent duplicate check-ins
        for (MemberLogEntry entry : logEntries) {
            if (entry.getName().equals(currentMember.getFirstName()) && entry.getCheckOutTime().equals(" — ")) {
                return; // Already checked in
            }
        }
        MemberLogEntry entry = new MemberLogEntry(currentMember, LocalDateTime.now());
        logEntries.add(0, entry);
        checkInTable.scrollTo(0);

        // Update actively displayed member
        currentMember.setChecked(true);
        handleSave();
        
        // Swap visible button:     check in -> check out
        checkInButton.setVisible(false);
        checkInButton.setManaged(false);
        checkInHeaderButton.setVisible(false);
        checkInHeaderButton.setManaged(false);
        checkOutButton.setVisible(true);
        checkOutButton.setManaged(true);
        checkOutHeaderButton.setVisible(true);
        checkOutHeaderButton.setManaged(true);
    }
    
    @FXML
    private void handleCheckOut(MouseEvent event) {
        MemberLogEntry entry;
        for (int i=0; i < logEntries.size(); i++) {
            entry = logEntries.get(i);
            if (entry.getName().equals(currentMember.getFirstName()) && entry.getCheckOutTime().equals(" — ")) {
                entry.setCheckOutTime(LocalDateTime.now());

                // Trigger a table update
                logEntries.set(i, entry);
                checkInTable.scrollTo(i);
                break;
            }
        }

        // Update actively displayed member
        currentMember.setChecked(false);
        handleSave();

        // Swap visible button
        checkInButton.setVisible(true);
        checkInButton.setManaged(true);
        checkInHeaderButton.setVisible(true);
        checkInHeaderButton.setManaged(true);
        checkOutButton.setVisible(false);
        checkOutButton.setManaged(false);
        checkOutHeaderButton.setVisible(false);
        checkOutHeaderButton.setManaged(false);
    }

    private void handleSave() {
        try {
            boolean success = apiService.updateMember(currentMember);
            if (success) {
                // maybe show a confirmation
                System.out.println("\nDEBUG: SUCCESS - member info saved\n");
            } else {
                // show error
                System.out.println("\nDEBUG: member info could not be saved\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // show error dialog
        }
    }

    private void displayMember(Member member) {
        currentMember = member;
        // Display sidebar
        memberDisplay.setVisible(true);
        memberDisplay.setManaged(true);
        
        // Validate and display member info
        nameLabel.setText(member.getLastName() + ",  " + member.getFirstName() + "  \"" + 
                          member.getPrefName() + "\"  (" + member.getPronouns() + ")");
        phoneLabel.setText(member.getPhoneNumber());
        emailLabel.setText(member.getEmail());
        memberIdLabel.setText(member.getMemberId());
        memberSinceLabel.setText(member.getMemberSince() != null ? member.getMemberSince().toString()  : "MM/DD/YYYY");
        addressLabel.setText(member.getAddress());
        eNameLabel.setText(member.getEmergencyContactName());
        ePhoneLabel.setText(member.getEmergencyContactPhone());

        // Calculate and display age and member since
        LocalDate dob = member.getDateOfBirth();
        String age = dob != null ? String.format("%s", Year.now().getValue() - dob.getYear()) : "Unknown";
        ageLabel.setText(age);
        LocalDate dom = member.getMemberSince();
        String memSince = dom != null ? String.format("%s", Year.now().getValue() - dom.getYear()) : "N/A";
        memberSinceLabel.setText(memSince);

        // Style and display member type
        String type = member.getType().toUpperCase();
        typeLabel.setText(type);
        String bgColor = switch (type) {
            case "ADMIN" -> "-fx-color-pos3";
            case "MEMBER" -> "-fx-color-pos1";
            case "STAFF" -> "-fx-color-pos4";
            case "VISITOR" -> "-fx-color-pos2";
            default -> "-fx-accent-color";
        };
        typeLabel.setStyle(String.format(
            "-fx-background-color: %s;", bgColor
        ));
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


    private void updateListView(List<Member> members) {
        floatingListView.getItems().setAll(members);
        if (!members.isEmpty()) {
            showFloatingList(true);
        } else {
            showFloatingList(false);
        }
    }

    private void showFloatingList(Boolean show) {
        floatingPane.setMouseTransparent(!show);
        floatingListView.setVisible(show);
    }
}
