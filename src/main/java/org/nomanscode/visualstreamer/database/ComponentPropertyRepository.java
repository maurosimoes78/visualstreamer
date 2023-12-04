package org.nomanscode.visualstreamer.database;

import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.exceptions.CybertronException;
import org.springframework.stereotype.Repository;

import java.util.Base64;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Repository
public class ComponentPropertyRepository extends PropertyRepository
{
    public String getValue(String key, UUID componentId, String defaultValue) throws CybertronException, InterruptedException
    {
        return getValue(new Property.Id(key, componentId), defaultValue);
    }

    public long getLongValue(String key, UUID componentId, long defaultValue) throws CybertronException, InterruptedException
    {
        return getLongValue(new Property.Id(key, componentId), defaultValue);
    }

    public int getIntValue(String key, UUID componentId, int defaultValue) throws CybertronException, InterruptedException
    {
        return getIntValue(new Property.Id(key, componentId), defaultValue);
    }

    public boolean getBoolValue(String key, UUID componentId, boolean defaultValue) throws CybertronException, InterruptedException
    {
        return getBoolValue(new Property.Id(key, componentId), defaultValue);
    }

    public double getDoubleValue(String key, UUID componentId, double defaultValue) throws CybertronException, InterruptedException
    {
        return getDoubleValue(new Property.Id(key, componentId), defaultValue);
    }

    public UUID getUUIDValue(String key, UUID componentId, UUID defaultValue) throws CybertronException, InterruptedException
    {
        return getUUIDValue(new Property.Id(key, componentId), defaultValue);
    }

    public void setValue(String key, UUID componentId, String value) throws CybertronException, InterruptedException
    {
        setValue(new Property.Id(key, componentId), value);
    }

    public void setValue(String key, UUID componentId, long value) throws CybertronException, InterruptedException
    {
        setLongValue(new Property.Id(key, componentId), value);
    }

    public void setValue(String key, UUID componentId, int value) throws CybertronException, InterruptedException
    {
        setIntValue(new Property.Id(key, componentId), value);
    }

    public void setValue(String key, UUID componentId, boolean value) throws CybertronException, InterruptedException
    {
        setBoolValue(new Property.Id(key, componentId), value);
    }

    public void setValue(String key, UUID componentId, double value) throws CybertronException, InterruptedException
    {
        setDoubleValue(new Property.Id(key, componentId), value);
    }

    public void setValue(String key, UUID componentId, UUID value) throws CybertronException, InterruptedException
    {
        setUUIDValue(new Property.Id(key, componentId), value);
    }

    public boolean delValue(String key, UUID componentId) throws CybertronException, InterruptedException
    {
        return del(new Property.Id(key, componentId));
    }

    @Override
    protected void loadCache(Connection conn, Map<Property.Id, Property> cache) throws SQLException
    {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT component_id, key, value FROM components.property ORDER BY component_id, key")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Property property = new Property(rs.getString("key"), UUID.fromString(rs.getString("component_id")), rs.getString("value"));
                    cache.put(property.getId(), property);
                }
            }
        }
    }

    @Override
    protected Property set(Connection conn, Property value) throws SQLException
    {
        String sql = "INSERT INTO components.property (id, component_id, key, value) VALUES (?, ?, ?, ?) ON CONFLICT (component_id, key) DO UPDATE SET value = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setObject(2, value.getId().getUuid());
            stmt.setString(3, value.getId().getKey());
            stmt.setString(4, value.getValue());
            stmt.setString(5, value.getValue());
            stmt.executeUpdate();
        }

        return value;
    }

    @Override
    protected boolean del(Connection conn, Property.Id id) throws SQLException
    {
        boolean result;

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM components.property WHERE component_id = ? AND key = ?")) {
            stmt.setObject(1, id.getUuid());
            stmt.setString(2, id.getKey());
            result = (stmt.executeUpdate() > 0);
        }

        return result;
    }
}
