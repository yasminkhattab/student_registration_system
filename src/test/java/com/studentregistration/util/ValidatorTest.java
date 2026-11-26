package com.studentregistration.util;

import com.studentregistration.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link Validator}.
 */
class ValidatorTest {

    @Test
    @DisplayName("identifiers are trimmed and upper-cased")
    void normalisesIdentifier() {
        assertEquals("STU-1001", Validator.requireId("  stu-1001  ", "Student ID"));
    }

    @Test
    @DisplayName("blank identifiers are rejected")
    void rejectsBlankIdentifier() {
        assertThrows(ValidationException.class, () -> Validator.requireId("  ", "Student ID"));
    }

    @Test
    @DisplayName("identifiers with spaces are rejected")
    void rejectsIdentifierWithSpaces() {
        assertThrows(ValidationException.class, () -> Validator.requireId("STU 1001", "Student ID"));
    }

    @Test
    @DisplayName("valid e-mail addresses are accepted")
    void acceptsValidEmail() {
        assertEquals("student@taibah.edu.sa", Validator.requireEmail("student@taibah.edu.sa"));
    }

    @Test
    @DisplayName("e-mail addresses without a domain are rejected")
    void rejectsInvalidEmail() {
        assertThrows(ValidationException.class, () -> Validator.requireEmail("student@"));
    }

    @Test
    @DisplayName("phone numbers with separators are accepted")
    void acceptsPhoneWithSeparators() {
        assertEquals("+966 50 123 4567", Validator.requirePhone("+966 50 123 4567"));
    }

    @Test
    @DisplayName("integers outside the range are rejected")
    void rejectsOutOfRangeInteger() {
        assertThrows(ValidationException.class,
                () -> Validator.requireIntInRange("9", "Level", 1, 8));
    }

    @Test
    @DisplayName("non-numeric text is rejected")
    void rejectsNonNumericText() {
        assertThrows(ValidationException.class,
                () -> Validator.requireIntInRange("three", "Credit hours", 1, 6));
    }
}
