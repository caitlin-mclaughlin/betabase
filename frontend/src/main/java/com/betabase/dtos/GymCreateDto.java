package com.betabase.dtos;

import java.time.LocalDate;

import com.betabase.models.Address;

public class GymCreateDto {
    private String name;
    private String groupName;
    private Address address;
    private LocalDate gymSince;

    public GymCreateDto() {}

    public GymCreateDto(String name, String groupName, Address address, LocalDate gymSince) {
        this.name = name;
        this.groupName = groupName;
        this.address = address;
        this.gymSince = gymSince;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    public LocalDate getGymSince() { return gymSince; }
    public void setGymSince(LocalDate gymSince) { this.gymSince = gymSince; }
    
}
