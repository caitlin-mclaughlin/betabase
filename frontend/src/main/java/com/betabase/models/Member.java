package com.betabase.models;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Member {
    private Long id;

    private Long gymId;

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
    private String type;
    private Boolean clocked;
    private Boolean checked;
    private Boolean loggedIn;

    // Billing Info
    private String billingMethod;
    private String address;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactEmail;

    // Validated getters and setters (EXCEPT DATES)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGymId() { return (gymId != null ? gymId : 0); }
    public void setGymId(Long gymId) { this.gymId = gymId; }

    public String getFirstName() { return (firstName != null ? firstName : ""); }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return (lastName != null ? lastName : ""); }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPrefName() { return (prefName != null ? prefName : ""); }
    public void setPrefName(String prefName) { this.prefName = prefName; }

    public String getPronouns() { return (pronouns != null ? pronouns : " / "); }
    public void setPronouns(String pronouns) { 
        if (pronouns == null || pronouns.isBlank()) {
            this.pronouns = "Prefer Not to Answer";
        } else {
            this.pronouns = switch (pronouns.toLowerCase()) {
                case "she / her" -> "she / her";
                case "he / him" -> "he / him";
                case "they / them" -> "they / them";
                case "she / they" -> "she / they";
                case "he / they" -> "he / they";
                default -> "Prefer Not to Answer";
            };
        }
     }

    public String getGender() { return gender; }
    public void setGender(String gender) { 
        if (gender == null || gender.isBlank()) {
            this.gender = "Prefer Not to Answer";
        } else {
            this.gender = switch (gender.toLowerCase()) {
                case "female" -> "Female";
                case "male" -> "Male";
                case "nonbinary" -> "Nonbinary";
                default -> "Prefer Not to Answer";
            };
        } 
    }

    public String getPhoneNumber() { return phoneNumber.equals("Invalid") ? "Invalid" :
                                            "(" + phoneNumber.substring(0,3) +
                                            ") " + phoneNumber.substring(3,6) +
                                            "-" + phoneNumber.substring(6); }
    public void setPhoneNumber(String phoneNumber) { 
        String digits = phoneNumber.replaceAll("[^\\d]", "");
        if (!Pattern.compile("\\d{10}").matcher(digits).matches()) {
            this.phoneNumber = "Invalid";
        } else {
            this.phoneNumber = digits;
        }
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { 
        if (email == null || email.isBlank()) {
            this.email = "Unset";
        } else  if (!Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(email).matches()) {
            this.email = "Invalid";
        } else {
            this.email = email;
        } 
     }

    // UNVALIDATED
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhotoUrl() { return (photoUrl != null ? photoUrl : ""); }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getMemberId() { return (memberId != null ? memberId : "000000000"); }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    // UNVALIDATED
    public LocalDate getMemberSince() { return (memberSince);}// != null ? memberSince : ""); }
    public void setMemberSince(LocalDate memberSince) { this.memberSince = memberSince; }

    public String getType() { return type; }
    public void setType(String type) { 
        if (type == null || type.isBlank()) {
            this.type = "UNSET";
        } else {
            this.type = switch (type.toUpperCase()) {
                case "ADMIN" -> "ADMIN";
                case "STAFF" -> "STAFF";
                case "MEMBER" -> "MEMBER";
                case "VISITOR" -> "VISITOR";
                default -> "UNSET";
            };
        }
    }

    public Boolean getClocked() { return clocked; }
    public void setClocked(Boolean clocked) { 
        this.clocked = clocked;
        // Log current time;
    }

    public Boolean getChecked() { return checked; }
    public void setChecked(Boolean checked) { 
        this.checked = checked;
        // Log current time;
    }

    public void Login() { loggedIn = true; }
    public void Logout() { loggedIn = false; }

    public String getBillingMethod() { return billingMethod; }
    public void setBillingMethod(String billingMethod) { 
        if (billingMethod == null || billingMethod.isBlank()) {
            this.billingMethod = "Unset";
        } else {
            this.billingMethod = switch (billingMethod.toLowerCase()) {
                case "card" -> "Card";
                case "cash" -> "Cash";
                default -> "Unset";
            };
        } 
    }

    public String getAddress() { return (address != null ? address : ""); }
    public void setAddress(String address) { this.address = address; }

    public String getEmergencyContactName() { return (emergencyContactName != null ? emergencyContactName : ""); }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { 
        String digits = emergencyContactPhone.replaceAll("[^\\d]", "");
        if (!Pattern.compile("\\d{10}").matcher(digits).matches()) {
            this.emergencyContactPhone = "Invalid";
        } else {
            this.emergencyContactPhone = digits;
        }
     }

    public String getEmergencyContactEmail() { return emergencyContactEmail; }
    public void setEmergencyContactEmail(String emergencyContactEmail) { 
        if (emergencyContactEmail == null || emergencyContactEmail.isBlank()) {
            this.emergencyContactEmail = "Unset";
        } else  if (!Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(emergencyContactEmail).matches()) {
            this.emergencyContactEmail = "Invalid";
        } else {
            this.emergencyContactEmail = emergencyContactEmail;
        } 
     }
}
