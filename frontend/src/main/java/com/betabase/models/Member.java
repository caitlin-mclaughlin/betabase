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
    public Long getId() { return (id != null ? id : 0); }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return (firstName != null ? firstName : ""); }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return (lastName != null ? lastName : ""); }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPrefName() { return (prefName != null ? prefName : ""); }
    public void setPrefName(String prefName) { this.prefName = prefName; }

    public String getPronouns() { return (pronouns != null ? pronouns : " / "); }
    public void setPronouns(String pronouns) { this.pronouns = pronouns; }

    public String getGender() { return (gender != null ? gender : ""); }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoneNumber() { return (phoneNumber != null ? phoneNumber : ""); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return (email != null ? email : ""); }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhotoUrl() { return (photoUrl != null ? photoUrl : ""); }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getMemberId() { return (memberId != null ? memberId : ""); }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public LocalDate getMemberSince() { return (memberSince);}// != null ? memberSince : ""); }
    public void setMemberSince(LocalDate memberSince) { this.memberSince = memberSince; }

    public String getStatus() { return (status != null ? status : ""); }
    public void setStatus(String status) { this.status = status; }

    public String getBillingMethod() { return (billingMethod != null ? billingMethod : ""); }
    public void setBillingMethod(String billingMethod) { this.billingMethod = billingMethod; }

    public String getAddress() { return (address != null ? address : ""); }
    public void setAddress(String address) { this.address = address; }

    public String getEmergencyContactName() { return (emergencyContactName != null ? emergencyContactName : ""); }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return (emergencyContactPhone != null ? emergencyContactPhone : ""); }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getEmergencyContactEmail() { return (emergencyContactEmail != null ? emergencyContactEmail : ""); }
    public void setEmergencyContactEmail(String emergencyContactEmail) { this.emergencyContactEmail = emergencyContactEmail; }
}
