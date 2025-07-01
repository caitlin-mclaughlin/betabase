package com.betabase.models;

import java.time.LocalDate;
import java.util.regex.Pattern;

import com.betabase.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

@JsonIgnoreProperties(value = {"gym"}, allowSetters = true)
public class Member {
    
    private Long id;

    private Gym gym;

    private SimpleStringProperty firstName;
    private SimpleStringProperty prefName;
    private SimpleStringProperty lastName;
    private SimpleObjectProperty<PronounsType> pronouns;
    private SimpleObjectProperty<GenderType> gender;
    private SimpleObjectProperty<LocalDate> dateOfBirth;
    private SimpleStringProperty phoneNumber;
    private SimpleStringProperty email;
    private SimpleStringProperty photoUrl;
    private SimpleStringProperty memberId;
    private SimpleObjectProperty<LocalDate> memberSince;
    private SimpleObjectProperty<MemberType> type;
    private SimpleBooleanProperty clocked;
    private SimpleBooleanProperty checked;
    private SimpleBooleanProperty loggedIn;

    private SimpleObjectProperty<Address> address;

    // Emergency Contact
    private SimpleStringProperty emergencyContactName;
    private SimpleStringProperty emergencyContactPhone;
    private SimpleStringProperty emergencyContactEmail;

    public Member() {
        this.firstName = new SimpleStringProperty("");
        this.prefName = new SimpleStringProperty("");
        this.lastName = new SimpleStringProperty("");
        this.pronouns = new SimpleObjectProperty<>(PronounsType.UNSET);
        this.gender = new SimpleObjectProperty<>(GenderType.UNSET);
        this.dateOfBirth = new SimpleObjectProperty<>(null);
        this.phoneNumber = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.photoUrl = new SimpleStringProperty("");
        this.memberId = new SimpleStringProperty("");
        this.memberSince = new SimpleObjectProperty<>(null);
        this.type = new SimpleObjectProperty<>(MemberType.UNSET);
        this.clocked = new SimpleBooleanProperty(false);
        this.checked = new SimpleBooleanProperty(false);
        this.loggedIn = new SimpleBooleanProperty(false);

        this.address = new SimpleObjectProperty<>(null);

        this.emergencyContactName = new SimpleStringProperty("");
        this.emergencyContactPhone = new SimpleStringProperty("");
        this.emergencyContactEmail = new SimpleStringProperty("");
    }

    /** Getters and setters  **/
    // Id
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Gym
    public Gym getGym() { return gym; }
    public void setGym(Gym gym) { this.gym = gym; }

    // First Name
    public SimpleStringProperty firstNameProperty() { return firstName; }
    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }

    // Last Name
    public SimpleStringProperty lastNameProperty() { return lastName; }
    public String getLastName() { return lastName.get(); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }

    // Preferred Name
    public SimpleStringProperty prefNameProperty() { return prefName; }
    public String getPrefName() { return prefName.get(); }
    public void setPrefName(String prefName) { this.prefName.set(prefName); }

    // Pronouns
    public SimpleObjectProperty<PronounsType> pronounsProperty() { return pronouns; }

    public PronounsType getPronouns() { return pronouns.get(); }
    public void setPronouns(PronounsType pronouns) { this.pronouns.set(pronouns); }

    public void setPronouns(String pronounsStr) {
        this.pronouns.set(PronounsType.fromString(pronounsStr));
    }

    // Gender
    public SimpleObjectProperty<GenderType> genderProperty() { return gender; }

    public GenderType getGender() { return gender.get(); }
    public void setGender(GenderType gender) { this.gender.set(gender); }

    public void setGender(String genderStr) {
        this.gender.set(GenderType.fromString(genderStr));
    }

    // Phone Number (validated)
    public SimpleStringProperty phoneNumberProperty() { return phoneNumber; }

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
    public SimpleStringProperty emailProperty() { return email; }

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
    public SimpleObjectProperty<LocalDate> dateOfBirthProperty() { return dateOfBirth; }

    public LocalDate getDateOfBirth() { return dateOfBirth.get(); }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth.set(dateOfBirth); }

    // Photo URL
    public SimpleStringProperty photoUrlProperty() { return photoUrl; }

    public String getPhotoUrl() { return photoUrl.get(); }
    public void setPhotoUrl(String photoUrl) { this.photoUrl.set(photoUrl); }

    // Member ID
    public SimpleStringProperty memberIdProperty() { return memberId; }

    public String getMemberId() { return memberId.get(); }
    public void setMemberId(String memberId) { this.memberId.set(memberId); }

    // Member Since
    public SimpleObjectProperty<LocalDate> memberSinceProperty() { return memberSince; }

    public LocalDate getMemberSince() { return memberSince.get(); }
    public void setMemberSince(LocalDate memberSince) { this.memberSince.set(memberSince); }

    // Member Type
    public SimpleObjectProperty<MemberType> typeProperty() { return type; }

    public MemberType getType() { return type.get(); }
    public void setType(MemberType type) { this.type.set(type); }

    public void setType(String typeStr) {
        this.type.set(MemberType.fromString(typeStr));
    }

    // Clocked In?
    public boolean getClocked() { return clocked.get(); }
    public void setClocked(boolean clocked) { this.clocked.set(clocked); }

    // Checked In?
    public boolean getChecked() { return checked.get(); }
    public void setChecked(boolean checked) { this.checked.set(checked); }

    // Login/out
    public void Login() { loggedIn.set(true); }
    public void Logout() { loggedIn.set(false); }

    // Address
    public Address getAddress() { return address.get(); }
    public void setAddress(Address address) { this.address.set(address); }

    // Emergency Contact Name
    public SimpleStringProperty eNameProperty() { return emergencyContactName; }

    public String getEmergencyContactName() { return emergencyContactName.get(); }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName.set(emergencyContactName); }

    // Emergency Contact Phone Number (validated)
    public SimpleStringProperty ePhoneProperty() { return emergencyContactPhone; }

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
    public SimpleStringProperty eEmailProperty() { return emergencyContactEmail; }

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
