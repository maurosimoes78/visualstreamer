package org.nomanscode.visualstreamer.exceptions;

public class InterfaceNotImplementedException extends RuntimeException {
    public InterfaceNotImplementedException() {
        super();
    }
    public InterfaceNotImplementedException(String message) {
        super(message);
    }
    public InterfaceNotImplementedException(InterfaceNotImplementedException e) {
        super(e);
    }
}