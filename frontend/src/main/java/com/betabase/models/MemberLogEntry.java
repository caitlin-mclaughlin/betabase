package com.betabase.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.betabase.enums.UserType;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class MemberLogEntry {
    private final LocalDate date;
    private final Long userId;
    private final SimpleStringProperty name;
    private final SimpleObjectProperty<UserType> type;
    private final SimpleStringProperty phoneNumber;
    private final SimpleStringProperty email;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private static final DateTimeFormatter TIME_FORMAT_12HR = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter TIME_FORMAT_24HR = DateTimeFormatter.ofPattern("H:mm a");
    private boolean format_12Hr;

    public MemberLogEntry() {
        date = null;
        userId = null;
        name = new SimpleStringProperty("");
        type = new SimpleObjectProperty<UserType>(UserType.UNSET);
        phoneNumber = new SimpleStringProperty("");
        email = new SimpleStringProperty("");

        checkInTime = null;
        checkOutTime = null;

        format_12Hr = true; // Default to 12Hr clock
    }

    public MemberLogEntry(CompositeMember member, LocalDateTime checkInTime) {
        User user = member.getUser();
        date = checkInTime.toLocalDate();
        this.userId = user.getId();
        name = new SimpleStringProperty(user.getFirstName());
        type = new SimpleObjectProperty<UserType>(member.getMembership().getType());
        phoneNumber = new SimpleStringProperty(user.getPhoneNumber());
        email = new SimpleStringProperty(user.getEmail());

        this.checkInTime = checkInTime;
        checkOutTime = null;

        format_12Hr = true; // Default to 12Hr clock
    }

    public void changeTo12Hr() {
        format_12Hr = true;
    }

    public void changeTo24Hr() {
        format_12Hr = false;
    }

    public void setCheckInTime(LocalDateTime checkInTime) { 
        this.checkInTime = checkInTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) { 
        this.checkOutTime = checkOutTime;
    }

    public String getCheckInTime() { 
        String formatted;
        if (checkInTime != null) {
            formatted = format_12Hr ? checkInTime.format(TIME_FORMAT_12HR) : checkInTime.format(TIME_FORMAT_24HR);
        } else if (name.get().isBlank()) {
            formatted = "";
        } else {
            formatted = " — ";
        }
        return formatted; 
    }
    public String getCheckOutTime() {
        String formatted;
        if (checkOutTime != null) {
            formatted = format_12Hr ? checkOutTime.format(TIME_FORMAT_12HR) : checkOutTime.format(TIME_FORMAT_24HR);
        } else if (name.get().isBlank()) {
            formatted = "";
        } else {
            formatted = " — ";
        }
        return formatted; 
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getName() { return name.get(); }
    public UserType getMembershipType() { return type.get(); }
    public String getPhoneNumber() { return phoneNumber.get(); }
    public String getEmail() { return email.get(); }
}
