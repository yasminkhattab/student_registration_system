package com.studentregistration.util;

import com.studentregistration.exception.ValidationException;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Central place for the field-level rules shared by all services.
 *
 * <p>Keeping validation here avoids duplicated checks in controllers and makes
 * the rules easy to unit test.</p>
 */
public final class Validator {

    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9-]{3,20}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\- ]{7,20}$");

    private Validator() {
        // Utility class: no instances.
    }

    /**
     * Requires a non-blank text value and trims it.
     *
     * @param value     raw value
     * @param fieldName field label used in the error message
     * @return trimmed value
     * @throws ValidationException when the value is missing
     */
    public static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required.");
        }
        return value.trim();
    }

    /**
     * Normalises and validates an identifier such as {@code STU-1001}.
     *
     * @param value     raw identifier
     * @param fieldName field label used in the error message
     * @return upper-case identifier
     * @throws ValidationException when the format is not accepted
     */
    public static String requireId(String value, String fieldName) {
        String id = requireText(value, fieldName).toUpperCase(Locale.ROOT);
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new ValidationException(
                    fieldName + " must be 3-20 characters using letters, digits or hyphens.");
        }
        return id;
    }

    /**
     * Validates a simple e-mail address.
     *
     * @param value raw e-mail address
     * @return trimmed address
     * @throws ValidationException when the format is not accepted
     */
    public static String requireEmail(String value) {
        String email = requireText(value, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(
                    "Enter a valid email address, for example name@taibah.edu.sa.");
        }
        return email;
    }

    /**
     * Validates a phone number (digits plus the usual separators).
     *
     * @param value raw phone number
     * @return trimmed phone number
     * @throws ValidationException when the format is not accepted
     */
    public static String requirePhone(String value) {
        String phone = requireText(value, "Phone");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException(
                    "Phone must contain 7-20 characters: digits, spaces, +, - or parentheses.");
        }
        return phone;
    }

    /**
     * Parses a whole number inside an inclusive range.
     *
     * @param raw       raw text from a text field
     * @param fieldName field label used in error messages
     * @param min       smallest accepted value
     * @param max       largest accepted value
     * @return parsed number
     * @throws ValidationException when the text is not a number or is out of range
     */
    public static int requireIntInRange(String raw, String fieldName, int min, int max) {
        String text = requireText(raw, fieldName);
        int number;
        try {
            number = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new ValidationException(fieldName + " must be a whole number.");
        }
        if (number < min || number > max) {
            throw new ValidationException(
                    fieldName + " must be between " + min + " and " + max + ".");
        }
        return number;
    }
}
