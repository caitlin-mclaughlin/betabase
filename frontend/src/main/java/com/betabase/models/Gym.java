package com.betabase.models;

import java.time.LocalDate;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Gym {
    
    private SimpleLongProperty id;

    private SimpleStringProperty name;
    private SimpleStringProperty address;
    private SimpleStringProperty city;
    private SimpleStringProperty zipCode;
    private SimpleStringProperty state;
    private SimpleObjectProperty<LocalDate> userSince;

    public Long getId() {return id.get(); }
    public void setId(Long id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public String getAddress() { return address.get(); }
    public void setAddress(String address) { this.address.set(address); }

    public String getCity() { return city.get(); }
    public void setCity(String city) { this.city.set(city); }

    public String getZipCode() { return zipCode.get(); }
    public void setZipCode(String zipCode) { this.zipCode.set(zipCode); }

    public String getState() { return state.get(); }
    public void setState(String state) { this.state.set(state); }

    public LocalDate getUserSince() { return userSince.get(); }
    public void setUserSince(LocalDate userSince) { this.userSince.set(userSince); }
}