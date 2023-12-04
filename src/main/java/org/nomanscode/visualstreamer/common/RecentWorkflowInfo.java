package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;
import java.util.UUID;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class RecentWorkflowInfo {

    @JsonIgnore
    private UUID id_;

    @JsonIgnore
    private String name_;

    @JsonIgnore
    private Date date_;

    @JsonIgnore
    private UUID userId_;

    @JsonIgnore
    private UUID workflowId_;

    @JsonIgnore
    private String groupName_;

    @JsonIgnore
    private UUID groupId_;

    @JsonIgnore
    public RecentWorkflowInfo(final RecentWorkflowInfo tr) {
        this.name_ = tr.name_;
        this.id_ = tr.id_;
        this.date_ = tr.date_;
        this.userId_ = tr.userId_;
        this.workflowId_ = tr.workflowId_;
        this.groupName_ = tr.groupName_;
        this.groupId_ = tr.groupId_;
    }

    @JsonCreator
    public RecentWorkflowInfo(@JsonProperty("id") final UUID id,
                              @JsonProperty("name") final String name,
                              @JsonProperty("date") final Date date,
                              @JsonProperty("workflowid") final UUID workflowId,
                              @JsonProperty("userid") final UUID userId,
                              @JsonProperty("groupname") final String groupName,
                              @JsonProperty("groupid") final UUID groupId) {
        this.name_ = name;
        this.id_ = id;
        this.date_ = date;
        this.userId_ = userId;
        this.workflowId_ = workflowId;
        this.groupName_ = groupName;
        this.groupId_ = groupId;
    }

    @JsonProperty("id")
    public UUID getId()
    {
        return this.id_;
    }

    public void setId(final UUID value) {
        this.id_ = value;
    }

    @JsonProperty("name")
    public String getName()
    {
        return this.name_;
    }

    public void setName(final String value) {
        this.name_ = value;
    }

    @JsonProperty("date")
    public Date getDate()
    {
        return this.date_;
    }

    public void setDate(final Date value) {
        this.date_ = value;
    }

    @JsonProperty("userid")
    public UUID getUserId()
    {
        return this.userId_;
    }

    public void setUserId(final UUID value) {
        this.userId_ = value;
    }

    @JsonProperty("workflowid")
    public UUID getWorkflowId()
    {
        return this.workflowId_;
    }

    public void setWorkflowId(final UUID value) {
        this.workflowId_ = value;
    }

    @JsonProperty("groupname")
    public String getGroupName()
    {
        return this.groupName_;
    }

    public void setGroupName(final String value) {
        this.groupName_ = value;
    }

    @JsonProperty("groupid")
    public UUID getGroupId()
    {
        return this.groupId_;
    }

    public void setGroupId(final UUID value) {
        this.groupId_ = value;
    }

    //-----------------------------------------------------------------

    @JsonIgnore
    public static RecentWorkflowInfo create(final UUID id, final String name, final Date date, final UUID workflowId, final UUID userId, final String groupName, final UUID groupId) {
        try {
            return new RecentWorkflowInfo(id, name, date, workflowId, userId, groupName, groupId);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static RecentWorkflowInfo create(final String name, final Date date, final UUID workflowId, final UUID userId, final String groupName, final UUID groupId) {
        try {
            UUID newId = UUID.randomUUID();
            return new RecentWorkflowInfo(newId, name, date, workflowId, userId, groupName, groupId);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static RecentWorkflowInfo create(final RecentWorkflowInfo recentWorkflow) {
        try {
            recentWorkflow.setId( UUID.randomUUID() );
            return new RecentWorkflowInfo(recentWorkflow);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static RecentWorkflowInfo copy(final RecentWorkflowInfo recentWorkflow) {
        try {
            return new RecentWorkflowInfo(recentWorkflow);
        }
        catch(Exception e) {
            return null;
        }
    }
}
