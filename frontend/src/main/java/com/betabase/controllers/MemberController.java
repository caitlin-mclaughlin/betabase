package com.betabase.controllers;

import java.sql.Date;
import java.util.function.UnaryOperator;

import com.betabase.models.Member;
import com.betabase.services.MemberApiService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class MemberController {
    
    // Member information fields
    @FXML private Label nameLabel;
    @FXML private Label statusLabel;
    @FXML private Label genderLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label dobLabel;
    @FXML private Label memberIdLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label billingLabel;
    @FXML private Label addressLabel;
    @FXML private Label eNameLabel;
    @FXML private Label ePhoneLabel;
    @FXML private Label eEmailLabel;

    // Editable fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField prefNameField;
    @FXML private ChoiceBox<String>  statusField;
    @FXML private ChoiceBox<String> genderField;
    @FXML private ChoiceBox<String>  pronounsField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
//    @FXML private DatePicker dobField;
//    @FXML private DatePicker memberSinceField;
    @FXML private TextField billingField;
    @FXML private TextField addressField;
    @FXML private TextField eNameField;
    @FXML private TextField ePhoneField;
    @FXML private TextField eEmailField;

    // Buttons
    @FXML private Button editButton;
    @FXML private Button checkInButton;
    @FXML private Button checkOutButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private MemberApiService apiService;

    private Member currentMember;
    private boolean editing = false;

    public void setApiService(MemberApiService apiService) {
        this.apiService = apiService;
        setupPhoneFormatter(this.phoneField);
    }

    private void setupPhoneFormatter(TextField phoneField) {
        TextFormatter<String> formatter = new TextFormatter<>(new StringConverter<>() {
            @Override
            public String toString(String object) {
                return formatPhoneNumber(object);
            }

            @Override
            public String fromString(String string) {
                return string.replaceAll("[^\\d]", "");
            }
        }, "", change -> {
            String newText = change.getControlNewText().replaceAll("[^\\d]", "");
            if (newText.length() > 10) return null;
            return change;
        });

        phoneField.setTextFormatter(formatter);

        // Keep caret at end while typing
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> phoneField.positionCaret(phoneField.getText().length()));
        });
    }

    private String formatPhoneNumber(String digits) {
        digits = digits.replaceAll("[^\\d]", "");
        if (digits.length() == 0) return "";
        if (digits.length() <= 3) return "(" + digits;
        if (digits.length() <= 6) return "(" + digits.substring(0, 3) + ") " + digits.substring(3);
        return "(" + digits.substring(0, 3) + ") " + digits.substring(3, 6) + "-" + digits.substring(6);
    }


    public void setMember(Member member) {
        this.currentMember = member;
        editing = false;
        updateDisplayFromMember(member);
    }

    @FXML
    private void handleEdit() {
        editing = true;
        setEditableState(editing);
        setFieldPrompts(currentMember);
    }

    @FXML
    private void handleCancel() {
        editing = false;
        setEditableState(editing);
        updateDisplayFromMember(currentMember); // revert to original values
    }

    @FXML
    private void handleSave() { 
        // save changes to object
        updateMemberFromFields(currentMember);

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
        editing = false;
        setEditableState(editing);
        updateDisplayFromMember(currentMember); // refresh display
    }


    private void updateDisplayFromMember(Member member) {
        // validate and display data
        nameLabel.setText(member.getLastName() + ",  " + member.getFirstName() + "  \"" + 
                          member.getPrefName() + "\"  (" + member.getPronouns() + ")");
        genderLabel.setText(member.getGender());
        phoneLabel.setText(member.getPhoneNumber());
        emailLabel.setText(member.getEmail());
        dobLabel.setText(member.getDateOfBirth() != null ? member.getDateOfBirth().toString()  : "MM/DD/YYYY");
        memberIdLabel.setText(member.getMemberId());
        memberSinceLabel.setText(member.getMemberSince() != null ? member.getMemberSince().toString()  : "MM/DD/YYYY");
        billingLabel.setText(member.getBillingMethod());
        addressLabel.setText(member.getAddress());
        eNameLabel.setText(member.getEmergencyContactName());
        ePhoneLabel.setText(member.getEmergencyContactPhone());
        eEmailLabel.setText(member.getEmergencyContactEmail());

        // style and display member status
        String status = member.getStatus().toUpperCase();
        switch (status) {
            case "ADMIN": {
                statusLabel.setStyle("-fx-background-color: -fx-color-pos3;");
                break;
            }
            case "MEMBER": {
                statusLabel.setStyle("-fx-background-color: -fx-color-pos1;");
                break;
            }
            case "STAFF": {
                statusLabel.setStyle("-fx-background-color: -fx-color-pos4;");
                break;
            }
            case "VISITOR": {
                statusLabel.setStyle("-fx-background-color: -fx-color-pos2;");
                break;
            }
            default: statusLabel.setStyle("-fx-background-color: -fx-accent-color;");
        }
        statusLabel.setText(status);
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
        statusLabel.setVisible(!editable);
        statusLabel.setManaged(!editable);
        statusField.setVisible(editable);
        statusField.setManaged(editable);
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
        dobLabel.setVisible(!editable);
        dobLabel.setManaged(!editable);
//        dobField.setVisible(editable);
//        dobField.setManaged(editable);
        memberSinceLabel.setVisible(!editable);
        memberSinceLabel.setManaged(!editable);
//        memberSinceField.setVisible(editable);
//        memberSinceField.setManaged(editable);
        billingLabel.setVisible(!editable);
        billingLabel.setManaged(!editable);
        billingField.setVisible(editable);
        billingField.setManaged(editable);
        addressLabel.setVisible(!editable);
        addressLabel.setManaged(!editable);
        addressField.setVisible(editable);
        addressField.setManaged(editable);

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

    private void updateMemberFromFields(Member member) {
        // Update or restore provided member 
        member.setFirstName(!firstNameField.getText().isBlank() ? firstNameField.getText() : 
                                                                  firstNameField.getPromptText());
        member.setLastName(!lastNameField.getText().isBlank() ? lastNameField.getText() : 
                                                                lastNameField.getPromptText());
        member.setPrefName(!prefNameField.getText().isBlank() ? prefNameField.getText() : 
                                                                prefNameField.getPromptText());
        member.setPronouns(pronounsField.getValue().toString());
        member.setGender(genderField.getValue().toString());
        member.setStatus(statusField.getValue().toString());
        member.setPhoneNumber(!phoneField.getText().isBlank() ? phoneField.getText().replaceAll("[^\\d]", "") : 
                                                                phoneField.getPromptText());
        member.setEmail(!emailField.getText().isBlank() ? emailField.getText() : 
                                                          emailField.getPromptText());
//        member.setDateOfBirth(dobField.getValue());
//        member.setMemberSince(memberSinceField.getValue());
        member.setMemberId(currentMember.getMemberId());
        member.setBillingMethod(!billingField.getText().isBlank() ? billingField.getText() : 
                                                                    billingField.getPromptText());
        member.setAddress(!addressField.getText().isBlank() ? addressField.getText() : 
                                                              addressField.getPromptText());
        member.setEmergencyContactName(!eNameField.getText().isBlank() ? eNameField.getText() : 
                                                                          eNameField.getPromptText());
        member.setEmergencyContactPhone(!ePhoneField.getText().isBlank() ? ePhoneField.getText() : 
                                                                           ePhoneField.getPromptText());
        member.setEmergencyContactEmail(!eEmailField.getText().isBlank() ? eEmailField.getText() : 
                                                                           eEmailField.getPromptText());
    }

    private void setFieldPrompts(Member member) {
        firstNameField.setPromptText(member.getFirstName());
        lastNameField.setPromptText(member.getLastName());
        prefNameField.setPromptText(member.getPrefName());
        pronounsField.setValue(member.getPronouns());
        statusField.setValue(member.getStatus());
        genderField.setValue(member.getGender());
        phoneField.setPromptText(formatPhoneNumber(member.getPhoneNumber()));
        emailField.setPromptText(member.getEmail());
        billingField.setPromptText(member.getBillingMethod());
        addressField.setPromptText(member.getAddress());
        eNameField.setPromptText(member.getEmergencyContactName());
        ePhoneField.setPromptText(member.getEmergencyContactPhone());
        eEmailField.setPromptText(member.getEmergencyContactEmail());

    }

}
