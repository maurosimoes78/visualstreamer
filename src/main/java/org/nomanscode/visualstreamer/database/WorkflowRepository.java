package org.nomanscode.visualstreamer.database;

import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.types.Workflow;
import org.nomanscode.visualstreamer.database.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class WorkflowRepository extends CacheRepository<UUID, WorkflowInfo>
{
    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private PluginRepository templateRepository;

    @Autowired
    private WorkflowPropertyRepository workflowPropertyRepository;

    @Override
    protected void loadCache(Connection conn, Map<UUID, WorkflowInfo> cache) throws SQLException, InterruptedException {

        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM workflows.workflow ORDER BY name")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID id = UUID.fromString(rs.getString("id"));

                    String workflowString = workflowPropertyRepository.getValue("data", id, "");
                    if ( workflowString == null || workflowString.isEmpty() ) {
                        continue;
                    }

                    WorkflowInfo workflowInfo = WorkflowInfo.fromJSON(workflowString);
                    if ( workflowInfo == null ) {
                        continue;
                    }

                    cache.put(id, workflowInfo);
                }
            }
        }
    }

    @Override
    protected WorkflowInfo set(Connection conn, WorkflowInfo t) throws SQLException
    {
        if (t.getId() == null) {
            throw new SQLException("Invalid workflow id");
        }

        String sql = "INSERT INTO workflows.workflow (id, name, description) VALUES (?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = ?, description = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, t.getId());
            stmt.setString(2, t.getName());
            stmt.setString(3, t.getDescription());
            stmt.setString(5, t.getName());
            stmt.setString(6, t.getDescription());
            stmt.executeUpdate();
        }

        return t;
    }

    @Override
    protected boolean del(Connection conn, UUID id) throws SQLException
    {
        boolean result;

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM workflows.workflow WHERE id = ?")) {
            stmt.setObject(1, id);
            result = (stmt.executeUpdate() > 0);
        }

        return result;
    }

    public boolean setWorkflow(Workflow workflow) {

        try {
            if ( workflow == null ) {
                return false;
            }

            this.set(workflow);

            WorkflowInfo info = WorkflowInfo.copy(workflow);
            String jsonString = info.toJSON();

            this.workflowPropertyRepository.setValue("data", workflow.getId(), jsonString);

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
