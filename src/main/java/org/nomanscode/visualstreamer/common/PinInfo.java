package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class PinInfo implements IPinInfo {

    @JsonIgnore
    private String name_;
    @JsonIgnore
    private PinDirection pindir_;
    @JsonIgnore
    private boolean connectionRequired_;
    @JsonIgnore
    private UUID connectedToId_ = null;
    @JsonIgnore
    private UUID uuid_;
    @JsonIgnore
    private List<PinType> defaultPinTypes_;
    @JsonIgnore
    private List<PinType> pinTypes_;
    @JsonIgnore
    private UUID equivalentUUID_;
    @JsonIgnore
    private int sequencial_;

    @JsonCreator
    public PinInfo(@JsonProperty("pindir") final PinDirection pindir,
                   @JsonProperty("uuid") final UUID uuid,
                   @JsonProperty("name") final String name,
                   @JsonProperty("connectionrequired") final boolean connectionRequired,
                   @JsonProperty("defaulttype") final List<PinType> defaultPinTypes,
                   @JsonProperty("type") final List<PinType> pinTypes,
                   @JsonProperty("equivalentuuid") final UUID equivalentUUID,
                   @JsonProperty("sequencial") final int sequencial,
                   @JsonProperty("connectedtoid") final UUID connectedToId) {
        this.name_ = name;

        this.defaultPinTypes_ = new ArrayList<>();

        if (defaultPinTypes == null || defaultPinTypes.size() == 0) {
            this.defaultPinTypes_.add(PinType.PIN_TYPE_ANY);
        } else {
            this.defaultPinTypes_ = PinType.copyAll(defaultPinTypes);
        }

        if (pinTypes == null || pinTypes.size() == 0) {
            this.pinTypes_ = PinType.copyAll(this.defaultPinTypes_);
        } else {
            this.pinTypes_ = PinType.copyAll(pinTypes);
        }

        this.connectedToId_ = connectedToId;
        this.pindir_ = pindir;

        if (this.pindir_ == PinDirection.PINDIR_INPUT) {
            this.connectionRequired_ = connectionRequired;
        }

        this.uuid_ = uuid;

        this.equivalentUUID_ = equivalentUUID;
        this.sequencial_ = sequencial;
    }

    public PinInfo(final PinInfo pin) {
        this.name_ = pin.name_;
        this.pinTypes_ = pin.pinTypes_;
        this.defaultPinTypes_ = pin.defaultPinTypes_;
        this.connectedToId_ = pin.connectedToId_;
        this.pindir_ = pin.pindir_;
        this.connectionRequired_ = pin.connectionRequired_;
        this.uuid_ = pin.uuid_;
        this.equivalentUUID_ = pin.equivalentUUID_;
        this.sequencial_ = pin.sequencial_;
    }


    @JsonProperty("uuid")
    public UUID getUUID() {
        return this.uuid_;
    }


    @JsonProperty("defaulttype")
    public List<PinType> getDefaultPinTypes() {
        if (this.defaultPinTypes_ == null) {
            this.defaultPinTypes_ = new ArrayList<>();
            this.defaultPinTypes_.add(PinType.PIN_TYPE_DONT_KNOW);
        }

        return this.pinTypes_;
    }

    @JsonProperty("type")
    public List<PinType> getPinTypes() {
        if (this.pinTypes_ == null) {
            return this.getDefaultPinTypes();
        }

        return this.pinTypes_;
    }

    @JsonIgnore
    public void setPinTypes(List<PinType> value)
    {
        this.pinTypes_ = PinType.copyAll(value);
    }

    @JsonIgnore
    public void resetPinTypes()
    {
        this.pinTypes_ = PinType.copyAll(this.defaultPinTypes_);
    }

    @JsonProperty("pindir")
    public PinDirection getPinDirection()
    {
        return this.pindir_;
    }

    @JsonProperty("connectedtoid")
    public UUID getConnectedToId()
    {
        return this.connectedToId_;
    }

    @JsonIgnore
    public void setConnectedToId(UUID value)
    {
        this.connectedToId_ = value;
    }

    @JsonProperty("name")
    public String getName()
    {
        return this.name_;
    }


    @JsonProperty("connectionrequired")
    public boolean getConnectionRequired()
    {
        return this.connectionRequired_;
    }

    @JsonProperty("equivalentuuid")
    public UUID getEquivalentUUID()
    {
        return this.equivalentUUID_;
    }

    @JsonProperty("sequencial")
    public int getSequencial()
    {
        return this.sequencial_;
    }

    @JsonProperty("sequencialname")
    public String getSequencialName()
    {
        return this.name_ + " (" + this.sequencial_ + ")";
    }

    @JsonIgnore
    public boolean isChildren() {
        return (this.sequencial_ > 0);
    }

    //-----------------------------------------------------------------------------

    @JsonIgnore
    public boolean isConnected()
    {
        return ( this.connectedToId_ != null );
    }

    //-------------------------------------------------------------------------
    @JsonIgnore
    public static PinInfo copy(PinInfo pin)
    {
        if ( pin == null ) {
            return null;
        }

        return new PinInfo(pin);
    }

    @JsonIgnore
    public static Map<UUID, PinInfo> copyAll(Map<UUID,PinInfo> pins)
    {
        if ( pins == null ) {
            return null;
        }

        Map<UUID, PinInfo> ret = new LinkedHashMap<>();
        pins.forEach( (key, value) -> ret.put(key, PinInfo.copy(value)));
        return ret;
    }
}
