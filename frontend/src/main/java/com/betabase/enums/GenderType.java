package com.betabase.enums;

public enum GenderType {
    FEMALE("Female"),
    MALE("Male"),
    NONBINARY("Nonbinary"),
    UNSET("Prefer not to answer");

    private final String display;

    GenderType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }

    public static GenderType fromString(String value) {
        if (value == null) return UNSET;
        String normalized = value.trim().toLowerCase();
        normalized = normalized.substring(0,1).toUpperCase() + normalized.substring(1);
        for (GenderType g : values()) {
            if (g.display.equals(normalized)) {
                return g;
            }
        }
        return UNSET;
    }
}
