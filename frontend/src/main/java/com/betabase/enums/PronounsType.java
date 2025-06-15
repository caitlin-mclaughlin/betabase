package com.betabase.enums;

public enum PronounsType {
    SHE_HER("She/her"),
    HE_HIM("He/him"),
    THEY_THEM("They/them"),
    SHE_THEY("She/they"),
    HE_THEY("He/they"),
    UNSET("Prefer not to answer");

    private final String display;

    PronounsType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }

    public static PronounsType fromString(String value) {
        if (value == null) return UNSET;
        String normalized = value.trim().toLowerCase();
        normalized = normalized.substring(0,1).toUpperCase() + normalized.substring(1);
        for (PronounsType p : values()) {
            if (p.display.equals(normalized)) {
                return p;
            }
        }
        return UNSET;
    }
}
