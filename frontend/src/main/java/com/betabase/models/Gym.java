package com.betabase.models;

import java.time.LocalDate;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Gym {
    
    private Long id;

    private SimpleStringProperty name;
    private SimpleObjectProperty<Address> address;
    private SimpleObjectProperty<LocalDate> userSince;

    public Gym() {
        this.name = new SimpleStringProperty("");
        this.address = new SimpleObjectProperty<>();
        this.userSince = new SimpleObjectProperty<>();
    }

    public Long getId() {return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public Address getAddress() { return address.get(); }
    public void setAddress(Address address) { this.address.set(address); }

    public LocalDate getUserSince() { return userSince.get(); }
    public void setUserSince(LocalDate userSince) { this.userSince.set(userSince); }
}