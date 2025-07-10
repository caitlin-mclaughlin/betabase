package com.betabase.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GymLoginRequest {

    public final StringProperty username = new SimpleStringProperty();
    public final StringProperty password = new SimpleStringProperty();

    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }
    
}
