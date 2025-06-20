package com.betabase.controllers;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import com.betabase.enums.*;
import com.betabase.models.Member;
import com.betabase.services.MemberApiService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

public class MemberController implements Initializable {
    
    // Member information fields
    @FXML private Label nameLabel;
    @FXML private Label typeLabel;
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
    @FXML private ChoiceBox<MemberType>  typeField;
    @FXML private ChoiceBox<GenderType> genderField;
    @FXML private ChoiceBox<PronounsType>  pronounsField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField memberIdField;
//    @FXML private DatePicker dobField;
//    @FXML private DatePicker memberSinceField;
    @FXML private ChoiceBox<BillingType> billingField;
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

    @FXML private HBox staffButtons;

    private MemberApiService apiService;

    private Member currentMember;

    public void setApiService(MemberApiService apiService) {
        this.apiService = apiService;
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
        billingField.setItems(FXCollections.observableArrayList(BillingType.values()));

        setupPhoneFormatter(this.phoneField);
        initializePhoneField();
    }

    private void initializePhoneField() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText().replaceAll("[^\\d]", ""); // Only digits

            if (text.length() > 10) {
                return null; // restrict to 10 digits
            }

            // Format: (XXX) XXX-XXXX
            StringBuilder formatted = new StringBuilder();
            int len = text.length();

            if (len > 0) formatted.append("(").append(text.substring(0, Math.min(3, len)));
            if (len >= 4) formatted.append(") ").append(text.substring(3, Math.min(6, len)));
            if (len >= 7) formatted.append("-").append(text.substring(6));

            change.setText(formatted.toString());
            change.setRange(0, change.getControlText().length()); // overwrite whole field
            return change;
        };

        TextFormatter<String> formatter = new TextFormatter<>(filter);
        phoneField.setTextFormatter(formatter);
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

    public void setMember(Long memberId) {
        setEditableState(false);
        System.out.println("\nDEBUG: setting member\n");
        updateDisplayFromMember(memberId);
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
        updateDisplayFromMember(currentMember.getId()); // revert to original values
    }

    @FXML
    private void handleSave(MouseEvent event) { 
        // save changes to object
        boolean newMember = currentMember.getFirstName().isBlank();

        try {
            boolean success;

            if (newMember) {
                success = apiService.createMember(currentMember);
            } else {
                updateMemberFromFields(currentMember.getId());
                success = apiService.updateMember(currentMember);
            }
            
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
        setEditableState(false);
        updateDisplayFromMember(currentMember.getId()); // refresh display
    }


    private void updateDisplayFromMember(Long memberId) {
        // Reload and display data
        try {
            currentMember = apiService.getMemberById(memberId);
            System.out.println("\nDEBUG: got member: "+currentMember.getFirstName()+"\n");
            if (currentMember.getPrefName().isBlank()) {
                nameLabel.setText(currentMember.getLastName() + ", " + currentMember.getFirstName() +
                                "  (" + currentMember.getPronouns().toString() + ")");
            } else {
                nameLabel.setText(currentMember.getLastName() + ",  " + currentMember.getFirstName() + "  \"" + 
                                currentMember.getPrefName() + "\"  (" + currentMember.getPronouns().toString() + ")");
            }
            genderLabel.setText(currentMember.getGender().toString());
            phoneLabel.setText(currentMember.getPhoneNumber());
            emailLabel.setText(currentMember.getEmail());
            dobLabel.setText(currentMember.getDateOfBirth() != null ? currentMember.getDateOfBirth().toString()  : "MM/DD/YYYY");
            memberIdLabel.setText(currentMember.getMemberId());
            memberSinceLabel.setText(currentMember.getMemberSince() != null ? currentMember.getMemberSince().toString()  : "MM/DD/YYYY");
            billingLabel.setText(currentMember.getBillingMethod().toString());
            addressLabel.setText(currentMember.getAddress());
            eNameLabel.setText(currentMember.getEmergencyContactName());
            ePhoneLabel.setText(currentMember.getEmergencyContactPhone());
            eEmailLabel.setText(currentMember.getEmergencyContactEmail());

            // style and display member type
            MemberType type = currentMember.getType();
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
        } catch (Exception e) {
            e.printStackTrace();
            // show error dialog
        }
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
        //dobField.setVisible(editable);
        //dobField.setManaged(editable);
        memberSinceLabel.setVisible(!editable);
        memberSinceLabel.setManaged(!editable);
        //memberSinceField.setVisible(editable);
        //memberSinceField.setManaged(editable);
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

    private void updateMemberFromFields(Long memberId) {
        // Update or restore provided member
        try {
            currentMember.setFirstName(firstNameField.getText());
            currentMember.setLastName(lastNameField.getText());
            currentMember.setPrefName(prefNameField.getText());
            currentMember.setPronouns(pronounsField.getValue().toString());
            currentMember.setGender(genderField.getValue().toString());
            currentMember.setType(typeField.getValue().toString());
            currentMember.setPhoneNumber(phoneField.getText().replaceAll("[^\\d]", ""));
            currentMember.setEmail(emailField.getText());
    //        currentMember.setDateOfBirth(dobField.getValue());
    //        currentMember.setMemberSince(memberSinceField.getValue());
            currentMember.setMemberId(memberIdField.getText());
            currentMember.setBillingMethod(billingField.getValue().toString());
            currentMember.setAddress(addressField.getText());
            currentMember.setEmergencyContactName(eNameField.getText());
            currentMember.setEmergencyContactPhone(ePhoneField.getText());
            currentMember.setEmergencyContactEmail(eEmailField.getText());
            
            boolean success = apiService.updateMember(currentMember);
            if (success) {
                // maybe show a confirmation
                System.out.println("\nDEBUG: SUCCESS - member info saved\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // show error dialog
        }
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
        billingField.setValue(member.getBillingMethod());
        addressField.setText(member.getAddress());
        eNameField.setText(member.getEmergencyContactName());
        ePhoneField.setText(member.getEmergencyContactPhone());
        eEmailField.setText(member.getEmergencyContactEmail());

    }

}
