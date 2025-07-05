package com.betabase.enums;

public enum UserType {
    ADMIN("Admin"),
    STAFF("Staff"),
    MEMBER("Member"),
    VISITOR("Visitor"),
    UNSET("Unset");

    private final String display;

    UserType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }

    public static UserType fromString(String value) {
        if (value == null) return UNSET;
        try {
            return UserType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNSET;
        }
    }
}

