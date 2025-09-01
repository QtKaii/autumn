package exceptions;

import logging.AssignmentLogger;

public class UnrecognizedInstrumentException extends Exception {
    public UnrecognizedInstrumentException(String message) {
        super(message);
        AssignmentLogger.logConstructor(this);
    }
}