package com.studentregistration.exception;

/**
 * Base class for every business-rule violation raised by the application.
 *
 * <p>Using one unchecked exception hierarchy keeps GUI controllers small: they
 * can catch {@code RegistrationException} and show {@link #getMessage()} to the
 * user without inspecting implementation details.</p>
 */
public class RegistrationException extends RuntimeException {

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
