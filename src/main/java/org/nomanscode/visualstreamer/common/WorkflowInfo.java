package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.glookast.commons.xml.XmlAdapterUUID;
import org.nomanscode.visualstreamer.common.*;
//import org.nomanscode.visualstreamer.sdk.job.JobStatus;

//import javax.xml.bind.annotation.*;
//import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class WorkflowInfo extends WorkflowBasicInfo implements Serializable
{

    @JsonIgnore
    private Map<UUID, ComponentInfo> components;

    @JsonIgnore
    private Map<UUID, PinConnection> connections;

    @JsonIgnore
    private int eventNumber = 0;

    /*@JsonIgnore
    private JobStatus status_ = JobStatus.JOB_STATUS_IDLE;*/

    @JsonIgnore
    private int progress_ = 0;

    @JsonIgnore
    private PluginType defaultComponentEntryType_;

    /**
     * Default no-arg constructor
     */
    public WorkflowInfo()
    {
        super();
    }

    public WorkflowInfo(final UUID id, final String name, final String description)
    {
        super.setId(id);
        super.setName(name);
        super.setDescription(description);
    }

    public WorkflowInfo(final UUID id, final String name, final String description, List<ComponentInfo> components)
    {
        super.setId(id);
        super.setName(name);
        super.setDescription(description);

        this.components = ComponentInfo.copyAllMapping(components);
    }

    public WorkflowInfo(final UUID id, final String name, final String description, List<ComponentInfo> components, List<PinConnection> connections)
    {
        super.setId(id);
        super.setName(name);
        super.setDescription(description);

        this.components = ComponentInfo.copyAllMapping(components);
        this.connections = PinConnection.copyAllMapping(connections);
    }

    /**
     * JSON Fully-initialising value constructor
     */
    @JsonCreator
    public WorkflowInfo(@JsonProperty("id") final UUID id,
                        @JsonProperty("name") final String name,
                        @JsonProperty("description") final String description,
                        @JsonProperty("published") final boolean published,
                        @JsonProperty("components") Map<UUID, ComponentInfo> components,
                        @JsonProperty("connections") Map<UUID, PinConnection> connections,
                        @JsonProperty("created") final Date created,
                        @JsonProperty("modified") final Date modified)
    {
        super.setId(id);
        super.setName(name);
        super.setDescription(description);
        super.setPublished(published);

        //this.params = WorkflowParameter.copyAll(parameters);
        this.connections = PinConnection.copyAll(connections);
        this.components = ComponentInfo.copyAll(components);

        super.setCreated(created);
        super.setModified(modified);

    }

    public WorkflowInfo(final WorkflowInfo w)
    {
        if ( w == null ) {
            return;
        }

        super.setId(w.getId());
        super.setName(w.getName());
        super.setDescription(w.getDescription());
        super.setPublished(w.isPublished());

        ///this.params = WorkflowParameter.copyAll(w.params);
        this.components = ComponentInfo.copyAll(w.components);
        this.connections = PinConnection.copyAll(w.connections);

        super.setModified(w.getModified());
        super.setCreated(w.getCreated());
        super.setRequireSave(w.getRequireSave());

        this.eventNumber = w.eventNumber;
        //this.status_ = w.status_;
        this.progress_ = w.progress_;
        this.defaultComponentEntryType_ = w.defaultComponentEntryType_;
    }

    public WorkflowInfo(final WorkflowInfo w, final UUID lockedById)
    {
        if ( w == null ) {
            return;
        }

        super.setId(w.getId());
        super.setName(w.getName());
        super.setDescription(w.getDescription());
        super.setPublished(w.isPublished());

        //this.params = WorkflowParameter.copyAll(w.params);

        this.components = ComponentInfo.copyAll(w.components);
        this.connections = PinConnection.copyAll(w.connections);

        super.setModified(w.getModified());
        super.setCreated(w.getCreated());
        super.setRequireSave(w.getRequireSave());

        this.eventNumber = w.eventNumber;
        this.defaultComponentEntryType_ = w.defaultComponentEntryType_;
    }

    /**
     * Gets the value of the event number property.
     *
     * @return possible object is
     * {@link int }
     */
    @JsonProperty("eventnumber")
    public int getEventNumber()
    {
        return this.eventNumber;
    }

    /**
     * Sets the value of the event number property.
     *
     * @param value allowed object is
     *              {@link int }
     */

    public void setEventNumber(int value)
    {
        this.eventNumber = value;
    }

    /**
     * Gets the value of the components list.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the components property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComponents().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Map<UUID, ComponentInfo> }
     */
    @JsonProperty("components")
    public Map<UUID, ComponentInfo> getComponents()
    {
        if (this.components == null) {
            this.components = new LinkedHashMap<>();
        }
        return this.components;
    }

    public void setComponents(Map<UUID, ComponentInfo> components)
    {
        this.components = new LinkedHashMap<>(components);
    }

    /**
     * Gets the value of the connections.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the connections property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConnections().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link List<PinConnection> }
     */
    @JsonProperty("connections")
    public Map<UUID, PinConnection> getConnections()
    {
        if (this.connections == null) {
            this.connections = new LinkedHashMap<>();
        }
        return this.connections;
    }

    public void setConnections(Map<UUID, PinConnection> connections)
    {
        this.connections = PinConnection.copyAll(connections);
    }

    /*@JsonProperty("status")
    public JobStatus getStatus() {
        return this.status_;
    }*/

    /*@JsonIgnore
    public void setStatus(JobStatus value) {
        this.status_ = value;
    }*/

    @JsonProperty("progress")
    public Integer getProgress() {
        return this.progress_;
    }

    @JsonIgnore
    public void setProgress(Integer value) {
        this.progress_ = value;
    }

    @JsonProperty("defaultentrytype")
    public PluginType getDefaultComponentEntryType() {
        return this.defaultComponentEntryType_;
    }

    @JsonProperty("defaultentrytype")
    public void setDefaultComponentEntryType(PluginType value) {
        this.defaultComponentEntryType_ = value;
    }

    @Override
    @JsonIgnore
    public String toString()
    {
        return "WorkflowInfo { id= " + super.getId() + ", name= " + super.getName() + ", description= " + super.getDescription() + ", components= " + getComponents() + ", modified= " + super.getModified() + ", eventNumber= " + this.eventNumber + " }";
    }

    //------------------------------------------------------------------------


    @JsonIgnore
    public static WorkflowInfo copy (WorkflowInfo workflowInfo)
    {
        if ( workflowInfo == null ) {
            return null;
        }

        return new WorkflowInfo(workflowInfo);
    }

    @JsonIgnore
    public static List<WorkflowInfo> copyAll (List<WorkflowInfo> workflowInfoList)
    {
        if ( workflowInfoList == null ) {
            return null;
        }

        return workflowInfoList.stream().map(WorkflowInfo::copy).collect(Collectors.toList());
    }

    @JsonIgnore
    public static Map<UUID, WorkflowInfo> copyAll(Map<UUID, WorkflowInfo> workflowList)
    {
        if ( workflowList == null ) {
            return null;
        }

        Map<UUID, WorkflowInfo> newMap = new LinkedHashMap<>();

        workflowList.entrySet()
                .stream()
                .forEach( e -> newMap.put( e.getKey(), WorkflowInfo.copy(e.getValue())));

        return newMap;
    }

    @JsonIgnore
    public static Map<UUID, WorkflowInfo> copyAllMapping(List<WorkflowInfo> workflowList)
    {
        if ( workflowList == null ) {
            return null;
        }

        Map<UUID, WorkflowInfo> newMap = new LinkedHashMap<>();

        workflowList.forEach( t -> newMap.put( t.getId(), WorkflowInfo.copy(t)));

        return newMap;
    }

    @JsonIgnore
    public static List<WorkflowInfo> copyAllToList(Map<UUID, WorkflowInfo> workflowList)
    {
        if ( workflowList == null ) {
            return null;
        }

        return workflowList.values().stream().map( WorkflowInfo::copy ).collect(Collectors.toList());
    }

    @JsonIgnore
    public String toJSON()
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        }
        catch( Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public static WorkflowInfo fromJSON(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, WorkflowInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
