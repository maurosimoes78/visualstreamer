package org.nomanscode.visualstreamer.common.interfaces;

import org.nomanscode.visualstreamer.common.*;

import java.util.List;
import java.util.UUID;

public interface IProfile {
    IProfile find(String path);
    public String getStringValue(String defaultValue);
    public <T> T getEnumValue (T defaultValue);
    public long getLongValue(long defaultValue);
    public int getIntValue(int defaultValue);
    public boolean getBoolValue(boolean defaultValue);
    public double getDoubleValue(double defaultValue);
    public float getFloatValue(float defaultValue);
    public UUID getUUIDValue(UUID defaultValue);
    public String getScriptValue();

    public String getObjectClass();
    public List<Profile> getSub();
}
