package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class ComponentInfo extends BasicInfo implements Serializable
{
    @JsonIgnore
    private UUID pluginId;

    @JsonIgnore
    private PluginInfo pluginInfo;

    @JsonIgnore
    private boolean isPublic = false;

    @JsonIgnore
    public ComponentInfo()
    {
        super();
    }

    @JsonIgnore
    public ComponentInfo(final UUID id,
                    final String name,
                    final String description,
                    final boolean isPublic,
                    final UUID pluginId,
                    final boolean enabled,
                    final boolean deletable,
                    final boolean builtIn,
                    final String icon,
                    final CybertronRGBColor color)
    {
        this.id = id;
        this.name = name;
        this.description = description;

        this.isPublic = isPublic;

        this.pluginId = pluginId;
        this.enabled = enabled;
        this.deletable = deletable;
        this.builtIn = builtIn;
        this.icon = icon;
        this.color = color;
    }

    @JsonIgnore
    public ComponentInfo(   final UUID id,
                            final String name,
                            final String description,
                            final boolean isPublic,
                            final UUID pluginId,
                            final boolean enabled,
                            final boolean deletable,
                            final Profile profile,
                            final String icon,
                            final CybertronRGBColor color)
    {
        this.id = id;
        this.name = name;
        this.description = description;

        this.isPublic = isPublic;

        this.pluginId = pluginId;
        this.enabled = enabled;
        this.deletable = deletable;

        //this.parameters = ParameterX.copyAll(params);
        this.profile = Profile.copy(profile);
        this.builtIn = builtIn;
        this.icon = icon;
        this.color = color;
    }

    /**
     * JSON Fully-initialising value constructor
     */
    @JsonCreator
    public ComponentInfo(   @JsonProperty("id") final UUID id,
                            @JsonProperty("name") final String name,
                            @JsonProperty("description") final String description,
                            @JsonProperty("ispublic") final boolean isPublic,
                            @JsonProperty("pluginid") final UUID pluginId,
                            @JsonProperty("enabled") final boolean enabled,
                            @JsonProperty("deletable") final boolean deletable,
                            @JsonProperty("profile") final Profile profile,
                            @JsonProperty("plugininfo") final PluginInfo pluginInfo,
                            @JsonProperty("xcoord") final long xCoord,
                            @JsonProperty("ycoord") final long yCoord,
                            @JsonProperty("icon") final String icon,
                            @JsonProperty("color") final CybertronRGBColor color)
    {
        this.id = id;
        this.name = name;
        this.description = description;

        this.isPublic = isPublic;

        this.pluginId = pluginId;
        this.enabled = enabled;
        this.deletable = deletable;

        this.profile =  Profile.copy(profile);
        this.pluginInfo = PluginInfo.copy(pluginInfo);

        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.builtIn = builtIn;
        this.icon = icon;
        this.color = color;
    }

    public ComponentInfo(ComponentInfo componentInfo, PluginInfo pluginInfo)
    {
        if ( componentInfo == null ) {
            return;
        }

        this.id = componentInfo.id;
        this.name = componentInfo.name;
        this.description = componentInfo.description;

        this.isPublic = componentInfo.isPublic;

        this.pluginId = componentInfo.pluginId;
        this.enabled = componentInfo.enabled;
        this.deletable = componentInfo.deletable;

        super.setStatus(componentInfo.getStatus());
        super.setProgress(componentInfo.getProgress());

        this.xCoord = componentInfo.xCoord;
        this.yCoord = componentInfo.yCoord;

        this.inputPins = PinInfo.copyAll(componentInfo.inputPins);
        this.outputPins = PinInfo.copyAll(componentInfo.outputPins);

        this.profile = Profile.copy(componentInfo.profile);
        this.pluginInfo = PluginInfo.copy(pluginInfo);
        this.builtIn = componentInfo.builtIn;
        this.icon = componentInfo.icon;
        this.type = componentInfo.type;
        this.groupTypes = componentInfo.groupTypes;

        this.color = componentInfo.color;
    }

    public ComponentInfo(ComponentInfo componentInfo)
    {
        if ( componentInfo == null ) {
            return;
        }

        this.id = componentInfo.id;
        this.name = componentInfo.name;
        this.description = componentInfo.description;

        this.isPublic = componentInfo.isPublic;

        this.pluginId = componentInfo.pluginId;
        this.enabled = componentInfo.enabled;
        this.deletable = componentInfo.deletable;

        this.xCoord = componentInfo.xCoord;
        this.yCoord = componentInfo.yCoord;

        super.setStatus(componentInfo.getStatus());
        super.setProgress(componentInfo.getProgress());

        this.inputPins = PinInfo.copyAll(componentInfo.inputPins);
        this.outputPins = PinInfo.copyAll(componentInfo.outputPins);

        this.profile = Profile.copy(componentInfo.profile);
        this.pluginInfo = PluginInfo.copy(componentInfo.pluginInfo);
        this.builtIn = componentInfo.builtIn;
        this.icon = componentInfo.icon;
        this.type = componentInfo.type;
        this.groupTypes = componentInfo.groupTypes;

        this.color = componentInfo.color;
    }

    public ComponentInfo(UUID newComponentId, ComponentInfo componentInfo, long newXCoord, long newYCoord)
    {
        if ( componentInfo == null ) {
            return;
        }

        this.id = newComponentId;
        this.name = componentInfo.name;
        this.description = componentInfo.description;

        this.isPublic = componentInfo.isPublic;

        this.pluginId = componentInfo.pluginId;
        this.enabled = componentInfo.enabled;
        this.deletable = componentInfo.deletable;

        super.setStatus(componentInfo.getStatus());
        super.setProgress(componentInfo.getProgress());

        this.xCoord = newXCoord;
        this.yCoord = newYCoord;

        this.inputPins = PinInfo.copyAll(componentInfo.inputPins);
        this.outputPins = PinInfo.copyAll(componentInfo.outputPins);

        this.profile = Profile.copy(componentInfo.profile);
        this.pluginInfo = PluginInfo.copy(componentInfo.pluginInfo);
        this.builtIn = componentInfo.builtIn;
        this.icon = componentInfo.icon;
        this.type = componentInfo.type;
        this.groupTypes = componentInfo.groupTypes;

        this.color = componentInfo.color;
    }

    public ComponentInfo(UUID newComponentId, ComponentInfo componentInfo)
    {
        if ( componentInfo == null ) {
            return;
        }

        this.id = newComponentId;
        this.name = componentInfo.name;
        this.description = componentInfo.description;

        this.isPublic = componentInfo.isPublic;

        this.pluginId = componentInfo.pluginId;
        this.enabled = componentInfo.enabled;
        this.deletable = componentInfo.deletable;

        super.setStatus(componentInfo.getStatus());
        super.setProgress(componentInfo.getProgress());

        this.xCoord = componentInfo.xCoord;
        this.yCoord = componentInfo.yCoord;

        this.inputPins = PinInfo.copyAll(componentInfo.inputPins);
        this.outputPins = PinInfo.copyAll(componentInfo.outputPins);

        this.profile = Profile.copy(componentInfo.profile);
        this.pluginInfo = PluginInfo.copy(componentInfo.pluginInfo);
        this.builtIn = componentInfo.builtIn;
        this.icon = componentInfo.icon;
        this.type = componentInfo.type;
        this.groupTypes = componentInfo.groupTypes;

        this.color = componentInfo.color;
    }

    public ComponentInfo( PluginInfo pluginInfo ) {

        if ( pluginInfo == null ) {
            return;
        }

        this.id = pluginInfo.getId();
        this.name = pluginInfo.getName();
        this.description = pluginInfo.getDescription();

        this.pluginId = pluginInfo.getReferenceId();
        this.enabled = pluginInfo.getEnabled();

        super.setStatus(pluginInfo.getStatus());
        super.setProgress(pluginInfo.getProgress());

        this.xCoord = pluginInfo.getXCoord();
        this.yCoord = pluginInfo.getYCoord();

        this.inputPins = PinInfo.copyAll(pluginInfo.getInputPins());
        this.outputPins = PinInfo.copyAll(pluginInfo.getOutputPins());

        this.numberOfPins = pluginInfo.getNumberOfPins();

        this.profile = Profile.copy(pluginInfo.getProfile());

        //TODO: Is pluginInfo instance within componentInfo really necessary?
        //      What for?
        this.pluginInfo = null;

        this.deletable = pluginInfo.getDeletable();
        this.builtIn = pluginInfo.isBuiltIn();
        this.icon = pluginInfo.getIcon();
        this.type = pluginInfo.getType();
        this.groupTypes = pluginInfo.getGroupTypes();

        this.color = pluginInfo.getColor();
    }

    /**
     * Gets the value of the ownerId property.
     *
     * @return possible object is
     * {@link UUID }
     */
    @JsonProperty("ispublic")
    public boolean isPublic()
    {
        return this.isPublic;
    }

    /**
     * Sets the value of the ownerId property.
     *
     * @param value allowed object is
     *              {@link UUID }
     */
    public void setPublic(boolean value)
    {
        this.isPublic = value;
    }

    /**
     * Gets the value of the pluginId property.
     *
     * @return possible object is
     * {@link String }
     */
    @JsonProperty("pluginid")
    public UUID getPluginId()
    {
        return pluginId;
    }

    /**
     * Sets the value of the pluginId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPluginId(UUID value)
    {
        this.pluginId = value;
    }

    @JsonProperty("plugininfo")
    public PluginInfo getPluginInfo()
    {
        return this.pluginInfo;
    }

    public void setPluginInfo(PluginInfo value) {
        this.pluginInfo = PluginInfo.copy(value);
    }

    @Override
    public String toString()
    {
        return "ComponentInfo { id= " + id + ", name= " + name + ", description= " + description + ", enabled= " + enabled + ", numberOfPins= " + numberOfPins + ", inputPins= " + getInputPins()+ ", outputPins= " + getOutputPins() + /*", parameters= " + getParameters()  +*/ ", xCoord=" + this.xCoord + ", yCoord=" + this.yCoord + ", profile=" + profile + " }";
    }


    @JsonIgnore
    public ComponentInfo mergeProfile(Profile newProfile)
    {
        //Merges component profile along side plugin profile in the way that component profile settings
        //prevails over plugin profile configuration.
        //to do so, gets all the plugin profiles that don't occurs in component profile and inserts them.

        if ( newProfile == null ) {
            return this; //does not do anything.
        }

        this.setProfile(Profile.merge( this.getProfile(), newProfile ));

        return this;
    }

    @JsonIgnore
    public PinInfo findPin(PinDirection direction, UUID pinId)
    {
        try {
            Map<UUID, PinInfo> pinList = null;
            if (PinDirection.PINDIR_INPUT == direction) {
                pinList = this.inputPins;
            } else {
                pinList = this.outputPins;
            }

            PinInfo targetPin = pinList.entrySet()
                    .stream()
                    .filter(e -> e.getKey().equals(pinId))
                    .map(Map.Entry::getValue)
                    .findAny()
                    .orElse(null);

            return targetPin;
        }
        catch(Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public PinInfo findInputPin(UUID pinId)
    {
        try {
            return findPin(PinDirection.PINDIR_INPUT, pinId);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public PinInfo findOutputPin(UUID pinId)
    {
        try {

            return findPin(PinDirection.PINDIR_OUTPUT, pinId);
        }
        catch(Exception e)
        {
            return null;
        }
    }
    //------------------------------------------------------------------------
    @JsonIgnore
    public static ComponentInfo copy (ComponentInfo componentInfo)
    {
        return new ComponentInfo(componentInfo);
    }

    @JsonIgnore
    public static List<ComponentInfo> copyAll (List<ComponentInfo> componentList)
    {
        if( componentList == null ) {
            return null;
        }

        List<ComponentInfo> ret = new ArrayList<>();
        for (ComponentInfo component : componentList) {
            ret.add ( ComponentInfo.copy(component));
        }

        return ret;
    }

    @JsonIgnore
    public static Map<UUID, ComponentInfo> copyAllMapping ( List<ComponentInfo> componentList)
    {
        if ( componentList == null ) {
            return null;
        }

        return componentList.stream().collect(Collectors.toMap(ComponentInfo::getId, ComponentInfo::copy));

    }

    @JsonIgnore
    public static Map<UUID, ComponentInfo> copyAll (Map<UUID, ComponentInfo> componentList)
    {
        if( componentList == null ) {
            return null;
        }

        return componentList.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ComponentInfo.copy(entry.getValue())));

    }

    @JsonIgnore
    public static List<ComponentInfo> copyAllToList(Map<UUID, ComponentInfo> pluginList)
    {
        if ( pluginList == null ) {
            return null;
        }

        return pluginList.values().stream().map( ComponentInfo::copy ).collect(Collectors.toList());
    }

    @JsonIgnore
    public  String toJSON()
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        }
        catch( Exception e)
        {
            e.getStackTrace();
            return null;
        }
    }

    @JsonIgnore
    public static ComponentInfo fromJSON(String jsonString)
    {
        if ( jsonString == null || jsonString.isEmpty() ) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, ComponentInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /*@JsonIgnore
    @SuppressWarnings("unchecked")
    public static Map<UUID, ComponentInfo> fromJSONEx(String jsonString)
    {
        if ( jsonString == null || jsonString.isEmpty() ) {
            return null;
        }

        Map<UUID, ComponentInfo> list = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            list = mapper.readValue(jsonString, Map.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return list;
    }*/

    /*@JsonIgnore
    public static String toJSON(Map<UUID, ComponentInfo> list)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(list);
        }
        catch( Exception e)
        {
            return null;
        }
    }*/


    @JsonIgnore
    public static ComponentInfo mergeProfile(ComponentInfo componentInfo, Profile newProfile)
    {
        //Merges component profile along side plugin profile in the way that component profile settings
        //prevails over plugin profile configuration.
        //to do so, gets all the plugin profiles that don't occurs in component profile and inserts them.

        if ( componentInfo == null ) {
            return null; //Component is null, so returns null.
        }

        if ( newProfile == null ) {
            return componentInfo;
        }

        if ( componentInfo.getPluginInfo() == null ) {
            return componentInfo; //does not do anything.
        }

        componentInfo.setProfile(Profile.merge( componentInfo.getProfile(), newProfile ));

        return componentInfo;
    }


}
