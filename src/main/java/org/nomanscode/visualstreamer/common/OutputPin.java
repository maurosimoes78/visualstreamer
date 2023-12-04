
package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.common.interfaces.*;

import java.util.*;
import java.util.stream.Collectors;

public class OutputPin extends Pin implements IOutputPin
{
    @JsonIgnore
    boolean dispatched = false;
    @JsonIgnore
    ComponentExecuter component_;

    public OutputPin (final ComponentExecuter component,
                      final UUID uuid,
                      final String name)
    {
        super(component, PinDirection.PINDIR_OUTPUT,
                uuid,
                name,
                false,
                null,
                null,
                0);

        component_ = component;
    }

    public OutputPin (final ComponentExecuter component,
                      final UUID uuid,
                      final String name,
                      final List<PinType> defaultPinTypes)
    {
        super(component, PinDirection.PINDIR_OUTPUT,
                uuid,
                name,
                false,
                defaultPinTypes,
                null,
                0);

        component_ = component;
    }

    public OutputPin (final ComponentExecuter component,
                      final UUID uuid,
                      final String name,
                      final List<PinType> defaultPinTypes,
                      final UUID equivalentUIID,
                      final int sequencial)
    {
        super(component, PinDirection.PINDIR_OUTPUT,
                uuid,
                name,
                false,
                defaultPinTypes,
                equivalentUIID,
                sequencial);

        component_ = component;
    }

    public OutputPin ( final OutputPin pin )
    {
        super(pin.component_, PinDirection.PINDIR_OUTPUT,
                pin.getUUID(),
                pin.getName(),
                false,
                pin.getDefaultPinTypes(),
                pin.getPinTypes(),
                pin.getEquivalentUUID(),
                pin.getSequencial());

        component_ = pin.component_;
    }

    //Allows us to create a new pin based on another
    public OutputPin(final ComponentExecuter component, final OutputPin model, UUID newId, int sequencial)
    {
        super(component, PinDirection.PINDIR_OUTPUT,
                newId,
                model.getName(),
                false,
                model.getDefaultPinTypes(),
                model.getPinTypes(),
                model.getUUID(),
                sequencial);

        this.component_ = component;
    }

    @Override
    protected void onCompleteConnect(PinConnection pinConnection) {

        this.component_.onConnectOutputPin(PinInfo.copy(this));

        this.component_.onCompleteConnect(PinInfo.copy(this), PinConnection.copy(pinConnection));

    }

    @Override
    protected void onBeforeDisconnect() {

    }

    @Override
    protected boolean checkPinConnection(IPin pin) {
        return this.component_.checkOutputPinConnection( (IInputPin) pin, this);
    }

    @Override
    protected void onCompleteDisconnect() {
        this.component_.onCompleteDisconnect(PinInfo.copy(this));

        this.component_.onDisconnectOutputPin(PinInfo.copy(this));
    }

    public void dispose(boolean notify)
    {
        this.disconnect();

        if ( notify ) {
            this.component_.onRemovePin(PinInfo.copy(this));
        }
    }

    public void disconnect() {

        this.resetPinTypes();

        if ( this.isConnected() ) {
            ((IInputPin) this.getConnectedPin()).requestPinTypesReset();
        }

        super.disconnect();
    }

    public boolean dispatch(String name, Object value, Class c, MyHolder<String> errorMessage) {
        return this.dispatch(name, value, c.getName(), errorMessage);
    }

    public boolean dispatch(String name, Object value, Class c) {
        return this.dispatch(name, value, c.getName());
    }

    //Receives a request from downstream pin to check if the current component has the same
    //type as specified in the parameter.
    @Override
    public IComponentControl findPluginPassThru(PluginType type, boolean skip) {

        return this.component_.findPluginPassThru(this, type, skip);

    }

    @Override
    public IComponentControl findPluginPassThru(PluginType type) {
        return this.component_.findPluginPassThru(this, type, false);
    }

    public boolean dispatch(String name, Object value, String classPath) {
        return this.dispatch();
    }

    public boolean dispatch(String name, Object value, String classPath, MyHolder<String> errorMessage) {
        return this.dispatch(errorMessage);
    }

    public boolean dispatch() {
        return this.dispatch();
    }

    public boolean dispatch(MyHolder<String> errorMessage)
    {
        if (!super.isConnected() || this.dispatched)
        {
            return true;
        }


        //Pin is set as the data went through it to the downstream input pin
        //this.dispatched = true; //WARNING:
        InputPin pin = (InputPin) this.connectedTo_;
        boolean bRet = pin.receiveRun(errorMessage);
        if ( !bRet ) {
            return false;
        }

        //We're special plugins and we have specific ways to deliver data to
        //pins.
        if ( this.component_.getType() == PluginType.PLUGIN_TYPE_ENTRY ||
             this.component_.getType() == PluginType.PLUGIN_TYPE_WATCH_FOLDER ) {
            return true; //Yes we don't want to replicate output pin data since
                         //in our perspective each input pin need special treatment!
        }

        //Replicate the common parameter to the correspondent aggregated pin.
        //This is just for regular plugins. Special plugins like ENTRY or WATCH FOLDER
        //have their own way of delivering parameters data.
        bRet = !this.component_.getOutputPinInstances()
                .values()
                .stream()
                .filter(p -> {

                    if (p.getEquivalentUUID() != null && p.getEquivalentUUID().equals(this.getUUID())) {
                        return true;
                    }

                    return false;
                })
                .anyMatch(p2 -> {

                    boolean ret = p2.dispatch(errorMessage);
                    return !ret;
                });

        return bRet;
    }

    public void dispatchAbort()
    {
        if (!super.isConnected() || this.connectedTo_ == null)
        {
            return;
        }

        System.out.println("Dispatching abort through pin " + this.getName() + " of " + this.component_.getName());
        InputPin pin= (InputPin) this.connectedTo_;
        pin.receiveAbort();
    }

    public void dispatchBeginDelivery()
    {
        if (!super.isConnected() || this.connectedTo_ == null)
        {
            return;
        }

        InputPin pin= (InputPin) this.connectedTo_;
        pin.beginDelivery();
    }

    public void reset() {
        this.dispatched = false;
    }

    public boolean isDispatched() {
        return this.dispatched;
    }

    //---------------------------------------------------------------------
    private IOutputPin createEquivalentOutputPin()
    {

        int sequencial = 0;
        List<Integer> sequence = this.component_.getOutputPinInstances().values()
                .stream().filter( p -> p != null &&
                        p.getEquivalentUUID() != null &&
                        p.getEquivalentUUID().equals(this.getUUID()))
                .map( OutputPin::getSequencial )
                .sorted().collect(Collectors.toList());

        for (Integer seqNum : sequence) {
            if ( seqNum > sequencial ) {
                break;
            }
            sequencial++;
        }

        return this.component_.createOutputPin(this, sequencial);
    }


    //Gets the next available output pin based on a model pin
    //If there is no available pin the routine will create another
    //one based on the model pin.
    private IOutputPin getNextAvailableOutputPin()
    {
        //Is our model pin connected? If not we will use it!
        if ( this.isConnected() == false ) {
            return this;
        }

        //We need an disconnected output pin that is suitable to model pin
        OutputPin current = this.component_.getOutputPinInstances().values()
                .stream()
                .filter( p -> p != null &&
                        !p.isConnected() &&
                        p.getEquivalentUUID() != null &&
                        p.getEquivalentUUID().
                                equals(this.getUUID()))
                .findAny()
                .orElse(null);

        if ( current == null ) {
            //We didnt find it. Let's create a new pin similar to the
            //model pin. In order to generate a similar new name we need
            //to iterate

            return this.createEquivalentOutputPin();

        }

        return current;
    }

    public PinConnection connect(IInputPin pin, MyHolder<IOutputPin> alternativeOutputPin) {

        if ( !pin.isCompatible(this.getPinTypes()) ){
            return null;
        }

        if ( this.isConnected()) {
            //
            IOutputPin outPin = getNextAvailableOutputPin();
            if ( outPin == null ) {
                return null;
            }

            alternativeOutputPin.value = outPin;

            return outPin.connect(pin);

        }

        return super.connect(pin);
    }

    //---------------------------------------------------------------------
    public static OutputPin copy (OutputPin pin)
    {
        if (pin == null) {
            return null;
        }

        return new OutputPin(pin);
    }

    public static Map<UUID, OutputPin> copyAllPins(Map<UUID, OutputPin> pins)
    {
        if ( pins == null ) {
            return null;
        }

        Map<UUID, OutputPin> ret = new LinkedHashMap<>();

        for (OutputPin pin : pins.values()) {
            OutputPin newPin = OutputPin.copy(pin);
            ret.put( newPin.getUUID(), newPin);
        }

        return ret;

    }

    public static OutputPin create(ComponentExecuter component, PinInfo pin) {
        return new OutputPin(component,
                pin.getUUID(),
                pin.getName(),
                pin.getPinTypes(),
                pin.getEquivalentUUID(),
                pin.getSequencial());
    }

    /*@Override
    public void autoRouteParameters(List<IParameter> params) {
        //Just input pin may auto route parameters
    }*/

    @Override
    public boolean isCompatible(PinType type) {
        //Checks if type matches to one in our type list.
        //
        if ( this.getPinTypes().stream()
                .filter( t -> t.equals(type))
                .findAny()
                .orElse(null) == null ) {

            return false; //The pin has not the specified type so we fail. It is not compatible.
        }

        //The pin has the specified type so we succeed!
        return true;
    }

    public boolean acceptPinTypesChanges(List<PinType> types) {

        if ( !this.isConnected() ) {
            return true; //We are not connected, consider as it has accepted
        }

        if ( !this.isExclusivelyCompatibleWithByDefault(PinType.PIN_TYPE_ANY) ) {
            return false; //Our default type is not ANYTHING... so we don't even
            //need to check on our counter parties if they would accept or not.
        }

        //Asks to our connected pin if our change would be accepted.
        //If it has the type of ANY will accept. Also if the component
        //has output pins (of type ANY only) it will forward the checking on them
        //and will return successfully just in the case all of them replied with an OK.
        if ( !this.getConnectedPin().acceptPinTypesChanges(types) ) {
            return false;
        }

        return true;
    }

    public boolean changePinTypes(List<PinType> types) {

        if ( !this.isExclusivelyCompatibleWithByDefault(PinType.PIN_TYPE_ANY) ) {
            return false; //Our default type is not ANYTHING... so we don't even
                          //bother our counter part requesting changes...
        }

        if ( !this.isConnected() ) {
            this.setPinTypes(types);
            return true; //We are not connected, consider as it has successfully changed the pin types.
        }

        if ( !this.getConnectedPin().changePinTypes(types) ) {
            return false;
        }

        return true;
    }

    public void requestPinTypesReset() {

        //Reset our types
        super.resetPinTypes();

        //If we are disconnected return
        if ( !this.isConnected() ) {
            return;
        }

        //If connected we need to ask to our counter parties to reset their types too.
        ((IInputPin) this.getConnectedPin()).requestPinTypesReset();
    }
}
