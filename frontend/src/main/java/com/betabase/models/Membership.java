package com.betabase.models;

import java.time.LocalDate;

import com.betabase.enums.UserType;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Membership {
    
    private Long id;
    private Long userId;
    private Long gymId;
    private SimpleObjectProperty<UserType> type;
    private SimpleObjectProperty<LocalDate> userSince;

    private SimpleStringProperty membershipId;
    private SimpleStringProperty photoUrl;

    private SimpleBooleanProperty active;
    
    private SimpleBooleanProperty clocked;
    private SimpleBooleanProperty checked;
    private SimpleBooleanProperty loggedIn;

    public Membership() {
        this.type = new SimpleObjectProperty<>();
        this.userSince = new SimpleObjectProperty<>(null);
        this.membershipId = new SimpleStringProperty("");
        this.photoUrl = new SimpleStringProperty("");

        this.active = new SimpleBooleanProperty(true);
        this.clocked = new SimpleBooleanProperty(false);
        this.checked = new SimpleBooleanProperty(false);
        this.loggedIn = new SimpleBooleanProperty(false);
    }

    public Membership(Long userId, Long gymId, UserType type, LocalDate userSince) {
        this.userId = userId;
        this.gymId = gymId;
        
        this.type = new SimpleObjectProperty<>(type);
        this.userSince = new SimpleObjectProperty<>(userSince);
        this.membershipId = new SimpleStringProperty("");
        this.photoUrl = new SimpleStringProperty("");

        this.active = new SimpleBooleanProperty(true);
        this.clocked = new SimpleBooleanProperty(false);
        this.checked = new SimpleBooleanProperty(false);
        this.loggedIn = new SimpleBooleanProperty(false);
    }

    /** Getters and setters  **/
    // Id
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    // User Id
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    // Gym Id
    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }

    // Membership Type
    public UserType getType() { return type.get(); }
    public void setType(UserType type) { this.type.set(type); }

    // User Since
    public LocalDate getUserSince() { return userSince.get(); }
    public void setUserSince(LocalDate userSince) { this.userSince.set(userSince); }

    // User Id String
    public String getMembershipId() { return membershipId.get(); }
    public void setMembershipId(String membershipId) { this.membershipId.set(membershipId); }

    // Photo URL
    public String getPhotoUrl() { return photoUrl.get(); }
    public void setPhotoUrl(String photoUrl) { this.photoUrl.set(photoUrl); }

    // Active Membership?
    public boolean isActive() { return active.get(); }
    public void setActive(boolean active) { this.active.set(active); }

    // Clocked In?
    public boolean getClocked() { return clocked.get(); }
    public void setClocked(boolean clocked) { this.clocked.set(clocked); }

    // Checked In?
    public boolean getChecked() { return checked.get(); }
    public void setChecked(boolean checked) { this.checked.set(checked); }

    // Login/out
    public void Login() { loggedIn.set(true); }
    public void Logout() { loggedIn.set(false); }
    public boolean getLogged() { return loggedIn.get(); }

}
