package com.studentregistration.exception;

/**
 * Raised when user input or stored data violates a field rule.
 */
public class ValidationException extends RegistrationException {

    public ValidationException(String message) {
        super(message);
    }
}
