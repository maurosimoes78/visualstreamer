package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.common.interfaces.*;


import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class InputPin extends Pin implements IInputPin
{
    @JsonIgnore
    private ComponentExecuter component_ = null;
    @JsonIgnore
    private boolean bReady_ = false;
    @JsonIgnore
    private boolean doNotBlockComponent_ = true;
    @JsonIgnore
    private boolean ignoreComponentStatus_ = true;

    @JsonIgnore
    private boolean modifyOutputPins_ = true;

    @JsonIgnore
    final ReentrantLock lock = new ReentrantLock();

    public InputPin (final ComponentExecuter component,
                     final UUID uuid,
                     final String name,
                     final boolean connectionRequired)
    {
        super(component, PinDirection.PINDIR_INPUT,
                uuid,
                name,
                connectionRequired,
                null,
                null,
                0);

        this.component_ = component;
        this.doNotBlockComponent_ = false;
        this.ignoreComponentStatus_ = false;
    }

    public InputPin (final ComponentExecuter component,
                     final UUID uuid,
                     final String name,
                     final boolean connectionRequired,
                     final List<PinType> defaultInputTypes )
    {
        super(component, PinDirection.PINDIR_INPUT,
                uuid,
                name,
                connectionRequired,
                defaultInputTypes,
                null,
                0);

        this.component_ = component;
        this.doNotBlockComponent_ = false;
        this.ignoreComponentStatus_ = false;

    }

    public InputPin (final ComponentExecuter component,
                     final UUID uuid,
                     final String name,
                     final boolean connectionRequired,
                     final List<PinType> defaultInputTypes,
                     final boolean doNotBlockComponent,
                     final boolean ignoreComponentStatus)
    {
        super(component, PinDirection.PINDIR_INPUT,
                uuid,
                name,
                connectionRequired,
                defaultInputTypes,
                null,
                0);

        this.component_ = component;
        this.doNotBlockComponent_ = doNotBlockComponent;
        this.ignoreComponentStatus_ = ignoreComponentStatus;

    }

    public InputPin (final ComponentExecuter component,
                     final UUID uuid,
                     final String name,
                     final boolean connectionRequired,
                     final List<PinType> defaultInputTypes,
                     final boolean doNotBlockComponent,
                     final boolean ignoreComponentStatus,
                     final boolean notifyParameterChanges)
    {
        super(component, PinDirection.PINDIR_INPUT,
                uuid,
                name,
                connectionRequired,
                defaultInputTypes,
                null,
                0);

        this.component_ = component;
        this.doNotBlockComponent_ = doNotBlockComponent;
        this.ignoreComponentStatus_ = ignoreComponentStatus;

    }

    public InputPin (final ComponentExecuter component,
                     final UUID uuid,
                     final String name,
                     final boolean connectionRequired,
                     final List<PinType> defaultInputTypes,
                     final boolean doNotBlockComponent,
                     final boolean ignoreComponentStatus,
                     final boolean notifyParameterChanges,
                     final boolean modifyOutputPins)
    {
        super(component, PinDirection.PINDIR_INPUT,
                uuid,
                name,
                connectionRequired,
                defaultInputTypes,
                null,
                0);

        this.component_ = component;
        this.doNotBlockComponent_ = doNotBlockComponent;
        this.ignoreComponentStatus_ = ignoreComponentStatus;
        this.modifyOutputPins_ = modifyOutputPins;

    }

    public InputPin( final InputPin pin )
    {
        super(  pin.component_,
                PinDirection.PINDIR_INPUT,
                pin.getUUID(),
                pin.getName(),
                pin.getConnectionRequired(),
                pin.getDefaultPinTypes(),
                pin.getEquivalentUUID(),
                pin.getSequencial());

        this.component_ = pin.component_;
        this.doNotBlockComponent_ = pin.doNotBlockComponent_;
        this.ignoreComponentStatus_ = pin.ignoreComponentStatus_;
        this.modifyOutputPins_ = pin.modifyOutputPins_;
    }

    public boolean shouldBlockComponent()
    {
        return ! this.doNotBlockComponent_;
    }

    public boolean ignoreComponentStatus()
    {
        return this.ignoreComponentStatus_;
    }

    @Override
    protected void onCompleteConnect(PinConnection pinConnection) {
        this.component_.onConnectInputPin(PinInfo.copy(this));
    }

    @Override
    protected void onBeforeDisconnect() {

    }

    @Override
    protected boolean checkPinConnection(IPin pin) {

        if ( !this.component_.checkInputPinConnection(this, (IOutputPin)pin) ) {
            return false;
        }

        if ( this.isExclusivelyCompatibleWithByDefault(PinType.PIN_TYPE_ANY) ) {

            //If our pin has the type of ANY by default (and only this one)

            //Also, we need to update all the output pins having just one type (ANY)
            //in order they follow our change. Pins having other types will not be changed!
            //All the pins accepted the changes. We need to request them to change their types.
            if ( this.getComponentControl().getOutputPinInstances().values().stream()
                    .filter(p-> p.isExclusivelyCompatibleWithByDefault(PinType.PIN_TYPE_ANY))
                    .noneMatch( output -> {

                        //Triggers a checking on the output pins
                        //verifying if their counter parts (input pins) accept
                        //the change we will make. If not we need to fail our connection.
                        //Otherwise we will request them to change their types.
                        return output.changePinTypes(pin.getPinTypes());
                    }) ) {

                //One of the pins rejected our needs. We need to abort this connection.
                return false;
            }

            //Since all the output pins and their counter parts has been changed we can change our
            //own types and proceed with the connection.
            this.setPinTypes(pin.getPinTypes());
        }

        return true;
    }

    @Override
    protected void onCompleteDisconnect() {

    }

    public void dispose(boolean notify) {
        disconnect();

        if ( notify ) {
            this.component_.onRemovePin(PinInfo.copy(this));
        }
    }

    @Override
    public IComponentControl findPluginPassThru(PluginType type) {

        return this.findPluginPassThru(type, false);
    }

    @Override
    public IComponentControl findPluginPassThru(PluginType type, boolean skip) {

        if ( !this.isConnected() || this.connectedTo_ == null ) {
            return null;
        }

        IPin pin = this.connectedTo_;
        return pin.findPluginPassThru(type, skip);

    }

    PinConnection connect(OutputPin pin) {
        return super.connect(pin);
    }

    public void disconnect() {

        this.resetPinTypes();

        super.disconnect();
    }

    public void reset() {
        this.bReady_ = false;
    }

    /*private boolean isRequiredMissing (List<ParameterData> params, MyHolder<List<String>> missingParameterList)
    {
        List<String> required = this.getRequiredParametersLinkedToName();
        if ( required != null ) {
            //There are required parameters, we need to check them
            //to determine whether they are all present.
            required.removeAll(this.getParametersName(params));
            if (required.size() > 0) {
                //Error, there are required parameters missing

                missingParameterList.value = new ArrayList<>(required);
                return true;
            }
        }

        //There is no required parameters set or
        //they are all here. Either way, we can proceed.
        return false;
    }*/

    //We must ensure that all the required parameters are set.
    public boolean receiveRun(MyHolder<String> errorMessage)
    {
        try (Autolock alock = new Autolock(lock)) {

            //If thread is running block until it is not running anymore.
            this.component_.waitForCompletion();

            this.component_.onReceive(this);
            return run();
        }
    }

    public boolean run()
    {
        this.bReady_ = true;
        return component_.runInternal(this);
    }

    public void receiveAbort()
    {
        this.component_.abort();
    }

    public void beginDelivery() {

        this.component_.reset();
    }

    public boolean isReady()
    {

        return !this.isConnected() || bReady_ || doNotBlockComponent_;
    }

    //---------------------------------------------------------------------
    public static InputPin copy (InputPin pin)
    {
        if (pin == null) {
            return null;
        }

        return new InputPin(pin);
    }

    public static Map<UUID, InputPin> copyAllPins(Map<UUID, InputPin> pins)
    {
        if ( pins == null ) {
            return null;
        }

        Map<UUID, InputPin> ret = new LinkedHashMap<>();

        pins.forEach( (key, value) -> ret.put(key, InputPin.copy(value)));

        return ret;

    }

    public static InputPin create(ComponentExecuter component, PinInfo pin) {
        return new InputPin(component,
                            pin.getUUID(),
                            pin.getName(),
                            pin.getConnectionRequired(),
                            pin.getPinTypes(),
                            false,
                            false);
    }

    //
    //Checks if the pin type provided exists in the pin type list.
    //
    //
    @JsonIgnore
    public boolean isCompatible(PinType type)
    {

        //Checks if type matches to one in our type list or
        //we accept ANYTHING (PIN_TYPE_ANY).
        //
        if ( this.getPinTypes().stream()
                .filter( t -> t.equals(type))
                .findAny()
                .orElse(null) == null &&
                !type.equals(PinType.PIN_TYPE_ENTRY)) {

            return false; //The pin has not the specified type and does not accept ANYTHING... so we fail. It is not compatible.
        }

        //The pin has the specified type or it has the type of ANYTHING. So we succeed!
        return true;
    }

    //
    //Checks if the pin has at least one type of those specified in the provided type list.
    //
    @JsonIgnore
    public boolean isCompatible(List<PinType> type)
    {
        if ( type.stream().filter(this::isCompatible)
                .findAny()
                .orElse(null) == null ) {
            return false;
        }

        return true;
    }

    //Checks if one of the provided type is accepted by this pin
    //or we accept anything (ANY).
    //If we accept and our component has output pins (of type ANY only)
    //we will forward the checking on them.
    public boolean acceptPinTypesChanges(List<PinType> types) {

        //Checks if we accept ANYTHING or have specific types
        if ( !this.isExclusivelyCompatibleWithByDefault(PinType.PIN_TYPE_ANY) &&
             !this.isCompatible(types) ) {
            return false; //We are not exclusively of type ANY and not accept any of the provided types.
        }

        //We accept the changes, now we need to check with our output pins (if any).
        //We will fail if any of them fail to accept the changes.
        return this.getComponentControl().getOutputPinInstances().values().stream()
                .filter(output -> output.isExclusivelyCompatibleWithByDefault(PinType.PIN_TYPE_ANY))
                .noneMatch( output -> {

                        boolean ret = output.acceptPinTypesChanges(types);
                        return ret;
                });

    }

    @Override
    public boolean changePinTypes(List<PinType> types) {

        //Checks if we accept ANYTHING or have specific types
        if ( this.acceptPinTypesChanges(types) ) {
            return false; //We are not exclusively of type ANY and not accept any of the provided types.
        }

        //We accept the changes, now we need to askour output pins (if any) to change
        //We will fail if any of them fail to accept the changes.
        boolean ret = this.getComponentControl().getOutputPinInstances().values().stream()
                .filter(output -> output.isExclusivelyCompatibleWithByDefault(PinType.PIN_TYPE_ANY))
                .noneMatch( output -> output.changePinTypes(types));

        if ( !ret ) {
            return false;
        }

        //Now we change our types.
        this.setPinTypes(types);

        return true;
    }

    public void requestPinTypesReset()
    {
        if ( !this.isExclusivelyCompatibleWithByDefault(PinType.PIN_TYPE_ANY) ) {
            this.disconnect();
            return;
        }
        //We need to reset or pin type list to default...
        super.resetPinTypes();

        //... and ask to our output pins to do the same.
        this.getComponentControl().getOutputPinInstances().values()
                .forEach(OutputPin::requestPinTypesReset);
    }
}
