package org.nomanscode.visualstreamer.database;

import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.exceptions.CybertronException;

import java.io.*;
import java.util.UUID;

public abstract class PropertyRepository extends CacheRepository<Property.Id, Property>
{
    public String getValue(Property.Id id, String defaultValue) throws CybertronException, InterruptedException
    {
        Property value = get(id);
        if (value != null) {
            return value.getValue();
        }
        return defaultValue;
    }

    public long getLongValue(Property.Id id, long defaultValue) throws CybertronException, InterruptedException
    {
        try {
            return Long.valueOf(getValue(id, String.valueOf(defaultValue)));
        } catch (CybertronException | InterruptedException ex) {
            throw ex;
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public int getIntValue(Property.Id id, int defaultValue) throws CybertronException, InterruptedException
    {
        try {
            return Integer.valueOf(getValue(id, String.valueOf(defaultValue)));
        } catch (CybertronException | InterruptedException ex) {
            throw ex;
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public boolean getBoolValue(Property.Id id, boolean defaultValue) throws CybertronException, InterruptedException
    {
        try {
            return Boolean.valueOf(getValue(id, String.valueOf(defaultValue)));
        } catch (CybertronException | InterruptedException ex) {
            throw ex;
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public double getDoubleValue(Property.Id id, double defaultValue) throws CybertronException, InterruptedException
    {
        try {
            return Double.valueOf(getValue(id, String.valueOf(defaultValue)));
        } catch (CybertronException | InterruptedException ex) {
            throw ex;
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public UUID getUUIDValue(Property.Id id, UUID defaultValue) throws CybertronException, InterruptedException
    {
        try {
            return UUID.fromString(getValue(id, String.valueOf(defaultValue)));
        } catch (CybertronException | InterruptedException ex) {
            throw ex;
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public void setValue(Property.Id id, String value) throws CybertronException, InterruptedException
    {
        set(new Property(id, value));
    }

    public void setLongValue(Property.Id id, long value) throws CybertronException, InterruptedException
    {
        set(new Property(id, value));
    }

    public void setIntValue(Property.Id id, int value) throws CybertronException, InterruptedException
    {
        set(new Property(id, value));
    }

    public void setBoolValue(Property.Id id, boolean value) throws CybertronException, InterruptedException
    {
        set(new Property(id, value));
    }

    public void setDoubleValue(Property.Id id, double value) throws CybertronException, InterruptedException
    {
        set(new Property(id, value));
    }

    public void setUUIDValue(Property.Id id, UUID value) throws CybertronException, InterruptedException
    {
        set(new Property(id, value));
    }
}
