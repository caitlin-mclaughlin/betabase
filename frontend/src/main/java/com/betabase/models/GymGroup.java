package com.betabase.models;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GymGroup {
    private Long id;
    private List<Long> gymIds;
    private final StringProperty name = new SimpleStringProperty();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<Long> getGymIds() { return gymIds; }
    public void setGymIds(List<Long> gymIds) { this.gymIds = gymIds; }
    public void addGymId(Long gymId) { this.gymIds.add(gymId); }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }
}
