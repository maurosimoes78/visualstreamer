package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nomanscode.visualstreamer.common.*;

import java.io.Serializable;
import java.util.*;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class ComponentRequest implements Serializable {

    @JsonIgnore
    private String name_;

    @JsonIgnore
    private String description_;

    @JsonIgnore
    private UUID pluginId_;

    @JsonIgnore
    private boolean enabled_;

    @JsonIgnore
    private Profile profile_;

    @JsonIgnore
    private boolean isPublic_;

    @JsonIgnore
    private Set<UUID> viewerIds_;

    @JsonCreator
    public ComponentRequest (@JsonProperty("name") final String name,
                             @JsonProperty("description") final String description,
                             @JsonProperty("pluginid") final UUID pluginId,
                             @JsonProperty("enabled") final boolean enabled,
                             @JsonProperty("profile") final Profile profile,
                             @JsonProperty("ispublic") final boolean isPublic,
                             @JsonProperty("viewerids") final Set<UUID> viewerIds)
    {

        this.name_ = name;
        this.description_ = description;
        this.pluginId_ = pluginId;
        this.enabled_ = enabled;
        this.profile_ = profile;
        this.isPublic_ = isPublic;
        this.viewerIds_ = viewerIds;
    }

    @JsonIgnore
    public ComponentRequest(final PluginInfo info, final boolean isPublic, final Set<UUID> viewerIds) {
        this.name_ = info.getName();
        this.description_ = info.getDescription();
        this.enabled_ = true;
        this.pluginId_ = info.getId();
        this.isPublic_ = isPublic;
        this.viewerIds_ = viewerIds;
    }

    @JsonIgnore
    public ComponentRequest(final ComponentRequest tr)
    {
        this.name_ = tr.name_;
        this.description_ = tr.description_;
        this.pluginId_ = tr.pluginId_;
        this.enabled_ = tr.enabled_;
        this.profile_ = tr.profile_;
        this.isPublic_ = tr.isPublic_;
        this.viewerIds_ = tr.viewerIds_;
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

    @JsonProperty("pluginid")
    public UUID getPluginId()
    {
        return this.pluginId_;
    }

    public void setPluginId(final UUID value)
    {
        this.pluginId_ = value;
    }

    @JsonProperty("enabled")
    public boolean getEnabled() {
        return this.enabled_;
    }

    public void setEnabled(final boolean value)
    {
        this.enabled_ = value;
    }

    @JsonProperty("profile")
    public Profile getProfile() {
        return this.profile_;
    }

    public void setEnabled(final Profile value)
    {
        this.profile_ = value;
    }

    @JsonProperty("ispublic")
    public boolean isPublic()
    {
        return this.isPublic_;
    }

    public void setPublic(final boolean value)
    {
        this.isPublic_ = value;
    }

    @JsonProperty("viewerids")
    public Set<UUID> getViewerIds()
    {
        return this.viewerIds_;
    }

    public void setViewerIds(final Set<UUID> value)
    {
        this.viewerIds_ = value;
    }

    @JsonIgnore
    @Override
    public String toString()
    {
        return "ComponentRequest= {name=" + this.name_ + ", description=" + this.description_  + ", pluginId=" + this.pluginId_ + ", enabled="+ enabled_ + "}";
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