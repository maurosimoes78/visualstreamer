package org.nomanscode.visualstreamer.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SystemControlType {
    SYSTEM_CONTROL_TYPE_UNDEFINED,
    SYSTEM_CONTROL_TYPE_SYSTEM,
    SYSTEM_CONTROL_TYPE_LOOP;

    public static List<SystemControlType> add(SystemControlType ... controlTypes) {
        return Arrays.stream(controlTypes).collect(Collectors.toList());
    }
}
