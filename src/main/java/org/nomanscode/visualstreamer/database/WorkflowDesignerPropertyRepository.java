package org.nomanscode.visualstreamer.database;

import org.nomanscode.visualstreamer.exceptions.CybertronException;
import org.nomanscode.visualstreamer.common.Property;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.UUID;

@Repository
public class WorkflowDesignerPropertyRepository extends PropertyRepository {

    public String getValue(String key, UUID workflowId, String defaultValue) throws CybertronException, InterruptedException
    {
        return getValue(new Property.Id(key, workflowId), defaultValue);
    }

    public long getLongValue(String key, UUID workflowId, long defaultValue) throws CybertronException, InterruptedException
    {
        return getLongValue(new Property.Id(key, workflowId), defaultValue);
    }

    public int getIntValue(String key, UUID workflowId, int defaultValue) throws CybertronException, InterruptedException
    {
        return getIntValue(new Property.Id(key, workflowId), defaultValue);
    }

    public boolean getBoolValue(String key, UUID workflowId, boolean defaultValue) throws CybertronException, InterruptedException
    {
        return getBoolValue(new Property.Id(key, workflowId), defaultValue);
    }

    public double getDoubleValue(String key, UUID workflowId, double defaultValue) throws CybertronException, InterruptedException
    {
        return getDoubleValue(new Property.Id(key, workflowId), defaultValue);
    }

    public UUID getUUIDValue(String key, UUID workflowId, UUID defaultValue) throws CybertronException, InterruptedException
    {
        return getUUIDValue(new Property.Id(key, workflowId), defaultValue);
    }

    public void setValue(String key, UUID workflowId, String value) throws CybertronException, InterruptedException
    {
        setValue(new Property.Id(key, workflowId), value);
    }

    public void setLongValue(String key, UUID workflowId, long value) throws CybertronException, InterruptedException
    {
        setLongValue(new Property.Id(key, workflowId), value);
    }

    public void setIntValue(String key, UUID workflowId, int value) throws CybertronException, InterruptedException
    {
        setIntValue(new Property.Id(key, workflowId), value);
    }

    public void setBoolValue(String key, UUID workflowId, boolean value) throws CybertronException, InterruptedException
    {
        setBoolValue(new Property.Id(key, workflowId), value);
    }

    public void setDoubleValue(String key, UUID workflowId, double value) throws CybertronException, InterruptedException
    {
        setDoubleValue(new Property.Id(key, workflowId), value);
    }

    public void setUUIDValue(String key, UUID workflowId, UUID value) throws CybertronException, InterruptedException
    {
        setUUIDValue(new Property.Id(key, workflowId), value);
    }

    public boolean delValue(String key, UUID workflowId) throws CybertronException, InterruptedException
    {
        return del(new Property.Id(key, workflowId));
    }

    @Override
    protected void loadCache(Connection conn, Map<Property.Id, Property> cache) throws SQLException
    {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT workflow_id, key, value FROM workflowdesigner.property ORDER BY workflow_id, key")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Property property = new Property(rs.getString("key"), UUID.fromString(rs.getString("workflow_id")), rs.getString("value"));
                    cache.put(property.getId(), property);
                }
            }
        }
    }

    @Override
    protected Property set(Connection conn, Property value) throws SQLException
    {
        String sql = "INSERT INTO workflowdesigner.property (id, workflow_id, key, value) VALUES (?, ?, ?, ?) ON CONFLICT (workflow_id, key) DO UPDATE SET value = ?";
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

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM workflowdesigner.property WHERE workflow_id = ? AND key = ?")) {
            stmt.setObject(1, id.getUuid());
            stmt.setString(2, id.getKey());
            result = (stmt.executeUpdate() > 0);
        }

        return result;
    }
}