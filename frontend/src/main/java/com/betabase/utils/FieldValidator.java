package com.betabase.utils;

import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import java.util.function.Predicate;

public class FieldValidator {

    private static final String INVALID_CLASS = "invalid-field";
    private static final String VALID_CLASS = "field";

    /**
     * Attaches a change listener to a TextField that validates it live.
     * 
     * @param field     the TextField to validate
     * @param predicate the condition for the field to be considered valid
     */
    public static void attach(TextField field, Predicate<String> predicate) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (predicate.test(newValue)) {
                clearInvalid(field);
            } else {
                markInvalid(field);
            }
        });
    }

    /**
     * Validates a TextField immediately.
     * 
     * @param field     the field to check
     * @param predicate the validity condition
     * @return true if valid, false if invalid
     */
    public static boolean validate(TextField field, Predicate<String> predicate) {
        boolean isValid = predicate.test(field.getText());
        if (isValid) {
            clearInvalid(field);
        } else {
            markInvalid(field);
        }
        return isValid;
    }

    public static void markInvalid(Control field) {
        field.getStyleClass().remove(VALID_CLASS);
        if (!field.getStyleClass().contains(INVALID_CLASS)) {
            field.getStyleClass().add(INVALID_CLASS);
        }
    }

    public static void clearInvalid(Control field) {
        field.getStyleClass().remove(INVALID_CLASS);
        if (!field.getStyleClass().contains(VALID_CLASS)) {
            field.getStyleClass().add(VALID_CLASS);
        }
    }
}
