package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import org.nomanscode.visualstreamer.common.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class ComponentItemInfo {

    @JsonIgnore
    private UUID id = UUID.randomUUID();

    @JsonIgnore
    private String name;

    @JsonIgnore
    private String description;

    @JsonIgnore
    private Map<UUID, ComponentInfo> components = new LinkedHashMap<>();

    @JsonIgnore
    public ComponentItemInfo(final String name) {
        this(name, "", null);
    }

    public ComponentItemInfo(final String name, final String description) {
        this(name, description, null);
    }

    @JsonCreator
    public ComponentItemInfo(@JsonProperty("id") final UUID id,
                             @JsonProperty("name") final String name,
                             @JsonProperty("description") final String description,
                             @JsonProperty("components") final Map<UUID, ComponentInfo> components) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.components = components == null ? this.components : ComponentInfo.copyAll(components);
    }

    public ComponentItemInfo(final String name, final String description, final Map<UUID, ComponentInfo> components) {
        this.name = name;
        this.description = description;
        this.components = components == null ? this.components : ComponentInfo.copyAll(components);
    }

    @JsonProperty("id")
    public UUID getId() {
        return this.id;
    }

    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return this.description;

    }

    @JsonProperty("components")
    public Map<UUID, ComponentInfo> getComponents() {
        return this.components;
    }

    /*@JsonIgnore
    private void setType() {

        if ( this.component == null ||
             this.component.getPluginInfo() == null ||
             this.component.getPluginInfo().getType() == null ) {
            this.typeName = "Generic Components";
            return;
        }

        this.typeName = this.component.getPluginInfo().getType() != PluginType.PLUGIN_TYPE_TASK ?
                        "Built-In Components" :
                        this.component.getPluginInfo().getType().getFriendlyName();

    }*/

    //------------------------------------------------------------------------
    @JsonIgnore
    public static ComponentItemInfo copy (ComponentItemInfo info)
    {
        return new ComponentItemInfo( info.id, info.name, info.description, ComponentInfo.copyAll(info.components));
    }

    @JsonIgnore
    public static List<ComponentItemInfo> copyAllItemsToList(Map<UUID, ComponentItemInfo> pluginList)
    {
        if ( pluginList == null ) {
            return null;
        }

        return pluginList.values().stream().map( ComponentItemInfo::copy ).collect(Collectors.toList());
    }

}
