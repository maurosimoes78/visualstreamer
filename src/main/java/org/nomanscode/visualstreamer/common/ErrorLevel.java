package org.nomanscode.visualstreamer.common;

public enum ErrorLevel {
    INFORMATION,
    WARNING,
    COMMON_ERROR,
    SEVERE_ERROR,
    UNKNOWN;

    public static ErrorLevel fromString(String value) {
        try {
            return ErrorLevel.valueOf(value);
        }
        catch(Exception e) {
            return ErrorLevel.UNKNOWN;
        }
    }
}
