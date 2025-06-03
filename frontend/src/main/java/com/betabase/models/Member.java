package com.betabase.models;

import java.time.LocalDate;

public class Member {
    private Long id;

    private String firstName;
    private String prefName;
    private String lastName;
    private String pronouns;
    private String gender;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private String photoUrl;
    private String memberId;
    private LocalDate memberSince;
    private String status;

    // Billing Info
    private String billingMethod;
    private String address;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactEmail;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getPrefName() { return prefName; }
    public void setPrefName(String prefName) { this.prefName = prefName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPronouns() { return pronouns; }
    public void setPronouns(String pronouns) { this.pronouns = pronouns; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public LocalDate getMemberSince() { return memberSince; }
    public void setMemberSince(LocalDate memberSince) { this.memberSince = memberSince; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBillingMethod() { return billingMethod; }
    public void setBillingMethod(String billingMethod) { this.billingMethod = billingMethod; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getEmergencyContactEmail() { return emergencyContactEmail; }
    public void setEmergencyContactEmail(String emergencyContactEmail) { this.emergencyContactEmail = emergencyContactEmail; }
}
