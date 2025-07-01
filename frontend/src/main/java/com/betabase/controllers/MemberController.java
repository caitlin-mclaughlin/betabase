package com.betabase.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import com.betabase.enums.*;
import com.betabase.interfaces.ServiceAware;
import com.betabase.models.Address;
import com.betabase.models.Member;
import com.betabase.services.GymApiService;
import com.betabase.services.MemberApiService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class MemberController implements Initializable, ServiceAware {
    
    // Member information fields
    @FXML private Label nameLabel;
    @FXML private Label typeLabel;
    @FXML private Label genderLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label dobLabel;
    @FXML private Label memberIdLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label addressLabel1;
    @FXML private Label addressLabel2;
    @FXML private Label eNameLabel;
    @FXML private Label ePhoneLabel;
    @FXML private Label eEmailLabel;

    // Editable fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField prefNameField;
    @FXML private ChoiceBox<MemberType>  typeField;
    @FXML private ChoiceBox<GenderType> genderField;
    @FXML private ChoiceBox<PronounsType>  pronounsField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField memberIdField;
    @FXML private DatePicker dobField;
//    @FXML private DatePicker memberSinceField;
    @FXML private TextField streetField1;
    @FXML private TextField streetField2;
    @FXML private TextField cityField;
    @FXML private TextField zipField;
    @FXML private TextField stateField;
    @FXML private TextField eNameField;
    @FXML private TextField ePhoneField;
    @FXML private TextField eEmailField;

    // Buttons
    @FXML private Button editButton;
    @FXML private Button checkInButton;
    @FXML private Button checkOutButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML private HBox staffButtons;

    private Member currentMember;
    private MemberApiService memberService;

    @Override
    public void setServices(MemberApiService memberService, GymApiService gymService) {
        this.memberService = memberService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // If the member is not set, it's a new member --> enter edit mode
        if (currentMember == null) {
            currentMember = new Member();
            handleEdit(new ActionEvent());
        }
        typeField.setItems(FXCollections.observableArrayList(MemberType.values()));
        genderField.setItems(FXCollections.observableArrayList(GenderType.values()));
        pronounsField.setItems(FXCollections.observableArrayList(PronounsType.values()));

        setupPhoneFormatter(phoneField);
        setupPhoneFormatter(ePhoneField);
    }

    private void setupPhoneFormatter(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText().replaceAll("[^\\d]", "");

            if (newText.length() > 10) return null;

            // Format live
            StringBuilder formatted = new StringBuilder();
            int len = newText.length();

            if (len > 0) formatted.append("(").append(newText.substring(0, Math.min(3, len)));
            if (len >= 3) formatted.append(") ").append(newText.substring(3, Math.min(6, len)));
            if (len >= 7) formatted.append("-").append(newText.substring(6));

            change.setText(formatted.toString());
            change.setRange(0, change.getControlText().length()); // Replace full content
            return change;
        };

        TextFormatter<String> formatter = new TextFormatter<>(filter);
        field.setTextFormatter(formatter);

        // Optional: keep caret at end while typing
        field.textProperty().addListener((obs, oldVal, newVal) ->
            Platform.runLater(() -> field.positionCaret(field.getText().length()))
        );
    }

    public void setMember(Member member) {
        this.currentMember = member;
        setEditableState(false);
        updateDisplayFromMember(member);
    }

    @FXML
    private void handleClockIn(MouseEvent event) {
        System.out.println("\nDEBUG: Clock in pressed\n");
    }

    @FXML
    private void handleClockOut(MouseEvent event) {
        System.out.println("\nDEBUG: Clock out pressed\n");
    }
    
    @FXML
    private void handleEdit(ActionEvent event) {
        setEditableState(true);
        setFieldPrompts(currentMember);
    }

    @FXML
    private void handleCancel(MouseEvent event) {
        setEditableState(false);
        updateDisplayFromMember(currentMember); // revert to original values
    }

    @FXML
    private void handleSave(MouseEvent event) { 
        // save changes to object
        if (!validateFields()) {
            return;
        }
        boolean newMember = currentMember == null || currentMember.getId() == null;

        try {
            boolean success;
            updateCurrentMemberFromFields();

            if (newMember) {
                currentMember.setMemberSince(LocalDate.now());
                System.out.println("Before: " + currentMember+"\tid:"+currentMember.getId());
                System.out.println("\nDEBUG: new member with dob: "+currentMember.getDateOfBirth().toString()+"\n");
                currentMember = memberService.createMember(currentMember);
                System.out.println("After: " + currentMember+"\tid:"+currentMember.getId());

                success = currentMember != null;
                System.out.println("\nDEBUG: new member with id: "+currentMember.getId()+"\n"+success);
            } else {
                System.out.println("\nDEBUG: existing member with id: "+currentMember.getId()+"\n");
                success = memberService.updateMember(currentMember);
            }
            
            if (success) {
                // maybe show a confirmation
                System.out.println("\nDEBUG: save method: SUCCESS - member info saved\n");
                setEditableState(false);
                updateDisplayFromMember(currentMember); // refresh display
            } else {
                // show error
                System.out.println("\nDEBUG: member info could not be saved\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // show error dialog
        }
    }

    private void updateDisplayFromMember(Member member) {
        // Reload and display data
        // fix behavior when getById fails (null pointer)
        if (member == null) {
            System.out.println("\nDEBUG: attempted to display null member\n");
            return;
        }
        System.out.println("\nDEBUG: updating display with member: "+member.getFirstName()+"\n");
        if (member.getPrefName().isBlank()) {
            nameLabel.setText(member.getLastName() + ", " + member.getFirstName() +
                            "  (" + member.getPronouns().toString() + ")");
        } else {
            nameLabel.setText(member.getLastName() + ",  " + member.getFirstName() + "  \"" + 
                            member.getPrefName() + "\"  (" + member.getPronouns().toString() + ")");
        }
        genderLabel.setText(member.getGender().toString());
        phoneLabel.setText(member.getPhoneNumber());
        emailLabel.setText(member.getEmail());
        dobLabel.setText(member.getDateOfBirth() != null ? member.getDateOfBirth().toString()  : "MM/DD/YYYY");
        memberIdLabel.setText(member.getMemberId());
        memberSinceLabel.setText(member.getMemberSince() != null ? member.getMemberSince().toString()  : "MM/DD/YYYY");
        addressLabel1.setText(member.getAddress().toStringLine1());
        addressLabel2.setText(member.getAddress().toStringLine2());
        eNameLabel.setText(member.getEmergencyContactName());
        ePhoneLabel.setText(member.getEmergencyContactPhone());
        eEmailLabel.setText(member.getEmergencyContactEmail());

        // style and display member type
        MemberType type = member.getType();
        switch (type) {
            case ADMIN: {
                typeLabel.setStyle("-fx-background-color: -fx-color-pos3;");
                staffButtons.setVisible(true);
                break;
            }
            case MEMBER: {
                typeLabel.setStyle("-fx-background-color: -fx-color-pos1;");
                staffButtons.setVisible(false);
                break;
            }
            case STAFF: {
                typeLabel.setStyle("-fx-background-color: -fx-color-pos4;");
                staffButtons.setVisible(true);
                break;
            }
            case VISITOR: {
                typeLabel.setStyle("-fx-background-color: -fx-color-pos2;");
                staffButtons.setVisible(false);
                break;
            }
            default: {
                typeLabel.setStyle("-fx-background-color: -fx-accent-color;");
                staffButtons.setVisible(false);
            }
        }
        typeLabel.setText(type.toString().toUpperCase());
    }

    private boolean validateFields() {
        boolean valid = true;

        if (firstNameField.getText().isBlank()) {
            markInvalid(firstNameField);
            valid = false;
        } else {
            clearInvalid(firstNameField);
        }

        if (lastNameField.getText().isBlank()) {
            markInvalid(lastNameField);
            valid = false;
        } else {
            clearInvalid(lastNameField);
        }

        if (phoneField.getText().replaceAll("[^\\d]", "").length() != 10) {
            markInvalid(phoneField);
            valid = false;
        } else {
            clearInvalid(phoneField);
        }

        if (!emailField.getText().matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w+$")) {
            markInvalid(emailField);
            valid = false;
        } else {
            clearInvalid(emailField);
        }

        if (dobField.getValue() == null) {
            markInvalid(dobField);
            valid = false;
        } else {
            clearInvalid(dobField);
        }

        // Address 
        if (streetField1.getText().isBlank()) {
            markInvalid(streetField1);
            valid = false;
        } else {
            clearInvalid(streetField1);
        }

        if (streetField2.getText().isBlank()) {
            markInvalid(streetField2);
            valid = false;
        } else {
            clearInvalid(streetField2);
        }

        if (cityField.getText().isBlank()) {
            markInvalid(cityField);
            valid = false;
        } else {
            clearInvalid(cityField);
        }

        if (stateField.getText().isBlank()) {
            markInvalid(stateField);
            valid = false;
        } else {
            clearInvalid(stateField);
        }

        if (zipField.getText().isBlank()) {
            markInvalid(zipField);
            valid = false;
        } else {
            clearInvalid(zipField);
        }

        // Emergency contact info
        if (eNameField.getText().isBlank()) {
            markInvalid(eNameField);
            valid = false;
        } else {
            clearInvalid(eNameField);
        }

        if (ePhoneField.getText().replaceAll("[^\\d]", "").length() != 10) {
            markInvalid(ePhoneField);
            valid = false;
        } else {
            clearInvalid(ePhoneField);
        }

        if (!eEmailField.getText().matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w+$")) {
            markInvalid(eEmailField);
            valid = false;
        } else {
            clearInvalid(eEmailField);
        }

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

    private void setEditableState(boolean editable) {
        // Header
        nameLabel.setVisible(!editable);
        nameLabel.setManaged(!editable);
        lastNameField.setVisible(editable);
        lastNameField.setManaged(editable);
        firstNameField.setVisible(editable);
        firstNameField.setManaged(editable);
        prefNameField.setVisible(editable);
        prefNameField.setManaged(editable);
        pronounsField.setVisible(editable);
        pronounsField.setManaged(editable);

        // Member info
        typeLabel.setVisible(!editable);
        typeLabel.setManaged(!editable);
        typeField.setVisible(editable);
        typeField.setManaged(editable);
        genderLabel.setVisible(!editable);
        genderLabel.setManaged(!editable);
        genderField.setVisible(editable);
        genderField.setManaged(editable);
        phoneLabel.setVisible(!editable);
        phoneLabel.setManaged(!editable);
        phoneField.setVisible(editable);
        phoneField.setManaged(editable);
        emailLabel.setVisible(!editable);
        emailLabel.setManaged(!editable);
        emailField.setVisible(editable);
        emailField.setManaged(editable);
        memberIdLabel.setVisible(!editable);
        memberIdLabel.setManaged(!editable);
        memberIdField.setVisible(editable);
        memberIdField.setManaged(editable);
        dobLabel.setVisible(!editable);
        dobLabel.setManaged(!editable);
        dobField.setVisible(editable);
        dobField.setManaged(editable);
        memberSinceLabel.setVisible(!editable);
        memberSinceLabel.setManaged(!editable);
        //memberSinceField.setVisible(editable);
        //memberSinceField.setManaged(editable);

        // Address
        addressLabel1.setVisible(!editable);
        addressLabel1.setManaged(!editable);
        addressLabel1.setVisible(editable);
        addressLabel1.setManaged(editable);
        addressLabel2.setVisible(!editable);
        addressLabel2.setManaged(!editable);
        addressLabel2.setVisible(editable);
        addressLabel2.setManaged(editable);
        streetField1.setVisible(!editable);
        streetField1.setManaged(!editable);
        streetField1.setVisible(editable);
        streetField1.setManaged(editable);
        streetField2.setVisible(!editable);
        streetField2.setManaged(!editable);
        streetField2.setVisible(editable);
        streetField2.setManaged(editable);
        cityField.setVisible(!editable);
        cityField.setManaged(!editable);
        cityField.setVisible(editable);
        cityField.setManaged(editable);
        stateField.setVisible(!editable);
        stateField.setManaged(!editable);
        stateField.setVisible(editable);
        stateField.setManaged(editable);
        zipField.setVisible(!editable);
        zipField.setManaged(!editable);
        zipField.setVisible(editable);
        zipField.setManaged(editable);

        // Emergency contact info
        eNameLabel.setVisible(!editable);
        eNameLabel.setManaged(!editable);
        eNameField.setVisible(editable);
        eNameField.setManaged(editable);
        ePhoneLabel.setVisible(!editable);
        ePhoneLabel.setManaged(!editable);
        ePhoneField.setVisible(editable);
        ePhoneField.setManaged(editable);
        eEmailLabel.setVisible(!editable);
        eEmailLabel.setManaged(!editable);
        eEmailField.setVisible(editable);
        eEmailField.setManaged(editable);

        // Buttons
        checkInButton.setVisible(!editable);
        checkInButton.setManaged(!editable);
        saveButton.setVisible(editable);
        saveButton.setManaged(editable);
        checkOutButton.setVisible(!editable);
        checkOutButton.setManaged(!editable);
        cancelButton.setVisible(editable);
        cancelButton.setManaged(editable);
    }

    private void updateCurrentMemberFromFields() {
        // Update or restore member
        currentMember.setFirstName(firstNameField.getText());
        currentMember.setLastName(lastNameField.getText());
        currentMember.setPrefName(prefNameField.getText());
        currentMember.setPronouns(pronounsField.getValue());
        currentMember.setGender(genderField.getValue());
        currentMember.setType(typeField.getValue());
        currentMember.setPhoneNumber(phoneField.getText().replaceAll("[^\\d]", ""));
        currentMember.setEmail(emailField.getText());
        currentMember.setDateOfBirth(dobField.getValue());
        currentMember.setMemberId(memberIdField.getText());
        currentMember.setAddress(formAddress(streetField1.getText(), streetField2.getText(), cityField.getText(),
                                             stateField.getText(), zipField.getText()));
        currentMember.setEmergencyContactName(eNameField.getText());
        currentMember.setEmergencyContactPhone(ePhoneField.getText().replaceAll("[^\\d]", ""));
        currentMember.setEmergencyContactEmail(eEmailField.getText());
    }

    /**
     * 
     * NOTE: HARDCODED COUNTRY
     * 
     */
    private Address formAddress(String streetAddress1, String streetAddress2, String city, String state, String zip) {
        String streetNumber;
        String streetName;

        int firstSpace = streetAddress1.indexOf(" ");
        if (firstSpace > 0) {
            streetNumber = streetAddress1.substring(0, firstSpace).trim();
            streetName = streetAddress1.substring(firstSpace + 1).trim();
        } else {
            // fallback: assume whole thing is name
            streetNumber = "";
            streetName = streetAddress1.trim();
        }
        return streetAddress2.isBlank() ? new Address(streetNumber, streetName, city, state, zip, "USA") :
                                          new Address(streetNumber, streetName, streetAddress2, city, state, zip, "USA");
    }

    private void setFieldPrompts(Member member) {
        // type
        MemberType type = member.getType();
        typeField.setValue(type);
        String bgColor = switch (type) {
            case ADMIN -> "-fx-color-pos3";
            case MEMBER -> "-fx-color-pos1";
            case STAFF -> "-fx-color-pos4";
            case VISITOR -> "-fx-color-pos2";
            default -> "-fx-accent-color";
        };
        typeField.setStyle(String.format(
            "-fx-background-color: %s;", bgColor
        ));

        // other fields
        firstNameField.setText(member.getFirstName());
        lastNameField.setText(member.getLastName());
        prefNameField.setText(member.getPrefName());
        pronounsField.setValue(member.getPronouns());
        genderField.setValue(member.getGender());
        phoneField.setText(member.getPhoneNumber());
        emailField.setText(member.getEmail());
        memberIdField.setText(member.getMemberId());
        streetField1.setText(member.getAddress().getStreetAddress());
        streetField2.setText(member.getAddress().getApartmentNumber());
        cityField.setText(member.getAddress().getCity());
        stateField.setText(member.getAddress().getState());
        zipField.setText(member.getAddress().getZipCode());
        eNameField.setText(member.getEmergencyContactName());
        ePhoneField.setText(member.getEmergencyContactPhone());
        eEmailField.setText(member.getEmergencyContactEmail());
    }

}
