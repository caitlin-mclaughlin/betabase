package com.betabase.dtos;

import java.time.LocalDate;

import com.betabase.models.Address;

public class GymDto {
    private Long id;
    private String name;
    private String groupName;
    private Address address;
    private LocalDate gymSince;

    public GymDto() {}

    public GymDto(Long id, String name, String groupName, Address address, LocalDate gymSince) {
        this.id = id;
        this.name = name;
        this.groupName = groupName;
        this.address = address;
        this.gymSince = gymSince;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    public LocalDate getGymSince() { return gymSince; }
    public void setGymSince(LocalDate gymSince) { this.gymSince = gymSince; }
    
}
