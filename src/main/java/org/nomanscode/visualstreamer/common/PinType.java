package org.nomanscode.visualstreamer.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PinType {
    PIN_TYPE_DONT_KNOW,
    PIN_TYPE_ENTRY,
    PIN_TYPE_FILE,
    PIN_TYPE_MEDIA_FILE,
    PIN_TYPE_ANY,
    PIN_TYPE_SOURCE,
    PIN_TYPE_TARGET,
    PIN_TYPE_METADATA,
    PIN_TYPE_LOOP;

    public static List<PinType> add(PinType... types) {
        return Arrays.asList(types);
    }

    public static List<PinType> copyAll(List<PinType> types) {
        if ( types == null ) {
            return null;
        }

        return new ArrayList<>(types);
    }
}
