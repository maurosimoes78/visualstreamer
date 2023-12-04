package org.nomanscode.visualstreamer.common.interfaces;

import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.common.interfaces.*;

import java.util.List;
import java.util.UUID;

public interface IInputPin extends IPin {

    public UUID getUUID ();

    public IComponentControl findPluginPassThru(PluginType type);

    public boolean isCompatible(PinType type);
    public boolean isCompatible(List<PinType> type);

    public void requestPinTypesReset();
}
