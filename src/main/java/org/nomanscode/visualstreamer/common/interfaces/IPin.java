package org.nomanscode.visualstreamer.common.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.common.Pin;

import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value= Pin.class, name = "Pin")
})
public interface IPin {

    public String getName();
    public UUID getUUID();
    public PinDirection getPinDirection();
    public List<PinType> getPinTypes();
    public PinConnection connect(IPin pin);
    public void disconnect();

    public UUID getEquivalentUUID();
    public IComponentControl getComponentControl();

    public UUID getComponentId();
    public UUID getConnectedToId();
    public String getComponentName();
    public ComponentStatus runAnalysis();
    public IComponentControl findPluginPassThru(PluginType type);
    public IComponentControl findPluginPassThru(PluginType type, boolean skip);
    public boolean isConnected();

    public boolean acceptPinTypesChanges(List<PinType> types);
    public boolean changePinTypes(List<PinType> types);
    public void resetPinTypes();
}
