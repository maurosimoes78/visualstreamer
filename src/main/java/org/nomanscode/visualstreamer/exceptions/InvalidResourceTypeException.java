package org.nomanscode.visualstreamer.exceptions;

public class InvalidResourceTypeException extends RuntimeException {

    public InvalidResourceTypeException() {
        super();
    }
    public InvalidResourceTypeException(String message) {
        super(message);
    }
    public InvalidResourceTypeException(InvalidResourceTypeException e) {
        super(e);
    }
}
