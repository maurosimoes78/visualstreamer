package org.nomanscode.visualstreamer.common.interfaces;

import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.common.interfaces.*;

import java.util.List;
import java.util.UUID;

public interface IPinInfo {
    UUID getUUID();
    PinDirection getPinDirection();
    List<PinType> getPinTypes();
    boolean getConnectionRequired();
    boolean isConnected();
}
