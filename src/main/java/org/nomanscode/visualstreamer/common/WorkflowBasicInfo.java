package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class WorkflowBasicInfo {

    @JsonIgnore
    private UUID id;

    @JsonIgnore
    private String name;

    @JsonIgnore
    private String description;

    @JsonIgnore
    private boolean published;

    @JsonIgnore
    private UUID lockedById = null;

    @JsonIgnore
    private Date created = new Date();

    @JsonIgnore
    private Date modified = new Date(); //RUF Armazenar data de modificacao do Workflow

    @JsonIgnore
    private boolean requireSave = false;

    public WorkflowBasicInfo() {

    }
    @JsonCreator
    public WorkflowBasicInfo( @JsonProperty("id") final UUID id,
                              @JsonProperty("name") final String name,
                              @JsonProperty("description") final String description,
                              @JsonProperty("published") final boolean published,
                              @JsonProperty("created") final Date created,
                              @JsonProperty("modified") final Date modified) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.published = published;
        this.created = created;
        this.modified = modified;

    }
    // ---------------------------------------------------------------------------------------

    public WorkflowBasicInfo(WorkflowInfo info) {
        this.id = info.getId();
        this.name = info.getName();
        this.description = info.getDescription();
        this.published = info.isPublished();
        this.created = info.getCreated();
        this.modified = info.getModified();
        this.requireSave = info.getRequireSave();
    }

    @JsonProperty("id")
    public UUID getId() {
        return this.id;
    }

    public void setId(UUID value) {
        this.id = value;
    }

    @JsonProperty("name")
    public String getName() {
        if ( this.name == null ) {
            this.name = "";
        }

        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @JsonProperty("description")
    public String getDescription() {
        if ( this.description == null ) {
            this.description = "";
        }

        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    @JsonProperty("published")
    public boolean isPublished()
    {
        return published;
    }

    public void setPublished(boolean value)
    {
        this.published = value;
    }

    @JsonProperty("created")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date getCreated()
    {
        return this.created;
    }

    public void setCreated(final Date value)
    {
        this.created = value;
    }

    @JsonProperty("modified")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date getModified()
    {
        return this.modified;
    }

    public void setModified(final Date value)
    {
        this.modified = value;
    }

    @JsonProperty("requiresave")
    public boolean getRequireSave()
    {
        return this.requireSave;
    }

    public void setRequireSave(boolean value)
    {
        this.requireSave = value;
    }

}
