package com.helpdesk.util;

import java.util.Set;
import java.util.regex.Pattern;

public final class InputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private InputValidator() {
    }

    public static String requireText(String value, String fieldName, int maxLength) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must be at most " + maxLength + " characters.");
        }
        return normalized;
    }

    public static String requireEmail(String value) {
        String email = requireText(value, "Email", 150);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email format is invalid.");
        }
        return email;
    }

    public static String requireAllowedValue(String value, String fieldName, Set<String> allowedValues) {
        String normalized = requireText(value, fieldName, 100);
        if (!allowedValues.contains(normalized)) {
            throw new IllegalArgumentException(fieldName + " is invalid.");
        }
        return normalized;
    }

    public static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
