package com.betabase.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import com.betabase.enums.*;
import com.betabase.interfaces.ServiceAware;
import com.betabase.models.Address;
import com.betabase.models.CompositeMember;
import com.betabase.models.Membership;
import com.betabase.models.User;
import com.betabase.services.CompositeMemberService;
import com.betabase.services.GymApiService;
import com.betabase.services.MembershipApiService;
import com.betabase.services.UserApiService;
import com.betabase.utils.AuthSession;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class MemberController implements Initializable, ServiceAware {

    @FXML private Label nameLabel, typeLabel, genderLabel, phoneLabel, emailLabel, dobLabel, userIdLabel, userSinceLabel;
    @FXML private Label addressLabel1, addressLabel2, eNameLabel, ePhoneLabel, eEmailLabel;
    @FXML private TextField firstNameField, lastNameField, prefNameField, phoneField, emailField, userIdField;
    @FXML private TextField streetField1, streetField2, cityField, zipField, stateField;
    @FXML private TextField eNameField, ePhoneField, eEmailField;
    @FXML private ChoiceBox<UserType> typeField;
    @FXML private ChoiceBox<GenderType> genderField;
    @FXML private ChoiceBox<PronounsType> pronounsField;
    @FXML private DatePicker dobField;
    @FXML private Button editButton, checkInButton, checkOutButton, saveButton, cancelButton;
    @FXML private HBox staffButtons;

    private CompositeMember currentMember;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (currentMember == null)
            currentMember = new CompositeMember(new User(), new Membership());

        typeField.setItems(FXCollections.observableArrayList(UserType.values()));
        genderField.setItems(FXCollections.observableArrayList(GenderType.values()));
        pronounsField.setItems(FXCollections.observableArrayList(PronounsType.values()));

        setupPhoneFormatter(phoneField);
        setupPhoneFormatter(ePhoneField);

        handleEdit(null); // assume new entry if no currentMember was set externally
    }

    public void setMember(CompositeMember member) {
        this.currentMember = member;
        try {
            currentMember.setMembership(
                membershipService.getForUserAndGym(member.getId(), AuthSession.getCurrentGymId())
            );
        } catch (Exception e) {
            System.err.println("Could not load membership: " + e.getMessage());
        }
        setEditableState(false);
        updateDisplayFromMember(currentMember);
    }

    @FXML private void handleClockIn(MouseEvent e) { System.out.println("Clock in"); }
    @FXML private void handleClockOut(MouseEvent e) { System.out.println("Clock out"); }
    @FXML private void handleEdit(ActionEvent e) { setEditableState(true); setFieldPrompts(currentMember); }
    @FXML private void handleCancel(MouseEvent e) { setEditableState(false); updateDisplayFromMember(currentMember); }

    @FXML
    private void handleSave(MouseEvent e) {
        if (!validateFields()) return;

        try {
            boolean isNew = currentMember == null || 
                            currentMember.getUser() == null || 
                            currentMember.getId() == null;
            updateCurrentMemberFromFields();

            if (isNew) {
                User createdUser = userService.createUser(currentMember.getUser());

                Membership membership = new Membership(createdUser.getId(), AuthSession.getCurrentGymId(), 
                                                       UserType.MEMBER, LocalDate.now());
                membershipService.createMembership(membership);
                currentMember = new CompositeMember(createdUser, membership);
            } else {
                currentMember = compositeMemberService.updateCompositeMember(currentMember);
            }

            setEditableState(false);
            updateDisplayFromMember(currentMember);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setupPhoneFormatter(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText().replaceAll("[^\\d]", "");
            if (text.length() > 10) return null;
            StringBuilder formatted = new StringBuilder();
            if (text.length() > 0) formatted.append("(").append(text.substring(0, Math.min(3, text.length())));
            if (text.length() >= 3) formatted.append(") ").append(text.substring(3, Math.min(6, text.length())));
            if (text.length() >= 7) formatted.append("-").append(text.substring(6));
            change.setText(formatted.toString());
            change.setRange(0, change.getControlText().length());
            return change;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
        field.textProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(() -> field.positionCaret(field.getText().length())));
    }

    private void updateCurrentMemberFromFields() {
        User u = currentMember.getUser();
        Membership m = currentMember.getMembership();
        u.setFirstName(firstNameField.getText());
        u.setLastName(lastNameField.getText());
        u.setPrefName(prefNameField.getText());
        u.setPronouns(pronounsField.getValue());
        u.setGender(genderField.getValue());
        m.setType(typeField.getValue());
        u.setPhoneNumber(phoneField.getText().replaceAll("[^\\d]", ""));
        u.setEmail(emailField.getText());
        u.setDateOfBirth(dobField.getValue());
        m.setMembershipId(userIdField.getText());
        u.setAddress(new Address(
            streetField1.getText(), streetField2.getText(), cityField.getText(), stateField.getText(), zipField.getText(), "USA"
        ));
        u.setEmergencyContactName(eNameField.getText());
        u.setEmergencyContactPhone(ePhoneField.getText().replaceAll("[^\\d]", ""));
        u.setEmergencyContactEmail(eEmailField.getText());
    }

    private void updateDisplayFromMember(CompositeMember member) {
        if (member == null || member.getUser() == null || member.getMembership() == null) return;
        User u = member.getUser();
        Membership m = member.getMembership();

        nameLabel.setText(u.getLastName() + ", " + u.getFirstName() + (u.getPrefName().isBlank() ? "" : " (\"" + u.getPrefName() + "\")") +
                          " (" + u.getPronouns() + ")");
        genderLabel.setText(String.valueOf(u.getGender()));
        phoneLabel.setText(u.getPhoneNumber());
        emailLabel.setText(u.getEmail());
        dobLabel.setText(u.getDateOfBirth() != null ? u.getDateOfBirth().toString() : "MM/DD/YYYY");
        userIdLabel.setText(m.getMembershipId());
        userSinceLabel.setText(m.getUserSince() != null ? m.getUserSince().toString() : "MM/DD/YYYY");
        addressLabel1.setText(u.getAddress().toStringLine1());
        addressLabel2.setText(u.getAddress().toStringLine2());
        eNameLabel.setText(u.getEmergencyContactName());
        ePhoneLabel.setText(u.getEmergencyContactPhone());
        eEmailLabel.setText(u.getEmergencyContactEmail());

        typeLabel.setText(m.getType().toString().toUpperCase());
        typeLabel.setStyle("-fx-background-color: " + switch (m.getType()) {
            case ADMIN -> "-fx-color-pos3";
            case STAFF -> "-fx-color-pos4";
            case VISITOR -> "-fx-color-pos2";
            default -> "-fx-color-pos1";
        });
        staffButtons.setVisible(m.getType() == UserType.ADMIN || m.getType() == UserType.STAFF);
    }

    private void setEditableState(boolean editable) {
        for (Control field : new Control[]{
            firstNameField, lastNameField, prefNameField, phoneField, emailField, userIdField,
            streetField1, streetField2, cityField, stateField, zipField, eNameField, ePhoneField, eEmailField,
            genderField, pronounsField, typeField, dobField,
            saveButton, cancelButton
        }) {
            field.setVisible(editable);
            field.setManaged(editable);
        }

        for (Control label : new Control[]{
            nameLabel, typeLabel, genderLabel, phoneLabel, emailLabel, dobLabel, userIdLabel, userSinceLabel,
            addressLabel1, addressLabel2, eNameLabel, ePhoneLabel, eEmailLabel,
            checkInButton, checkOutButton
        }) {
            label.setVisible(!editable);
            label.setManaged(!editable);
        }
    }

    private boolean validateFields() {
        boolean valid = true;

        for (TextField field : new TextField[]{firstNameField, lastNameField, phoneField, emailField,
                                               streetField1, cityField, stateField, zipField,
                                               eNameField, ePhoneField, eEmailField}) {
            if (field.getText().isBlank()) {
                markInvalid(field); valid = false;
            } else {
                clearInvalid(field);
            }
        }

        if (dobField.getValue() == null) {
            markInvalid(dobField); valid = false;
        } else clearInvalid(dobField);

        if (!emailField.getText().matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w+$")) markInvalid(emailField);
        if (!eEmailField.getText().matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w+$")) markInvalid(eEmailField);
        if (phoneField.getText().replaceAll("[^\\d]", "").length() != 10) markInvalid(phoneField);
        if (ePhoneField.getText().replaceAll("[^\\d]", "").length() != 10) markInvalid(ePhoneField);

        return valid;
    }

    private void markInvalid(Control field) {
        field.getStyleClass().remove("user-info-edit");
        field.getStyleClass().add("user-info-invalid");
    }

    private void clearInvalid(Control field) {
        field.getStyleClass().remove("user-info-invalid");
        field.getStyleClass().add("user-info-edit");
    }

    private void setFieldPrompts(CompositeMember member) {
        User u = member.getUser();
        Membership m = member.getMembership();
        typeField.setValue(m.getType());
        firstNameField.setText(u.getFirstName());
        lastNameField.setText(u.getLastName());
        prefNameField.setText(u.getPrefName());
        pronounsField.setValue(u.getPronouns());
        genderField.setValue(u.getGender());
        phoneField.setText(u.getPhoneNumber());
        emailField.setText(u.getEmail());
        userIdField.setText(m.getMembershipId());
        streetField1.setText(u.getAddress().getStreetAddress());
        streetField2.setText(u.getAddress().getApartmentNumber());
        cityField.setText(u.getAddress().getCity());
        stateField.setText(u.getAddress().getState());
        zipField.setText(u.getAddress().getZipCode());
        eNameField.setText(u.getEmergencyContactName());
        ePhoneField.setText(u.getEmergencyContactPhone());
        eEmailField.setText(u.getEmergencyContactEmail());
        dobField.setValue(u.getDateOfBirth());
    }
}
