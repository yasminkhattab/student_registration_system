package com.studentregistration.exception;

/**
 * Raised when an enrollment would exceed the course capacity.
 */
public class CourseFullException extends RegistrationException {

    public CourseFullException(String message) {
        super(message);
    }
}
