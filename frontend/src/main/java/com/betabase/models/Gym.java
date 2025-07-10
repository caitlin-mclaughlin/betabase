package com.betabase.models;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Gym {
    
    private final LongProperty id = new SimpleLongProperty();

    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<GymGroup> group = new SimpleObjectProperty<>();
    private final ObjectProperty<Address> address = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> userSince = new SimpleObjectProperty<>();

    public Long getId() {return id.get(); }
    public void setId(Long id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public GymGroup getGroup() { return group.get(); }
    public void setGroup(GymGroup group) { this.group.set(group); }
    public ObjectProperty<GymGroup> GroupProperty() { return group; }

    public Address getAddress() { return address.get(); }
    public void setAddress(Address address) { this.address.set(address); }
    public ObjectProperty<Address> addressProperty() { return address; }

    public LocalDate getUserSince() { return userSince.get(); }
    public void setUserSince(LocalDate userSince) { this.userSince.set(userSince); }
}