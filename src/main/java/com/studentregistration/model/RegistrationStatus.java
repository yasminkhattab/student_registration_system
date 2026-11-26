package com.studentregistration.model;

/**
 * Lifecycle of a registration record.
 */
public enum RegistrationStatus {

    /** The student is currently enrolled in the course. */
    ACTIVE("Active"),

    /** The student dropped the course; the row is kept for history. */
    DROPPED("Dropped");

    private final String displayName;

    RegistrationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
