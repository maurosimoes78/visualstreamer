package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.UUID;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class WorkflowRequest implements Serializable {

    @JsonIgnore
    private UUID id_;

    @JsonIgnore
    private String name_;

    @JsonIgnore
    private String description_;

    @JsonIgnore
    private String keywords_;

    @JsonIgnore
    private UUID groupId_;

    @JsonIgnore
    private boolean regen_;


    @JsonCreator
    public WorkflowRequest (@JsonProperty("name") final String name,
                            @JsonProperty("description") final String description,
                            @JsonProperty("keywords") final String keywords,
                            @JsonProperty("groupid") final UUID groupId,
                            @JsonProperty("id") final UUID id,
                            @JsonProperty("regen") final boolean regen)
    {

        this.id_ = id;
        this.name_ = name;
        this.description_ = description;
        this.keywords_ = keywords;
        this.groupId_ = groupId;
        this.regen_ = regen;

    }

    public WorkflowRequest(final WorkflowRequest wr)
    {

        this.id_ = wr.id_;
        this.name_ = wr.name_;
        this.description_ = wr.description_;
        this.keywords_ = wr.keywords_;
        this.groupId_ = wr.groupId_;
        this.regen_ = wr.regen_;
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

    @JsonProperty("description")
    public String getDescription()
    {
        return this.description_;
    }

    public void setDescription(final String value)
    {
        this.description_ = value;
    }

    @JsonProperty("keywords")
    public String getKeywords()
    {
        return this.keywords_;
    }

    public void setKeywords(final String value)
    {
        this.keywords_ = value;
    }

    @JsonProperty("groupid")
    public UUID getGroupId()
    {
        return this.groupId_;
    }

    public void setGroupId(final UUID value)
    {
        this.groupId_ = value;
    }

    @JsonProperty("regen")
    public boolean getRegen()
    {
        return this.regen_;
    }

    public void setRegen(final boolean value)
    {
        this.regen_ = value;
    }

    @JsonIgnore
    @Override
    public String toString()
    {
        return "WorkflowRequest {name=" + this.name_ + ", description=" + this.description_ + "}";
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
}