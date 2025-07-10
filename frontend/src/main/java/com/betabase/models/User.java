package com.betabase.models;

import java.time.LocalDate;
import java.util.regex.Pattern;

import com.betabase.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@JsonIgnoreProperties(value = {"gym"}, allowSetters = true)
public class User {
    
    private LongProperty id = new SimpleLongProperty();

    private ObjectProperty<Gym> gym = new SimpleObjectProperty<>();

    private StringProperty firstName = new SimpleStringProperty();
    private StringProperty prefName = new SimpleStringProperty();
    private StringProperty lastName = new SimpleStringProperty();
    private ObjectProperty<PronounsType> pronouns = new SimpleObjectProperty<>(PronounsType.UNSET);
    private ObjectProperty<GenderType> gender = new SimpleObjectProperty<>(GenderType.UNSET);
    private ObjectProperty<LocalDate> dateOfBirth = new SimpleObjectProperty<>();
    private StringProperty phoneNumber = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private ObjectProperty<Address> address = new SimpleObjectProperty<>();

    // Emergency Contact
    private StringProperty emergencyContactName = new SimpleStringProperty();
    private StringProperty emergencyContactPhone = new SimpleStringProperty();
    private StringProperty emergencyContactEmail = new SimpleStringProperty();

    /** Getters and setters  **/
    // Id
    public Long getId() { return id.get(); }
    public void setId(Long id) { this.id.set(id); }

    // Gym
    public Gym getGym() { return gym.get(); }
    public void setGym(Gym gym) { this.gym.set(gym); }

    // First Name
    public StringProperty firstNameProperty() { return firstName; }
    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }

    // Last Name
    public StringProperty lastNameProperty() { return lastName; }
    public String getLastName() { return lastName.get(); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }

    // Preferred Name
    public StringProperty prefNameProperty() { return prefName; }
    public String getPrefName() { return prefName.get(); }
    public void setPrefName(String prefName) { this.prefName.set(prefName); }

    // Pronouns
    public ObjectProperty<PronounsType> pronounsProperty() { return pronouns; }

    public PronounsType getPronouns() { return pronouns.get(); }
    public void setPronouns(PronounsType pronouns) { this.pronouns.set(pronouns); }

    public void setPronouns(String pronounsStr) {
        this.pronouns.set(PronounsType.fromString(pronounsStr));
    }

    // Gender
    public ObjectProperty<GenderType> genderProperty() { return gender; }

    public GenderType getGender() { return gender.get(); }
    public void setGender(GenderType gender) { this.gender.set(gender); }

    public void setGender(String genderStr) {
        this.gender.set(GenderType.fromString(genderStr));
    }

    // Phone Number (validated)
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    public String getPhoneNumber() { return phoneNumber.get(); }
    public void setPhoneNumber(String phoneNumber) { 
        String digits = phoneNumber.replaceAll("[^\\d]", "");
        if (!Pattern.compile("\\d{10}").matcher(digits).matches()) {
            this.phoneNumber.set("Invalid");
        } else {
            this.phoneNumber.set(digits);
        }
    }

    // Email (validated)
    public StringProperty emailProperty() { return email; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { 
        if (email == null || email.isBlank()) {
            this.email.set("Unset");
        } else  if (!Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(email).matches()) {
            this.email.set("Invalid");
        } else {
            this.email.set(email);
        } 
     }

    // Birthday
    public ObjectProperty<LocalDate> dateOfBirthProperty() { return dateOfBirth; }

    public LocalDate getDateOfBirth() { return dateOfBirth.get(); }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth.set(dateOfBirth); }

    // Address
    public Address getAddress() { return address.get(); }
    public void setAddress(Address address) { this.address.set(address); }

    // Emergency Contact Name
    public StringProperty eNameProperty() { return emergencyContactName; }

    public String getEmergencyContactName() { return emergencyContactName.get(); }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName.set(emergencyContactName); }

    // Emergency Contact Phone Number (validated)
    public StringProperty ePhoneProperty() { return emergencyContactPhone; }

    public String getEmergencyContactPhone() { return emergencyContactPhone.get(); }
    public void setEmergencyContactPhone(String emergencyContactPhone) { 
        String digits = emergencyContactPhone.replaceAll("[^\\d]", "");
        if (!Pattern.compile("\\d{10}").matcher(digits).matches()) {
            this.emergencyContactPhone.set("Invalid");
        } else {
            this.emergencyContactPhone.set(digits);
        }
     }

    // Emergency Contact Email (validated)
    public StringProperty eEmailProperty() { return emergencyContactEmail; }

    public String getEmergencyContactEmail() { return emergencyContactEmail.get(); }
    public void setEmergencyContactEmail(String emergencyContactEmail) { 
        if (emergencyContactEmail == null || emergencyContactEmail.isBlank()) {
            this.emergencyContactEmail.set("Unset");
        } else  if (!Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(emergencyContactEmail).matches()) {
            this.emergencyContactEmail.set("Invalid");
        } else {
            this.emergencyContactEmail.set(emergencyContactEmail);
        } 
     }
}
