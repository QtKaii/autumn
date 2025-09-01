package exceptions;

import logging.AssignmentLogger;

/**
 * Thrown when the application cannot resolve a user-entered instrument name to
 * a known instrument type.
 */
public class UnrecognizedInstrumentException extends Exception {
    /**
     * Creates the exception with a descriptive message.
     *
     * @param message details about the failure
     */
    public UnrecognizedInstrumentException(String message) {
        super(message);
        AssignmentLogger.logConstructor(this);
    }
}
