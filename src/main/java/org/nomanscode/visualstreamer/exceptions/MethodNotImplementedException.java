package org.nomanscode.visualstreamer.exceptions;

public class MethodNotImplementedException extends RuntimeException {
    public MethodNotImplementedException() {
        super();
    }
    public MethodNotImplementedException(String message) {
        super(message);
    }
    public MethodNotImplementedException(MethodNotImplementedException e) {
        super(e);
    }
}
