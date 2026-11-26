package com.studentregistration.exception;

/**
 * Wraps an {@link java.io.IOException} raised by the CSV persistence layer.
 *
 * <p>Repositories translate low-level I/O failures into this unchecked
 * exception so that the GUI can report a single, understandable error while the
 * original exception stays available as the cause.</p>
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
