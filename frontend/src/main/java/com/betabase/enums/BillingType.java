package com.betabase.enums;

public enum BillingType {
    CARD("Card"),
    CASH("Cash"),
    CHECK("Check"),
    UNSET("Unset");

    private final String display;

    BillingType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }

    public static BillingType fromString(String value) {
        if (value == null) return UNSET;
        try {
            return BillingType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNSET;
        }
    }
}
