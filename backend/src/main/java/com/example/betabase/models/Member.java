package com.example.betabase.models;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.example.betabase.enums.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name="gym_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Gym gym;

    @Enumerated(EnumType.STRING)
    private MemberType type;
    @Enumerated(EnumType.STRING)
    private PronounsType pronouns;
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    private String firstName;
    private String prefName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private String photoUrl;
    private String memberId;
    private LocalDate memberSince;
    private Boolean clocked;
    private Boolean checked;

    // Billing Info
    private String billingMethod;
    private String address;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactEmail;

    /*public boolean hasMembershipAt(Long gymId) {
        return this.getGymIds().contains(gymId);
    }*/

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Gym getGym() { return gym; }
    public void setGym(Gym gym) { this.gym = gym; }
    //public void addGymId(Long gymId) { gymId.add(gymId); }
    //public void removeGymId(Long gymId) { gymIds.remove(gymId); }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getPrefName() { return prefName; }
    public void setPrefName(String prefName) { this.prefName = prefName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public PronounsType getPronouns() { return pronouns; }
    public void setPronouns(PronounsType pronouns) { this.pronouns = pronouns; }

    public GenderType getGender() { return gender; }
    public void setGender(GenderType gender) { this.gender = gender; }

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

    public MemberType getType() { return type; }
    public void setType(MemberType type) { this.type = type; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Boolean getClocked() { return clocked; }
    public void setClocked(Boolean clocked) { this.clocked = clocked; }

    public Boolean getChecked() { return checked; }
    public void setChecked(Boolean checked) { this.checked = checked; }

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
