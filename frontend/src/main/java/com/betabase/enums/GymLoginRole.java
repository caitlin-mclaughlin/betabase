package com.betabase.enums;

public enum GymLoginRole {
    ADMIN("Admin"),
    STAFF("Staff"),
    KIOSK("Kiosk"),
    UNSET("Unset");

    private final String display;

    GymLoginRole(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }

    public static GymLoginRole fromString(String value) {
        if (value == null) return UNSET;
        try {
            return GymLoginRole.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNSET;
        }
    }
}

