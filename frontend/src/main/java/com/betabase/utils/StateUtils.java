package com.betabase.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StateUtils {

    // Ordered map of state abbreviations to full names
    private static final Map<String, String> STATES = new LinkedHashMap<>();

    static {
        STATES.put("AL", "Alabama");
        STATES.put("AK", "Alaska");
        STATES.put("AZ", "Arizona");
        STATES.put("AR", "Arkansas");
        STATES.put("CA", "California");
        STATES.put("CO", "Colorado");
        STATES.put("CT", "Connecticut");
        STATES.put("DE", "Delaware");
        STATES.put("FL", "Florida");
        STATES.put("GA", "Georgia");
        STATES.put("HI", "Hawaii");
        STATES.put("ID", "Idaho");
        STATES.put("IL", "Illinois");
        STATES.put("IN", "Indiana");
        STATES.put("IA", "Iowa");
        STATES.put("KS", "Kansas");
        STATES.put("KY", "Kentucky");
        STATES.put("LA", "Louisiana");
        STATES.put("ME", "Maine");
        STATES.put("MD", "Maryland");
        STATES.put("MA", "Massachusetts");
        STATES.put("MI", "Michigan");
        STATES.put("MN", "Minnesota");
        STATES.put("MS", "Mississippi");
        STATES.put("MO", "Missouri");
        STATES.put("MT", "Montana");
        STATES.put("NE", "Nebraska");
        STATES.put("NV", "Nevada");
        STATES.put("NH", "New Hampshire");
        STATES.put("NJ", "New Jersey");
        STATES.put("NM", "New Mexico");
        STATES.put("NY", "New York");
        STATES.put("NC", "North Carolina");
        STATES.put("ND", "North Dakota");
        STATES.put("OH", "Ohio");
        STATES.put("OK", "Oklahoma");
        STATES.put("OR", "Oregon");
        STATES.put("PA", "Pennsylvania");
        STATES.put("RI", "Rhode Island");
        STATES.put("SC", "South Carolina");
        STATES.put("SD", "South Dakota");
        STATES.put("TN", "Tennessee");
        STATES.put("TX", "Texas");
        STATES.put("UT", "Utah");
        STATES.put("VT", "Vermont");
        STATES.put("VA", "Virginia");
        STATES.put("WA", "Washington");
        STATES.put("WV", "West Virginia");
        STATES.put("WI", "Wisconsin");
        STATES.put("WY", "Wyoming");
    }

    public static List<String> getStateAbbreviations() {
        return List.copyOf(STATES.keySet());
    }

    public static List<String> getStateNames() {
        return List.copyOf(STATES.values());
    }

    public static String getStateName(String abbreviation) {
        return STATES.getOrDefault(abbreviation, abbreviation);
    }

    public static String getAbbreviation(String name) {
        return STATES.entrySet().stream()
                .filter(e -> e.getValue().equalsIgnoreCase(name))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(name);
    }

    public static Map<String, String> getStates() {
        return STATES;
    }
}
