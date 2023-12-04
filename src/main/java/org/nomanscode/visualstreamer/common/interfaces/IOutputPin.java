package org.nomanscode.visualstreamer.common.interfaces;

import org.nomanscode.visualstreamer.common.*;

import java.util.List;
import java.util.UUID;

public interface IOutputPin extends IPin {
    public PinConnection connect(IPin pin);
    public UUID getUUID ();
    public IComponentControl findPluginPassThru(PluginType type);
    public IComponentControl findPluginPassThru(PluginType type, boolean skip);
    public boolean isConnected();
    public void requestPinTypesReset();
}
