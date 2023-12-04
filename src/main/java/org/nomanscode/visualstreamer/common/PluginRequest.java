package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class PluginRequest implements Serializable {

    @JsonIgnore
    String name;

    @JsonIgnore
    String description;

    @JsonIgnore
    String path;

    @JsonIgnore
    boolean enabled;

    @JsonIgnore
    private UUID ownerId;

    @JsonIgnore
    private boolean isPublic;

    @JsonIgnore
    private Set<UUID> viewerIds;

    @JsonCreator
    public PluginRequest (@JsonProperty("name") final String name,
                          @JsonProperty("description") final String description,
                          @JsonProperty("path") final String path,
                          @JsonProperty("enabled") final boolean enabled,
                          @JsonProperty("ownerid") final UUID ownerId,
                          @JsonProperty("ispublic") final boolean isPublic,
                          @JsonProperty("viewerids") final Set<UUID> viewerIds) {

        this.name = name;
        this.description = description;
        this.path = path;
        this.enabled = enabled;
        this.isPublic = isPublic;
        this.ownerId = ownerId;
        this.viewerIds = viewerIds;
    }

    public PluginRequest(final PluginRequest p) {
        this.name = p.name;
        this.description = p.description;
        this.path = p.path;
        this.enabled = p.enabled;
        this.ownerId = p.ownerId;
        this.isPublic = p.isPublic;
        this.viewerIds = p.viewerIds;
    }

    @JsonProperty
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @JsonProperty
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    @JsonProperty
    public String getPath() {
        return this.path;
    }

    public void setPath(String value) {
        this.path = value;
    }

    @JsonProperty
    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    @JsonProperty("ownerid")
    public UUID getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(UUID value) {
        this.ownerId = value;
    }

    @JsonProperty("ispublic")
    public boolean isPublic() {
        return this.isPublic;
    }

    public void setPublic(boolean value) {
        this.isPublic = value;
    }

    @JsonProperty("viewerids")
    public Set<UUID> getViewerIds() {
        return this.viewerIds;
    }

    public void setViewerIds(Set<UUID> value) {
        this.viewerIds = value;
    }

    @Override
    public String toString() {
        return "PluginRequest= {name= '" + name + "', description= '" + description + "', path='" + path + "', enabled=" + enabled + "}";
    }

}
