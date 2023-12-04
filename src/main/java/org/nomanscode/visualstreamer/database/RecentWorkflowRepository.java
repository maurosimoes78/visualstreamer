package org.nomanscode.visualstreamer.database;

import org.nomanscode.visualstreamer.common.RecentWorkflowInfo;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.Date;

@Repository
public class RecentWorkflowRepository extends CacheRepository<UUID, RecentWorkflowInfo>
{

    @Override
    protected void loadCache(Connection conn, Map<UUID, RecentWorkflowInfo> cache) throws SQLException, InterruptedException {

        //"SELECT DISTINCT ON (workflowid, userid) workflowid, userid, id, date, name FROM workflows.recentworkflows WHERE date >= now() - INTERVAL '30 day' ORDER BY workflowid, userid, date asc, name"

        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM workflows.recentworkflows WHERE date >= now() - INTERVAL '30 day'")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    UUID id = UUID.fromString(rs.getString("id"));
                    String name = rs.getString("name");
                    Date date = rs.getDate("date");
                    UUID workflowId = UUID.fromString(rs.getString("workflowid"));
                    UUID userId = UUID.fromString(rs.getString("userid"));
                    String category = rs.getString("groupname");
                    UUID groupId = UUID.fromString(rs.getString("groupid"));

                    RecentWorkflowInfo recentWorkflow = RecentWorkflowInfo.create(id, name, date, workflowId, userId, category, groupId);
                    cache.put(id, recentWorkflow);
                }
            }
        }
    }

    @Override
    protected RecentWorkflowInfo set(Connection conn, RecentWorkflowInfo t) throws SQLException
    {
        if (t.getId() == null) {
            throw new SQLException("Invalid workflow id");
        }

        String sql = "INSERT INTO workflows.recentworkflows (id, name, workflowid, userid, date, groupname, groupid) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (userid, workflowid) DO UPDATE SET name = ?, date = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, t.getId());
            stmt.setString(2, t.getName());
            stmt.setObject(3, t.getWorkflowId());
            stmt.setObject(4, t.getUserId());
            stmt.setTimestamp(5, new Timestamp( t.getDate().getTime() ));
            stmt.setString(6, t.getGroupName());
            stmt.setObject(7, t.getGroupId());
            stmt.setString(8, t.getName());
            stmt.setTimestamp(9, new Timestamp( t.getDate().getTime() ));
            stmt.executeUpdate();
        }

        return t;
    }

    @Override
    protected boolean del(Connection conn, UUID id) throws SQLException
    {
        boolean result;

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM workflows.recentWorkflows WHERE id = ?")) {
            stmt.setObject(1, id);
            result = (stmt.executeUpdate() > 0);
        }

        return result;
    }

    public boolean setRecentWorkflow(final String name, final UUID workflowId, final UUID userId, final String groupName, final UUID groupId) {
        return setRecentWorkflow(RecentWorkflowInfo.create(name, new Date(), workflowId, userId, groupName, groupId));
    }

    public boolean setRecentWorkflow(RecentWorkflowInfo recent) {

        try {
            if ( recent == null ) {
                return false;
            }

            this.set(recent);

            return true;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
