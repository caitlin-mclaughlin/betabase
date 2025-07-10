package com.betabase.models;

import java.time.LocalDate;

import com.betabase.enums.UserType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Membership {
    
    private final LongProperty id = new SimpleLongProperty();
    private final LongProperty userId = new SimpleLongProperty();
    private final LongProperty gymId = new SimpleLongProperty();
    private final ObjectProperty<UserType> type= new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> userSince = new SimpleObjectProperty<>();

    private final StringProperty membershipId = new SimpleStringProperty();
    private final StringProperty photoUrl = new SimpleStringProperty();

    private final BooleanProperty active = new SimpleBooleanProperty();
    
    private final BooleanProperty clocked = new SimpleBooleanProperty();
    private final BooleanProperty checked = new SimpleBooleanProperty();
    private final BooleanProperty loggedIn = new SimpleBooleanProperty();

    /** Getters and setters  **/
    // Id
    public Long getId() { return id.get(); }
    public void setId(Long id) { this.id.set(id); }
    
    // User Id
    public Long getUserId() { return userId.get(); }
    public void setUserId(Long userId) { this.userId.set(userId); }
    
    // Gym Id
    public Long getGymId() { return gymId.get(); }
    public void setGymId(Long gymId) { this.gymId.set(gymId); }

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
