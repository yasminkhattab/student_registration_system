package com.studentregistration.exception;

/**
 * Raised when a student or course identifier cannot be resolved.
 */
public class EntityNotFoundException extends RegistrationException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
