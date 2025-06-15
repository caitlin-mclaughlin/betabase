package com.betabase.enums;

public enum MemberType {
    ADMIN("Admin"),
    STAFF("Staff"),
    MEMBER("Member"),
    VISITOR("Visitor"),
    UNSET("Unset");

    private final String display;

    MemberType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }

    public static MemberType fromString(String value) {
        if (value == null) return UNSET;
        try {
            return MemberType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNSET;
        }
    }
}

