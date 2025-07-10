package com.betabase.models;

import com.betabase.enums.GymLoginRole;
import javafx.beans.property.*;

public class GymLogin {
    private Long id;
    private Long gymId;
    private Long gymGroupId;
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final ObjectProperty<GymLoginRole> role = new SimpleObjectProperty<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }

    public Long getGymGroupId() { return gymGroupId; }
    public void setGymGroupId(Long gymGroupId) { this.gymGroupId = gymGroupId; }

    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value); }
    public StringProperty usernameProperty() { return username; }

    public String getPassword() { return password.get(); }
    public void setPassword(String value) { password.set(value); }
    public StringProperty passwordProperty() { return password; }

    public GymLoginRole getRole() { return role.get(); }
    public void setRole(GymLoginRole value) { role.set(value); }
    public ObjectProperty<GymLoginRole> roleProperty() { return role; }

}
