package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.common.interfaces.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class Pin extends PinInfo implements IPin, IAnalyzer {

    public static class Autolock implements AutoCloseable {
        public Autolock(ReentrantLock lock) {
            this.mLock = lock;
            mLock.lock();
        }

        @Override
        public void close() {
            mLock.unlock();
        }

        private final ReentrantLock mLock;
    }

    @JsonIgnore
    private UUID pinConnectionId = null;
    @JsonIgnore
    protected IPin connectedTo_ = null;
    @JsonIgnore
    private ComponentExecuter component_ = null;

    abstract protected void onCompleteConnect(PinConnection pinConnection);
    abstract protected void onCompleteDisconnect();
    abstract protected void onBeforeDisconnect();
    abstract protected boolean checkPinConnection(IPin pin);
    abstract public IComponentControl findPluginPassThru(PluginType type);
    abstract public boolean acceptPinTypesChanges(List<PinType> types);
    abstract public boolean isCompatible(PinType type);

    public Pin(ComponentExecuter component,
               final PinDirection pindir,
               final UUID uuid,
               final String name,
               final boolean connectionRequired,
               final List<PinType> defaultPinTypes,
               final UUID equivalentUUID,
               final int sequencial) {

        super(pindir, uuid, name, connectionRequired, defaultPinTypes, null, equivalentUUID, sequencial, null);

        this.component_ = component;
    }

    public Pin(ComponentExecuter component,
               final PinDirection pindir,
               final UUID uuid,
               final String name,
               final boolean connectionRequired,
               final List<PinType> defaultPinTypes,
               final List<PinType> pinTypes,
               final UUID equivalentUUID,
               final int sequencial) {

        super(pindir, uuid, name, connectionRequired, defaultPinTypes, null, equivalentUUID, sequencial, null);

        this.component_ = component;
    }

    public Pin(ComponentExecuter component, final Pin pin) {
        super(pin);
        this.connectedTo_ = pin.connectedTo_;
        this.component_  = component;
    }

    public void dispose()
    {
        this.disconnect();
    }

    @Override
    public boolean isConnected()
    {
        return ( this.connectedTo_ != null );
    }

    public PinConnection connect(IPin pin)
    {

        if ( this.connectedTo_ != null ) {
            if ( this.connectedTo_ == pin ) {

                PinConnection pinConnection = PinConnection.create(this, pin);

                this.pinConnectionId = pinConnection.getConnectionId(); //Store the pin connection Id

                return pinConnection;
            }
            else {
                //Pin is already connected. We must disconnect first!
                return null;
            }

        }

        if ( pin == null)
        {
            return null;
        }

        //Checks if pin agrees with the counter party.
        if ( !this.checkPinConnection(pin) ) {
            return null;
        }

        //Holds counter part reference and saves its id
        this.connectedTo_ = pin;
        this.setConnectedToId(pin.getUUID());

        //Tells counter part our id and passes our reference
        PinConnection pinConnection = pin.connect(this);

        if ( pinConnection == null ) {
            this.connectedTo_ = null;
            this.setConnectedToId(null);
            return null;
        }

        this.pinConnectionId = pinConnection.getConnectionId(); //Stores our pin connection id

        //Tells parent classes this pin
        //is now connected.
        this.onCompleteConnect(pinConnection);

        return pinConnection;
    }

    public void disconnect()
    {

        this.pinConnectionId = null;

        if ( this.connectedTo_ == null )
        {
            return;
        }

        //Notifies parent classes
        //this pin is about to get disconnected
        this.onBeforeDisconnect();

        //Resets counterpart pin identification
        this.setConnectedToId(null);

        IPin tempReference = this.connectedTo_;
        this.connectedTo_ = null;

        tempReference.disconnect();

        //Notifies parent classes
        //this pin has been disconnected
        this.onCompleteDisconnect();
    }

    public UUID getComponentId() {
        return this.component_.getId();
    }

    public String getComponentName() {
        return this.component_.getName();
    }

    public IPin getConnectedPin() {
        return this.connectedTo_;
    }

    public ComponentStatus runAnalysis() {
        return this.component_.runAnalysis();
    }

    public IComponentControl getComponentControl() {
        return this.component_;
    }

//-----------------------------------------------------------------
    public UUID getConnectionId() {
        return this.pinConnectionId;
    }

    @JsonIgnore
    public boolean isExclusivelyCompatibleWithByDefault(PinType type)
    {
        return this.isExclusivelyCompatibleWith(this.getDefaultPinTypes(), type);
    }

    @JsonIgnore
    public boolean isCurrentlyExclusivelyCompatibleWith(PinType type)
    {
        return this.isExclusivelyCompatibleWith(this.getPinTypes(), type);
    }

    @JsonIgnore
    public boolean isExclusivelyCompatibleWith(List<PinType> repository, PinType type)
    {

        if ( repository == null ) {
            return false;
        }

        //Checks if we got only one type and if it is of type as provided.
        if ( repository.size() != 1 ) {
            return false; //There is more than one type.
        }

        //Ok, there is just one type... Let's check if this type is the same as of the provided one.
        if ( !repository.get(0).equals(type)) {
            return false; //It is not!
        }

        return true;
    }

}
