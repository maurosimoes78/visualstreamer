package org.nomanscode.visualstreamer.common.interfaces;

import org.nomanscode.visualstreamer.common.*;

//import javax.xml.ws.Holder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IComponentControl {
    public String getTag();
    public void setTag(String tag);
    public UUID getId();
    public boolean run();
    public void pause();
    public void abort();
    public void step();
    public String getName();
    public Integer getProgress();
    public boolean isStatusFinal();
    public ComponentStatus getStatus();
    public IInputPin createInputPin(UUID uuid, String name, boolean connectionRequired, List<PinType> pinTypes, boolean doNotBlockComponent, boolean ignoreComponentStatus);
    public IOutputPin createOutputPin(UUID uuid, String name, List<PinType> pinTypes);
    public Pin getPin(PinDirection pindir, int pos);
    public void addPin(Pin pin);
    public Map<UUID, InputPin> getInputPinInstances();
    public Map<UUID, OutputPin> getOutputPinInstances();
    public Integer getNumberOfInstancedPins();
    public PluginInfo getPluginInfo();
    public void resetProfile();
    public String getIcon();
    public PluginType getType();
    public boolean checkProfileChanges(Profile profile, MyHolder<String> errorMessage);

    public IComponentControl findPluginPassThru(IOutputPin pin, PluginType type, boolean skip);

    public IPin getPinByType(PinType type);
    public IPin getPinByType(PinDirection dir, PinType type);
    public IPin getOutputPinByType(PinType type);
    public IPin getInputPinByType(PinType type);

}