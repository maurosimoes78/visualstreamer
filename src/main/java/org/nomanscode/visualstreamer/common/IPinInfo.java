package org.nomanscode.visualstreamer.common;

import java.util.List;
import java.util.UUID;

public interface IPinInfo {
    UUID getUUID();
    PinDirection getPinDirection();
    List<PinType> getPinTypes();
    boolean getConnectionRequired();
    boolean isConnected();
}
