package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import org.nomanscode.visualstreamer.common.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.nomanscode.visualstreamer.common.PluginType.PLUGIN_TYPE_UNKNOWN;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public abstract class BasicInfo extends MinimalInfo
{

    @JsonIgnore
    protected UUID id;

    @JsonIgnore
    protected boolean enabled = false;

    @JsonIgnore
    protected boolean deletable = true;

    @JsonIgnore
    protected boolean builtIn = false;

    @JsonIgnore
    protected Integer numberOfPins = 0;

    @JsonIgnore
    protected Map<UUID, PinInfo> inputPins = new LinkedHashMap<>();

    @JsonIgnore
    protected Map<UUID, PinInfo> outputPins = new LinkedHashMap<>();

    @JsonIgnore
    protected Profile profile;

    @JsonIgnore
    protected long xCoord = 0;

    @JsonIgnore
    protected long yCoord = 0;

    @JsonIgnore
    protected String icon = "";

    @JsonIgnore
    protected CybertronRGBColor color = CybertronRGBColor.GRAY();

    @JsonIgnore
    protected ComponentStatus status = ComponentStatus.TASK_UNKNOW;

    @JsonIgnore
    protected Integer progress = 0;

    @JsonIgnore
    protected PluginType type = PLUGIN_TYPE_UNKNOWN;

    @JsonIgnore
    protected List<PluginGroupType> groupTypes = new ArrayList<>();

    public BasicInfo () {
        this.groupTypes.add(PluginGroupType.PLUGIN_GROUP_TYPE_GENERAL);

    }
    /**
     * Gets the value of the id property.
     *
     * @return possible object is
     * {@link String }
     */
    @JsonProperty("id")
    public UUID getId()
    {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setId(UUID value)
    {
        this.id = value;
    }

    /**
     * Gets the value of the enabled property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    @JsonProperty("enabled")
    public boolean getEnabled()
    {
        return this.enabled;
    }

    /**
     * Sets the value of the enabled property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setEnabled(boolean value)
    {
        this.enabled = value;
    }

    /**
     * Gets the value of the deletable property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    @JsonProperty("deletable")
    public boolean getDeletable()
    {
        return this.deletable;
    }

    /**
     * Sets the value of the deletable property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setDeletable(boolean value)
    {
        this.deletable = value;
    }

    /**
     * Gets the value of the deletable property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    @JsonProperty("builtin")
    public boolean isBuiltIn()
    {
        return this.builtIn;
    }

    /**
     * Sets the value of the deletable property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setBuiltIn(boolean value)
    {
        this.builtIn = value;
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
     * Gets the value of the inputPinIds property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inputPinIds property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInputPinIds().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Map<UUID, PinInfo> }
     */
    @JsonProperty("inputpins")
    public Map<UUID, PinInfo> getInputPins()
    {
        if (this.inputPins == null) {
            this.inputPins = new LinkedHashMap<>();
        }
        return this.inputPins;
    }

    public void setInputPins(Map<UUID, PinInfo> value)
    {
        this.inputPins = value;
    }

    /**
     * Gets the value of the outputPinIds property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outputPinIds property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutputPinIds().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Map<UUID, PinInfo> }
     */
    @JsonProperty("outputpins")
    public Map<UUID, PinInfo> getOutputPins()
    {
        if (this.outputPins == null) {
            this.outputPins = new LinkedHashMap<>();
        }
        return this.outputPins;
    }

    public void setOutputPins(Map<UUID, PinInfo> value)
    {
        this.outputPins = value;
    }

    @JsonProperty("profile")
    public Profile getProfile()
    {
        return this.profile;
    }

    public void setProfile(Profile profile)
    {
        this.profile = Profile.copy(profile);
    }

    @JsonProperty("xcoord")
    public long getXCoord() {
        return this.xCoord;
    }

    public void setXCoord(long value) {
        this.xCoord = value;
    }

    @JsonProperty("ycoord")
    public long getYCoord() {
        return this.yCoord;
    }

    public void setYCoord(long value) {
        this.yCoord = value;
    }

    @JsonProperty("status")
    public ComponentStatus getStatus() {
        return this.status;
    }

    public void setStatus(ComponentStatus value) {
        this.status = value;
    }

    @JsonProperty("progress")
    public Integer getProgress() {
        return this.progress;
    }

    public void setProgress(Integer value) {
        this.progress = value;
    }

    @JsonProperty("icon")
    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String value) {
        this.icon = value;
    }


    /**
     * Gets the value of the groupType property.
     *
     * @return possible object is
     * {@link PluginGroupType }
     */
    @JsonProperty("types")
    public List<PluginGroupType> getGroupTypes()
    {
        return this.groupTypes;
    }

    /**
     * Sets the value of the groupTypes property.
     *
     * @param value allowed object is
     *              {@link PluginGroupType }
     */
    public void setGroupTypes(List<PluginGroupType> value)
    {
        this.addGroupTypes(value);
    }

    public List<PluginGroupType> addGroupTypes(List<PluginGroupType> list) {

        if ( list == null ) {
            return this.groupTypes;
        }

        List<PluginGroupType> groupTypes = new ArrayList<>();
        groupTypes.add(PluginGroupType.PLUGIN_GROUP_TYPE_GENERAL);

        //We just add new and not GENERAL types to the main list.
        list.stream().forEach( group -> {

            if ( this.groupTypes.contains(group) || group.equals(PluginGroupType.PLUGIN_GROUP_TYPE_GENERAL)) {
                return;
            }

            groupTypes.add(group);

        });

        this.groupTypes.clear();
        this.groupTypes.addAll(groupTypes);

        return this.groupTypes;
    }


    /**
     * Gets the value of the type property.
     *
     * @return possible object is
     * {@link PluginType }
     */
    @JsonProperty("type")
    public PluginType getType()
    {
        return this.type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value allowed object is
     *              {@link PluginType }
     */
    public void setType(PluginType value)
    {
        this.type = value;
    }
}
