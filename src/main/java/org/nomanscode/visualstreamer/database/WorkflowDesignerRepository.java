package org.nomanscode.visualstreamer.database;

import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.types.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

//import javax.xml.ws.Holder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class WorkflowDesignerRepository extends CacheRepository<UUID, WorkflowInfo>
{
    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private PluginRepository templateRepository;

    @Autowired
    private WorkflowDesignerPropertyRepository workflowDesignerPropertyRepository;

    @Override
    protected void loadCache(Connection conn, Map<UUID, WorkflowInfo> cache) throws SQLException, InterruptedException {

        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM workflowdesigner.workflow ORDER BY name")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID id = UUID.fromString(rs.getString("id"));

                    //Unnecessary, since owner id is stored into workflow data structure.
                    //UUID ownerId = UUID.fromString(rs.getString("ownerid"));

                    String workflowString = workflowDesignerPropertyRepository.getValue("data", id, "");
                    if ( workflowString == null || workflowString.isEmpty() ) {
                        continue;
                    }

                    WorkflowInfo workflow = WorkflowInfo.fromJSON(workflowString);
                    if ( workflow == null ) {
                        continue;
                    }

                    cache.put(id, workflow);
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

        String sql = "INSERT INTO workflowdesigner.workflow (id, name, description, createdin) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = ?, description = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, t.getId());
            stmt.setString(2, t.getName());
            stmt.setString(3, t.getDescription());
            stmt.setTimestamp(5, new java.sql.Timestamp( new Date().getTime() ));
            stmt.setString(6, t.getName());
            stmt.setString(7, t.getDescription());
            stmt.executeUpdate();
        }

        return t;
    }

    @Override
    protected boolean del(Connection conn, UUID id) throws SQLException
    {
        boolean result;

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM workflowdesigner.workflow WHERE id = ?")) {
            stmt.setObject(1, id);
            result = (stmt.executeUpdate() > 0);
        }

        return result;
    }

    public boolean setWorkflow( Workflow workflow, MyHolder<String> errorMessage) {

        try {

            if ( workflow == null ) {
                return false;
            }

            this.set(workflow);

            WorkflowInfo info = WorkflowInfo.copy(workflow);
            String jsonString = info.toJSON();

            this.workflowDesignerPropertyRepository.setValue("data", workflow.getId(), jsonString);

            return true;
        }
        catch(InterruptedException e) {
            if ( errorMessage != null ) {
                HolderHelper.setHolderValue(errorMessage, e.getMessage());
            }
            return false;
        }

    }
}
