package com.studentregistration.exception;

/**
 * Raised when a student tries to enroll twice in the same course.
 */
public class DuplicateEnrollmentException extends RegistrationException {

    public DuplicateEnrollmentException(String message) {
        super(message);
    }
}
