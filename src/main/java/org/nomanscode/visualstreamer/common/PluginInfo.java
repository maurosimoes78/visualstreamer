package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nomanscode.visualstreamer.common.*;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.nomanscode.visualstreamer.common.PluginType.PLUGIN_TYPE_UNKNOWN;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class PluginInfo extends BasicInfo implements Serializable
{

    @JsonIgnore
    private String pluginName = "";

    @JsonIgnore
    private String vendor = "";

    @JsonIgnore
    private String version = "";

    @JsonIgnore
    private UUID productIdentificationId;

    @JsonIgnore
    private String path = "";

    @JsonIgnore
    private UUID referenceId;       //Used to hold a reference of the original plugin id as defined for the called.

    @JsonIgnore
    protected long width = 180;

    @JsonIgnore
    protected long height = 100;

    /**
     * Default no-arg constructor
     */
    public PluginInfo()
    {
        super();
    }

    /**
     * Used in database repository
     */
    public PluginInfo(final UUID id,
                        final String name,
                        final String description,
                        final String path,
                        final boolean enabled,
                        final boolean deletable,
                        final boolean builtIn,
                        final String icon,
                        final CybertronRGBColor color)
    {
        super();
        this.type = PLUGIN_TYPE_UNKNOWN;
        this.id = id;
        this.name = name;
        this.productIdentificationId = null;
        this.description = description;
        this.path = path;
        this.enabled = enabled;
        this.deletable = deletable;
        this.vendor = "";
        this.version = "";
        this.pluginName = "";
        this.numberOfPins = 0;
        this.inputPins = null;
        this.outputPins = null;
        //this.parameters = null;
        this.profile = null;
        this.builtIn = builtIn;
        this.icon = icon;
        this.color = color;
    }

    /**
     * JSON Fully-initialising value constructor
     */
    @JsonCreator
    public PluginInfo(@JsonProperty("id") final UUID id,
                        @JsonProperty("name") final String name,
                        @JsonProperty("description") final String description,
                        @JsonProperty("vendor") final String vendor,
                        @JsonProperty("version") final String  version,
                        @JsonProperty("pluginname") final String pluginName,
                        @JsonProperty("path") final String path,
                        @JsonProperty("enabled") final boolean enabled,
                        @JsonProperty("deletable") final boolean deletable,
                        @JsonProperty("builtin") final boolean builtIn,
                        @JsonProperty("icon") final String icon,
                        @JsonProperty("color") final CybertronRGBColor color)
    {
        super();
        this.type = PLUGIN_TYPE_UNKNOWN;
        this.id = id;
        this.name = name;
        this.productIdentificationId = null;
        this.description = description;
        this.path = path;
        this.deletable = deletable;
        this.enabled = enabled;
        this.vendor = vendor;
        this.version = version;
        this.pluginName = pluginName;
        this.numberOfPins = 0;
        this.inputPins = null;
        this.outputPins = null;
        //this.parameters = null;
        this.profile = null;

        this.builtIn = builtIn;
        this.icon = icon;
        this.color = color;
    }

    /**
     * Fully-initialising value constructor
     */
    public PluginInfo(PluginInfo plugin)
    {
        super();

        if ( plugin == null ) {
            return;
        }

        this.type = plugin.type;
        this.groupTypes = plugin.groupTypes;

        this.id = plugin.id;
        this.productIdentificationId = plugin.productIdentificationId;
        this.name = plugin.name;
        this.description = plugin.description;
        this.vendor = plugin.vendor;
        this.pluginName = plugin.pluginName;
        this.version = plugin.version;
        this.path = plugin.path;
        this.enabled = plugin.enabled;
        this.deletable = plugin.deletable;
        this.numberOfPins = plugin.numberOfPins;

        super.setStatus(plugin.getStatus());
        super.setProgress(plugin.getProgress());

        this.inputPins = PinInfo.copyAll(plugin.inputPins);
        this.outputPins = PinInfo.copyAll(plugin.outputPins);

       // this.parameters = ParameterX.copyAll(plugin.parameters);
        this.xCoord = plugin.getXCoord();
        this.yCoord = plugin.getYCoord();

        this.profile = Profile.copy(plugin.profile);

        this.builtIn = plugin.builtIn;
        this.icon = plugin.icon;
        this.color = plugin.color;
    }

    /**
     * Gets the value of the vendor property.
     *
     * @return possible object is
     * {@link String }
     */
    @JsonProperty("vendor")
    public String getVendor()
    {
        return vendor;
    }

    /**
     * Sets the value of the vendor property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVendor(String value)
    {
        this.vendor = value;
    }

    /**
     * Gets the value of the vendor property.
     *
     * @return possible object is
     * {@link String }
     */
    @JsonProperty("pluginname")
    public String getPluginName()
    {
        return pluginName;
    }

    /**
     * Sets the value of the vendor property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPluginName(String value)
    {
        this.pluginName = value;
    }

    /**
     * Gets the value of the icon.
     *
     * @return possible object is
     * {@link String }
     */
    @JsonProperty("icon")
    public String getIcon()
    {
        return this.icon;
    }

    /**
     * Sets the value of the icon.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setIcon(String value)
    {
        this.icon = value;
    }

    /**
     * Gets the value of the icon.
     *
     * @return possible object is
     * {@link String }
     */
    @JsonProperty("color")
    public CybertronRGBColor getColor()
    {
        return this.color;
    }

    /**
     * Sets the value of the icon.
     *
     * @param value allowed object is
     *              {@link Color}
     */
    public void setColor(CybertronRGBColor value)
    {
        this.color = value;
    }


    /**
     * Gets the value of the version property.
     *
     * @return possible object is
     * {@link String }
     */
    @JsonProperty("version")
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVersion(String value)
    {
        this.version = value;
    }


    /**
     * Gets the value of the productIdentificationId property.
     *
     * @return possible object is
     * {@link UUID }
     */
    @JsonProperty("productidentificationid")
    public UUID getProductIdentificationId()
    {
        return productIdentificationId;
    }

    /**
     * Sets the value of the productIdentificationId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setProductIdentificationId(UUID value)
    {
        this.productIdentificationId = value;
    }

    /**
     * Gets the value of the path property.
     *
     * @return possible object is
     * {@link String }
     */
    @JsonProperty("path")
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the value of the path property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPath(String value)
    {
        this.path = value;
    }

    /**
     * Gets the value of the numberOfPins property.
     *
     * @return possible object is
     * {@link Integer }
     */
    @JsonProperty("numberofpins")
    public Integer getNumberOfPins()
    {
        return this.numberOfPins;
    }

    /**
     * Sets the value of the numberOfPins property.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setNumberOfPins(Integer value)
    {
        this.numberOfPins = value;
    }

    /**
     * Gets the value of the parameters property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameters property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameters().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link List<  Parameter  > }
     */
    /*@JsonProperty("parameters")
    public List<ParameterX> getParameters()
    {
        if (this.parameters == null) {
            this.parameters = new ArrayList<>();
        }
        return this.parameters;
    }*/

    /**
     * Gets the value of the referenced plugin id property.
     *
     * @return possible object is
     * {@link UUID }
     */
    public UUID getReferenceId() {
        return this.referenceId;
    }

    /**
     * Sets the value of the referenced plugin id property.
     *
     * @param value allowed object is
     *              {@link UUID }
     */
    public void setReferenceId(UUID value) {
        this.referenceId = value;
    }

    @JsonProperty("width")
    public long getWidth() {
        return this.width;
    }

    public void setWidth(long value) {
        this.width = value;
    }

    @JsonProperty("height")
    public long getHeight() {
        return this.height;
    }

    public void setHeight(long value) {
        this.height = value;
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

    // --------------------------------------------
    
    @Override
    public String toString()
    {
        return "Plugin { id=" + id + ", name=" + name + ", vendor= " + vendor + ", version= " + version + ", productIdentificationId= " + productIdentificationId + ", description=" + description + ", path=" + path + ", enabled=" + enabled + ", numberOfPins= " + numberOfPins + ", inputPins= " + getInputPins()+ ", outputPins= " + getOutputPins() /*+ ", parameters= " + getParameters()*/ /*+ ", profile= " + getProfile().toJSON()*/ + ", referencedId=" + this.referenceId + " }";
    }

    @JsonIgnore
    public static PluginInfo copy (PluginInfo pluginInfo)
    {
        if ( pluginInfo == null ) {
            return null;
        }

        return new PluginInfo(pluginInfo);
    }

    @JsonIgnore
    public static Map<UUID, PluginInfo> copyAll(Map<UUID, PluginInfo> pluginList)
    {
        if ( pluginList == null ) {
            return null;
        }

        Map<UUID, PluginInfo> newMap = new LinkedHashMap<>();

        pluginList.values()
                .stream()
                .forEach( t -> newMap.put( t.getId(), PluginInfo.copy(t)));

        return newMap;
    }

    @JsonIgnore
    public static List<PluginInfo> copyAllToList(Map<UUID, PluginInfo> pluginList)
    {
        if ( pluginList == null ) {
            return null;
        }

        return pluginList.values().stream().map( PluginInfo::copy ).collect(Collectors.toList());
    }

}
