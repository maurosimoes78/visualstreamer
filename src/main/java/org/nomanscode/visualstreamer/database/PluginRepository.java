package org.nomanscode.visualstreamer.database;

import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.exceptions.CybertronException;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.nomanscode.visualstreamer.common.ErrorCode.PLUGIN_ERROR;

@Repository
public class PluginRepository extends CacheRepository<UUID, PluginInfo>
{
    @Override
    protected void loadCache(Connection conn, Map<UUID, PluginInfo> cache) throws SQLException
    {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM plugins.plugin")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    UUID id = UUID.fromString(rs.getString("id"));

                    PluginInfo plugin = new PluginInfo(id,
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("path"),
                            rs.getBoolean("enabled"),
                            true,
                            false,
                            "fas fa-puzzle-piece",
                            CybertronRGBColor.GRAY());

                    cache.put(id, plugin);
                }
            }
        }
    }

    @Override
    protected PluginInfo set(Connection conn, PluginInfo t) throws SQLException
    {
        if (t.getId() == null) {
            throw new CybertronException(ErrorCode.PLUGIN_ERROR, "Invalid id!", "Plugin id must be set prior to recording the plugin into database");
        }

        String sql = "INSERT INTO plugins.plugin (id, name, description, path, enabled) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = ?, description = ?, path = ?, enabled = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, t.getId());
            stmt.setString(2, t.getName());
            stmt.setString(3, t.getDescription());
            stmt.setString(4, t.getPath());
            stmt.setBoolean(5, t.getEnabled());
            stmt.setString(6, t.getName());
            stmt.setString(7, t.getDescription());
            stmt.setString(8, t.getPath());
            stmt.setBoolean(9, t.getEnabled());
            stmt.executeUpdate();
        }

        return t;
    }

    @Override
    protected boolean del(Connection conn, UUID id) throws SQLException
    {
        boolean result;

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM plugins.plugin WHERE id = ?")) {
            stmt.setObject(1, id);
            result = (stmt.executeUpdate() > 0);
        }

        return result;
    }
}
