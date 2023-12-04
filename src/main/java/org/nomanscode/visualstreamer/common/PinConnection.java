package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nomanscode.visualstreamer.common.interfaces.IPin;
import org.nomanscode.visualstreamer.common.*;

import java.io.IOException;
import java.util.*;

import static java.lang.System.currentTimeMillis;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class PinConnection {

    @JsonIgnore
    protected UUID connectionId;
    @JsonIgnore
    protected UUID sourceComponentId;
    @JsonIgnore
    protected UUID sourcePinId;
    @JsonIgnore
    protected UUID targetComponentId;
    @JsonIgnore
    protected UUID targetPinId;
    @JsonIgnore
    private Long sequence;

    @JsonCreator
    public PinConnection(@JsonProperty("connectionid") final UUID connectionId,
                         @JsonProperty("sourcecomponentid") final UUID sourceComponentId,
                         @JsonProperty("sourcepinid") final UUID sourcePinId,
                         @JsonProperty("targetcomponentid") final UUID targetComponentId,
                         @JsonProperty("targetpinid") final UUID targetPinId)
    {
        this.connectionId = connectionId;
        this.sourceComponentId = sourceComponentId;
        this.sourcePinId = sourcePinId;
        this.targetComponentId = targetComponentId;
        this.targetPinId = targetPinId;
        this.sequence = currentTimeMillis();
    }

    @JsonIgnore
    public PinConnection(PinConnection c)
    {
        this.connectionId = c.connectionId;
        this.sourceComponentId = c.sourceComponentId;
        this.sourcePinId = c.sourcePinId;
        this.targetComponentId = c.targetComponentId;
        this.targetPinId = c.targetPinId;
        this.sequence = c.sequence;
    }

    @JsonProperty("connectionid")
    public UUID getConnectionId()
    {
        return this.connectionId;
    }

    public void setConnectionId(UUID value)
    {
        this.connectionId = value;
    }

    @JsonProperty("sourcecomponentid")
    public UUID getSourceComponentId()
    {
        return this.sourceComponentId;
    }

    public void setSourceComponentId(UUID value)
    {
        this.sourceComponentId = value;
    }

    @JsonProperty("sourcepinid")
    public UUID getSourcePinId()
    {
        return this.sourcePinId;
    }

    public void setSourcePinId(UUID value)
    {
        this.sourcePinId = value;
    }

    @JsonProperty("targetcomponentid")
    public UUID getTargetComponentId()
    {
        return this.targetComponentId;
    }

    public void setTargetComponentId(UUID value)
    {
        this.targetComponentId = value;
    }

    @JsonProperty("targetpinid")
    public UUID getTargetPinId()
    {
        return this.targetPinId;
    }

    public void setTargetPinId(UUID value)
    {
        this.targetPinId = value;
    }

    @JsonProperty("sequence")
    public Long getSequence() {
        return this.sequence;
    }

    //----------------------------------------------------------------

    public static PinConnection copy ( PinConnection conn )
    {
        if ( conn == null ) {
            return null;
        }

        return new PinConnection(conn);
    }

    public static Map<UUID, PinConnection> copyAllMapping (List<PinConnection> connList)
    {
        if( connList == null ) {
            return null;
        }

        Map<UUID, PinConnection> ret = new LinkedHashMap<>();

        connList.forEach( c -> ret.put( c.getConnectionId(), PinConnection.copy(c)));

        return ret;
    }

    public static Map<UUID, PinConnection> copyAll (Map<UUID, PinConnection> connList)
    {
        if( connList == null ) {
            return null;
        }

        Map<UUID, PinConnection> ret = new LinkedHashMap<>();

        connList.entrySet().forEach( e -> ret.put( e.getKey(), PinConnection.copy(e.getValue())));

        return ret;
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public static Map<UUID, PinConnection> fromJSON(String jsonString)
    {
        if ( jsonString == null || jsonString.isEmpty() ) {
            return null;
        }

        Map<UUID, PinConnection> list = null;

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
    }

    @JsonIgnore
    public static PinConnection create(UUID sourceComponentId, UUID sourcePinId, UUID targetComponentId, UUID targetPinId)
    {
        try {
            return new PinConnection(sourceComponentId, sourcePinId, targetComponentId, targetPinId, null);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static PinConnection create(IPin sourcePin, IPin targetPin)
    {
        return create(sourcePin, targetPin, null);
    }

    @JsonIgnore
    public static PinConnection create(IPin sourcePin, IPin targetPin, UUID connectionId)
    {
        try {

            UUID pinId = sourcePin.getUUID();
            if ( sourcePin.getEquivalentUUID() != null ) {
                pinId = sourcePin.getEquivalentUUID();
            }

            return new PinConnection(sourcePin.getComponentId(), pinId, targetPin.getComponentId(), targetPin.getUUID(), connectionId);
        }
        catch(Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public static String toJSON(Map<UUID, PinConnection> list)
    {
        if ( list == null ) {
            return "";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            String val = mapper.writeValueAsString(list);
            return val;
        }
        catch( Exception e)
        {
            return null;
        }
    }
}
