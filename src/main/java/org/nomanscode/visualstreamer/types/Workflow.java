package org.nomanscode.visualstreamer.types;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nomanscode.visualstreamer.common.PinConnection;
import org.nomanscode.visualstreamer.common.ThreadUtils;
import org.nomanscode.visualstreamer.common.WorkflowInfo;
import org.nomanscode.visualstreamer.common.ComponentInfo;
import org.nomanscode.visualstreamer.common.WorkflowRequest;


import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class Workflow extends WorkflowInfo {

    @JsonIgnore
    private Lock lock = new ReentrantLock();

    @JsonIgnore
    private Date initialDate = new Date();

    @JsonIgnore
    private Date lastModification = initialDate;

    @JsonIgnore
    private boolean allowModificationDateChanges = true;

    @JsonIgnore
    public Workflow()
    {
        super();
    }

    @JsonIgnore
    public Workflow(final WorkflowInfo workflow)
    {
        super(workflow);
    }

    @JsonIgnore
    public Workflow(final WorkflowInfo workflow, UUID lockedById)
    {
        super(workflow, lockedById);
    }

    @JsonIgnore
    public Workflow(final String name,
                    final String description)
    {
        super(UUID.randomUUID(), name, description);
    }

    @JsonCreator
    public Workflow(@JsonProperty("id") final UUID id,
                    @JsonProperty("name") final String name,
                    @JsonProperty("description") final String description,
                    @JsonProperty("components") List<ComponentInfo> components,
                    @JsonProperty("connections") List<PinConnection> connections)
    {
        super(id, name, description, components, connections);
    }

    @JsonIgnore
    public Workflow( final String name, final String description, List<ComponentInfo> components)
    {
        super (UUID.randomUUID(), name, description, components);
    }

    @JsonIgnore
    public WorkflowInfo getWorkflowInfo()
    {
        return this;
    }

    @JsonIgnore
    public boolean isModified() {

        int result = this.lastModification.compareTo(this.initialDate);
        if ( result == 0 ) {
            return false; //No change detected
        }

        return true; //Something has changed!!
    }

    @JsonIgnore
    public void notifyWorkflowChange() {
        if ( !allowModificationDateChanges ) {
            return;
        }

        this.setRequireSave(true);
        this.lastModification = new Date();
    }

    @JsonIgnore
    public void notifyWorkflowSaved() {
        this.lastModification = this.getModified();
        this.initialDate = this.lastModification;
        this.setRequireSave(false);
    }

    @JsonIgnore
    public void setAllowModificationDateChanges(boolean value) {
        this.allowModificationDateChanges = value;
    }

    @JsonIgnore
    public boolean isNotificationAllowed() {
        return this.allowModificationDateChanges;
    }

    @JsonIgnore
    public boolean lock()
    {
        try {

            ThreadUtils.lock(this.lock);

            return true;
        }
        catch(Exception e)
        {
            //TODO:
            return false;
        }
        finally {
            ThreadUtils.unlock(this.lock);
        }
    }

    @JsonIgnore
    public boolean unLock()
    {
        try {

            ThreadUtils.lock(this.lock);

            return true;
        }
        catch(Exception e)
        {
            //TODO:
            return false;
        }
        finally {
            ThreadUtils.unlock(this.lock);
        }
    }

    // -------------------------------------------------------

    @JsonIgnore
    public void addConnection(PinConnection connection) {
        try {

            //Removes the connection entry
            this.getConnections().put(connection.getConnectionId(), connection);

            //Notifies workflow has been changed
            this.notifyWorkflowChange();
        }
        catch(Exception e) {

        }
    }

    @JsonIgnore
    public void deleteConnection(UUID connectionId) {
        try {

            //Removes the connection entry
            this.getConnections().remove(connectionId);

            //Notifies workflow has been changed
            this.notifyWorkflowChange();
        }
        catch(Exception e) {

        }
    }

    @JsonIgnore
    public PinConnection findConnection(UUID componentId, UUID pinId) {
        try {
            return this.getConnections().values().stream()
                    .filter( c -> (c.getSourceComponentId().equals(componentId) &&
                                   c.getSourcePinId().equals(pinId)) ||
                                  (c.getTargetComponentId().equals(componentId) &&
                                   c.getTargetPinId().equals(pinId)) )
                    .findAny()
                    .orElse(null);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public List<PinConnection> getConnections(UUID componentId, UUID pinId) {
        try {
            return this.getConnections().values().stream()
                    .filter( c -> (c.getSourceComponentId().equals(componentId) &&
                            c.getSourcePinId().equals(pinId)) ||
                            (c.getTargetComponentId().equals(componentId) &&
                                    c.getTargetPinId().equals(pinId)) )
                    .map( PinConnection::new )
                    .collect(Collectors.toList());
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public PinConnection findConnection(UUID connectionId)
    {
        try {
            return this.getConnections().entrySet().stream()
                                .filter( e -> e.getKey().equals(connectionId))
                                .map( Map.Entry::getValue)
                                .findAny()
                                .orElse(null);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    //------------------------------------------------------------------------------
    @JsonIgnore
    public static Workflow create(WorkflowInfo workflowInfo, final UUID lockedById)
    {
        try {
            return new Workflow(workflowInfo, lockedById);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static Workflow create(final WorkflowInfo workflow, final WorkflowRequest request, final UUID ownerId, final UUID lockedById)
    {
        try {
            workflow.setName(request.getName());
            workflow.setId(UUID.randomUUID());
            workflow.setDescription(request.getDescription());

            //TODO: Keywords?
            //workflow.setKeywords(request.getKeywords());

            return new Workflow(workflow, lockedById);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static Workflow create(final String name, final String description)
    {
        try {
            return new Workflow(name, description);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static Workflow create(final String name, final String description, List<ComponentInfo> components)
    {
        try {
            return new Workflow(name, description, components);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static Workflow create(final UUID id, final String name, final String description, List<ComponentInfo> components, List<PinConnection> connections)
    {
        try {
            return new Workflow(id, name, description, components, connections);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static Workflow create(WorkflowInfo workflowinfo)
    {
        try {
            return new Workflow(workflowinfo);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static Workflow copy(Workflow workflow)
    {
        try {
            return new Workflow(workflow);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public String toJSONPretty() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch( Exception e)
        {
            return null;
        }
    }
}
