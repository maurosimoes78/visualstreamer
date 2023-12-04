package org.nomanscode.visualstreamer.common;

import static org.nomanscode.visualstreamer.common.ComponentProperty.TASK_EXPECTS_ALL_THE_INPUT_PINS;
import static org.nomanscode.visualstreamer.common.ComponentStatus.*;
import static org.nomanscode.visualstreamer.common.PinDirection.PINDIR_INPUT;
import static org.nomanscode.visualstreamer.common.PinDirection.PINDIR_OUTPUT;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nomanscode.visualstreamer.common.interfaces.*;
import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.exceptions.*;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class ComponentExecuter extends PluginInfo implements IFeedback, IComponentControl, IAnalyzer, IComponentExecutor {

    @JsonIgnore
    public final Lock threadWaitLock = new ReentrantLock();
    @JsonIgnore
    private boolean dispatchEvents = false;
    @JsonIgnore
    private Lock lock;
    @JsonIgnore
    private Thread thread_;
    @JsonIgnore
    private String tag_;
    @JsonIgnore
    private boolean abort_ = false;
    @JsonIgnore
    private ComponentProperty property_ = TASK_EXPECTS_ALL_THE_INPUT_PINS;
    @JsonIgnore
    private Map<UUID, InputPin> input_pin_ = new LinkedHashMap<>();
    @JsonIgnore
    private Map<UUID, OutputPin> output_pin_ = new LinkedHashMap<>();
    /*@JsonIgnore
    private IJobController jobController_ = null;
    @JsonIgnore
    private IJobExecuter job_ = null;*/
    @JsonIgnore
    private Profile originalProfile_ = null;
    @JsonIgnore
    private Integer old_progress = 0;

    @JsonIgnore
    final ReentrantLock waitForCompletionLock = new ReentrantLock();
    final ReentrantLock commandLock = new ReentrantLock();

    //Abstract Methods
    protected abstract boolean begin();
    protected abstract boolean execute(IProfile profile, IFeedback feedback, /* IJobExecutor executor, IPluginResourceManager resources,*/ Thread thread) throws CybertronSDKException;
    protected abstract boolean end();
    protected abstract void begin_dispatch();
    protected abstract void end_dispatch();
    protected abstract void getDefaultProfile(IProfile customProfile);
    public abstract boolean checkProfileChanges(Profile profile, MyHolder<String> errorMessage);
    public abstract void setup();
    public abstract void dispose();
    public abstract boolean checkOutputPinConnection(IInputPin candidate, IOutputPin pin);
    public abstract boolean checkInputPinConnection(IInputPin pin, IOutputPin candidate);
    public abstract void onConnectInputPin(IPinInfo pin);
    public abstract void onDisconnectInputPin(IPinInfo pin);
    public abstract void onConnectOutputPin(IPinInfo pin);
    public abstract void onDisconnectOutputPin(IPinInfo pin);
    public abstract void onAbortRequested();
    public abstract void onPauseRequested();
    public abstract void onRunRequested();

    //Implemented Methods

    public ComponentExecuter(/*IJobExecuter job, IJobController jobController,*/ List<PluginGroupType> groupTypes, PluginType type, String name, String description, String version, String vendor, UUID productIdentificationId) {
        this.initialize( /*job, jobController,*/ groupTypes, type, UUID.randomUUID(), name, description,"", null, null, TASK_EXPECTS_ALL_THE_INPUT_PINS, version, vendor, productIdentificationId, null  );
    }

    public ComponentExecuter(/*IJobExecuter job, IJobController jobController,*/ List<PluginGroupType> groupTypes, PluginType type, String name, String description, String version, String vendor, UUID productIdentificationId, String icon) {
        this.initialize( /*job, jobController, */groupTypes, type, UUID.randomUUID(), name, description,"", null, null, TASK_EXPECTS_ALL_THE_INPUT_PINS, version, vendor, productIdentificationId, icon  );
    }

    public ComponentExecuter(/*IJobExecuter job, IJobController jobController,*/ List<PluginGroupType> groupTypes, PluginType type, String name, String description, String version, String vendor, UUID productIdentificationId, String icon, ComponentProperty property) {
        this.initialize( /*job, jobController,*/ groupTypes, type, UUID.randomUUID(), name, description,"", null, null, property, version, vendor, productIdentificationId, icon );
    }

    public void dispose(boolean notify) {

        try {

            this.dispose();

            deleteInputPins(notify);
            deleteOutputPins(notify);

            /*if (this.job_ != null && notify) {
                this.job_.onDeletePlugin(this.getId(), PluginInfo.copy(this));
            }*/
        }
        catch(Exception e) {
            System.out.println("ComponentExecutor::dispose: " + e.getMessage());
        }
    }

    private void deleteInputPins(boolean notify) {

        if (this.input_pin_ == null) {
            return;
        }

        List<UUID> ids = new ArrayList<>(this.input_pin_.keySet());
        ids.forEach(id -> this.deleteInputPin(id, notify));

        this.input_pin_.clear();
        this.inputPins.clear();
    }

    private void deleteOutputPins(boolean notify) {

        if ( this.output_pin_ == null ) {
            return;
        }

        List<UUID> ids = new ArrayList<>(this.output_pin_.keySet());
        ids.forEach(id -> this.deleteOutputPin(id, notify));

        this.output_pin_.clear();
        this.outputPins.clear();
    }

    /*public String loadIcon() throws IOException {

        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource("/icons/plugin.svg").getFile());

        //Read File Content
        return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
    }*/

    private void initialize(/*IJobExecuter job, IJobController jobController, */List<PluginGroupType> groupTypes, PluginType type, UUID uuid, String name, String description, String tag, Map<UUID,InputPin> input_pin, Map<UUID, OutputPin> output_pin, ComponentProperty property, String version, String vendor, UUID productIdentificationId, String icon) {

        if ( icon == null || icon.isEmpty() ) {
            this.icon = "fas fa-puzzle-piece";
        }
        else {
            this.icon = icon;
        }

        //this.job_ = job;
        //this.jobController_ = jobController;

        this.addGroupTypes(groupTypes);

        this.lock = new ReentrantLock();

        this.setType(type);
        this.setId(uuid);
        this.setPluginName(name);
        this.setName(name);
        this.setDescription(description);

        this.setVersion(version);
        this.setVendor(vendor);
        this.setProductIdentificationId(productIdentificationId);

        this.tag_ = tag;

        this.abort_ = false;
        super.setStatus(TASK_IDLE);
        this.property_ = property;

        if ( input_pin != null ) {
            input_pin.values().forEach(this::createInputPin);
        }
        if ( output_pin != null ) {
            output_pin.values().forEach(this::createOutputPin);
        }

        try {

            //configuration
            //    |
            //    |- Custom
            //    |     |
            //    |     |-  Custom Item Implementations...

            this.originalProfile_ = Profile.create("Configuration", Thread.currentThread().getStackTrace()[3].getClassName());

            Profile custom = Profile.addChild(this.originalProfile_, "Custom");
            this.getDefaultProfile(custom);

            this.setProfile(this.originalProfile_);
        }
        catch(Exception e)
        {
            this.error(e.getMessage());
        }

    }

    /*public void setup(UUID id, UUID referenceId, String name, String description, boolean enabled, boolean deletable, long xCoord, long yCoord, Profile profile) {
        this.setup(id, referenceId, name, description, enabled, deletable, null, null, xCoord, yCoord, profile);
    }

    public void setup(UUID id, UUID referenceId, String name, String description, boolean enabled, boolean deletable, Map<UUID, PinInfo> inputPins, Map<UUID, PinInfo> outputPins, long xCoord, long yCoord, Profile profile) {
        this.setup(id, referenceId, name, description, enabled, deletable, inputPins, outputPins, xCoord, yCoord, profile);
    }*/

    public void setup(UUID id, UUID referenceId, String name, String description, boolean enabled, boolean deletable, long xCoord, long yCoord, Profile profile) {
        this.setup(id, referenceId, name, description, enabled, deletable, xCoord, yCoord, profile, null, null);
    }

    public void setup(UUID id, UUID referenceId, String name, String description, boolean enabled, boolean deletable, long xCoord, long yCoord, Profile profile, Map<UUID, PinInfo> inputPins, Map<UUID, PinInfo> outputPins) {

        //O Id do plugin passa a ser o id do componente
        this.setId(id);

        //Stores the original plugin id as stored in the service
        this.setReferenceId(referenceId);

        this.setName(name);
        this.setDescription(description);

        //Invoke setup method, so pins can be created there
        //if they're still not created.
        this.setup();

        this.setXCoord(xCoord);
        this.setYCoord(yCoord);

        this.setEnabled(enabled);
        this.setDeletable(deletable);

        if ( inputPins != null && inputPins.size() > 0 ) {
            //We want to create pins accordingly to a preexisting pin configuration other than the default.
            this.deleteInputPins(false);
            inputPins.values().forEach( pin -> {
                this.createInputPin(pin);
            });

        }

        if ( outputPins != null && outputPins.size() > 0 ) {
            //We want to create pins accordingly to a preexisting pin configuration other than the default.
            this.deleteOutputPins(false);
            outputPins.values().forEach( pin -> {
                this.createOutputPin(pin);
            });
        }

        this.dispatchEvents = true; //Enables event echo

        this.setCurrentProfile(profile, null);

        /*if ( this.job_ != null && this.dispatchEvents) {
            this.job_.onCreatePlugin(this.getId(), PluginInfo.copy(this));
        }*/
    }

    /*private boolean setInputPins(Map<UUID, PinInfo> inputPins) {
        try {

            deleteInputPins();

            return !inputPins.values().stream().anyMatch( p -> !this.createInputPin(p) );
        }
        catch(Exception e) {
            return false;
        }
    }

    private boolean setOutputPins(Map<UUID, PinInfo> outputPins) {
        try {

            deleteOutputPins();

            return !outputPins.values().stream().anyMatch( p -> !this.createOutputPin(p) );
        }
        catch(Exception e) {
            return false;
        }
    }*/

    public boolean move(long xCoord, long yCoord) {

        this.setXCoord(xCoord);
        this.setYCoord(yCoord);

        //this.job_.onComponentMove(this.getId(), xCoord, yCoord);

        return true;
    }

    public boolean setCurrentProfile(Profile newProfile, MyHolder<String> errorMessage)
    {
        try {
            if (newProfile == null) {
                return false;
            }

            Profile tempProfile = null;

            if (this.getProfile() != null) {
                tempProfile = Profile.copy(this.getProfile());
                tempProfile = Profile.merge(tempProfile, newProfile);
            } else {
                tempProfile = Profile.copy(newProfile);
            }

            if (!this.checkProfileChanges(tempProfile, errorMessage)) {
                return false;
            }

            this.setProfile(tempProfile);

            /*if ( this.job_ != null && this.dispatchEvents ) {
                this.job_.onProfileChanged(this.getId(), Profile.copy(this.getProfile()));
            }*/

            return true;
        }
        catch(Exception e) {
            this.error(e.getMessage());
            return false;
        }
    }

    public void resetProfile()
    {
        this.setProfile(this.originalProfile_);
    }

    public String getTag()
    {
        return this.tag_;
    }

    public void setTag(String tag)
    {
        this.tag_ = tag;
    }

    /*public UUID getId()
    {
        return this.uuid_;
    }*/

    public PluginInfo getPluginInfo() {
        return this;
    }

    //Exclusively called by JobExecutor. It is safe to reset everything.
    public boolean run()
    {
        this.reset();

        MyHolder<String> errorMessage = new MyHolder<>();
        return this.run(errorMessage);
    }

    public boolean run(MyHolder<String> errorMessage)
    {
        try (Pin.Autolock alock = new Pin.Autolock(commandLock)) {
            try {

                if (this.getStatus() == ComponentStatus.TASK_ABORTING ||
                    this.getStatus() == ComponentStatus.TASK_ABORTED) {
                    //We can't accept any more data.
                    return false;
                }

                if (super.getStatus() == TASK_RUNNING) {
                    //It is running already
                    return true;
                }

                //call beginDelivery on every output
                this.output_pin_.values().forEach(OutputPin::dispatchBeginDelivery);

                //If we don't have any input pins we may trigger thru internals.
                if (input_pin_ == null || this.input_pin_.size() == 0) {
                    return this.runInternal(null);
                }

                //Since we got input pins we need to unblock all before running.
                return !this.input_pin_.values().stream()
                        .filter(p -> p.shouldBlockComponent() == true ||
                                !p.isConnected())
                        .anyMatch(p -> {

                            return !p.run();
                        });
            } catch (Exception e) {
                return false;
            }
        }
    }

    public void pause()
    {
        try (Pin.Autolock alock = new Pin.Autolock(commandLock)) {
            //TODO: Suspender a execução da thread até que
            //      o metodo resume seja chamado.
        }
    }

    public void abort()
    {
        System.out.println(this.getName() + ": Wait for lock to abort...");
        try (Pin.Autolock alock = new Pin.Autolock(commandLock)) {

            System.out.println(this.getName() + ": Lock granted!");

            if ( this.getStatus() == ComponentStatus.TASK_ABORTING ||
                 this.getStatus() == ComponentStatus.TASK_ABORTED ) {
                System.out.println(this.getName() + ": Component already set to abort!");
                return;
            }

            /*if (this.abort_) {
                System.out.println(this.getName() + ": Component already set to abort!");
                return;
            }*/

            this.setStatus(ComponentStatus.TASK_ABORTING);

            System.out.println(this.getName() + ": Dispatching abort notification...");
            this.output_pin_.values().forEach(OutputPin::dispatchAbort);

            System.out.println(this.getName() + ": Abort notification dispatched to all output pins!");

            this.abort_ = true;

            if (this.thread_ == null ||
                this.thread_.getState() == Thread.State.TERMINATED) {
                System.out.println(this.getName() + ": There is no active Thread. Resuming abortion...");
                this.setStatus(ComponentStatus.TASK_ABORTED);
                return;
            }

            synchronized (this.threadWaitLock) {
                this.threadWaitLock.notify();
            }

            //Waits for the thread to terminate
            String threadName = this.thread_.getName();
            System.out.println("Waiting for thread " + threadName + " to finish...");

            System.out.println(this.thread_.getName() + " is " + this.thread_.getState());
            this.thread_.join();

            //unload from memory
            this.thread_ = null;

            System.out.println("Thread " + threadName + " has finished!");
        }
        catch(Exception e) {
            e.getStackTrace();
        }
    }

    public void step()
    {
        try (Pin.Autolock alock = new Pin.Autolock(commandLock)) {
            //TODO: Permitir que a tarefa seja executada mas impedindo
            //      que faca alguma chamada aos pinos seguintes
        }
    }

    public void resume()
    {
        try (Pin.Autolock alock = new Pin.Autolock(commandLock)) {
            //TODO: Exclusivamente após um comando STEP ou PAUSE, resume
            //      fará resume a thread (PAUSE) e fará a chamada aos pinos seguintes.
        }
    }

    //------------------------ PROFILE ------------------------
    //Does profile checking and returns a copy of Profile class
    public Profile getProfile()
    {

        //Allows the component implementation to change anything just before this component
        //get the current profile
        this.onBeforeQueryProfile(this.profile);

        return profile;

        //Updates template entries too.
        //return Profile.getProfileWithCompatibleTemplates(profile, templateController, this.getPluginInfo().getId());
    }
    // -----------------------------------------------------------------

    public void reset() {
        if ( super.getStatus() != TASK_IDLE &&
             super.getStatus() != TASK_PAUSED ) {

            this.abort_ = false;
            this.progress = 0;
            this.setStatus(TASK_IDLE);
            resetOutputPins();
            resetInputPins();

            this.output_pin_.values().forEach(OutputPin::dispatchBeginDelivery);
        }
    }

    private void do_internal_consume()
    {
        try {

            //allow custom component implementation to prepare itself before run!
            this.begin_internal();

            this.information("Starting component execution...");

            boolean bRet = false;

            try {

                //Group resources by plugin resource Ids
                /*PluginResourceManager resourceManager = new PluginResourceManager(this.jobController_, this);

                resourceManager.addAll(this.getUserDefinedResourceIdsEx());

                try {
                    //run thread
                    bRet = execute(this.getProfile(), this, this.jobController_, resourceManager, this.thread_);
                }
                finally {
                    resourceManager.dispose();
                }*/

            /*} catch(CybertronSDKException e) {
                //Cybertron error
                end_internal(TASK_FAILED);
                this.error(e.getMessage());
                return;*/
            } catch (Exception e) {
                //Internal Exception (UNKNOWN error)
                end_internal(TASK_FAILED);
                this.error(e.getMessage());

                return;
            }

            if (this.abort_) {
                //user aborted operation by using abort flag!
                end_internal(TASK_ABORTED);
                return;
            }

            if (!bRet) {
                //execution returned error
                end_internal(TASK_FAILED);
                return;
            }

            //Force progress to get updated (in case user implementation missed that)
            this.setProgress(100);

            this.information("Execution finished.");

            MyHolder<String> errorMessage = new MyHolder<>();
            bRet = this.dispatchData(errorMessage);

            if ( !bRet ) {
                //One or multiple output pins have failed.
                this.information("Data dispatch failed: " + errorMessage.value);
                this.end_internal(TASK_FAILED);
                return;
            }

            this.end_internal(TASK_COMPLETE);
        }
        catch (Exception e) {
            //Internal Exception (UNKNOWN error)
            end_internal(TASK_FAILED);
            this.error(e.getMessage());

            return;
        }
        finally {

            //Reset abort flag
            //this.abort_ = false;

            //Reset pin flags and parameter data
            this.resetInputPins();

            //runs job analyzer to collect job status
            this.runJobExecutorAnalyzer();
        }

    }

    public boolean isAbortRequested() {
        return this.abort_;
    }

    public boolean dispatchData(MyHolder<String> errorMessage)
    {

        if ( this.getType() == PluginType.PLUGIN_TYPE_CONDITIONAL_BEGIN ||
             this.getType() == PluginType.PLUGIN_TYPE_WATCH_FOLDER ) {
            return true; //Conditional always send data manually to just one pin per round.
        }

                //call all downstream input pins with all data from input pins (connected or not)
        for (OutputPin pin : this.output_pin_.values()) {
            //We will dispatch data thru connected and non dispatched output pins only.
            //The rest or is disconnected or have been manually dispatched already.
            if (pin.isConnected() && !pin.isDispatched() ) {
                if ( !pin.dispatch(errorMessage)) {
                    return false;
                }
            }
        }

        return true;
    }

    protected boolean begin_internal()
    {
        return begin();
    }

    protected void end_internal(ComponentStatus status)
    {
        //Tells component implementation area this component is about to finish its execution...
        //Everything must be aborted, closed, etc... It is imperative that actions like this take place (memory usage, etc).
        end();

        if (this.getStatus() != status)
        {
            statusChangedInternal(status);
        }

        return;
    }

    private void statusChangedInternal(final ComponentStatus newstatus)
    {

        super.setStatus(newstatus);
        System.out.println(this.getName() + " status: " + this.getStatus());

        /*if ( this.job_ != null ) {
            this.job_.onComponentStatusChanged(this.getId(), this.getStatus());
            this.job_.onComponentChanged(this.getId(), this.getName(), this.getPluginInfo().getId(), this.getPluginInfo().getName(), this.getStatus(), this.getProgress());
        }*/
        //RxJavaUtils.safeOnNext(this.subjectComponentStatus, new Info<>( this.getId(), this.getStatus() ) );

    }

    /*private void reset()
    {
        if ( this.input_pin_ != null ) {
            this.input_pin_.values().forEach( p -> p.reset() );
        }

    }*/

    //Tells job executor to verify job components status
    //to discover whether the workflow is done or not.
    private void runJobExecutorAnalyzer() {

        /*if ( this.job_ == null ) {
            //Error!
            return;
        }

        //Analyzer will check every component output pin within the workflow
        //looking for the workflow execution completion.
        this.job_.onRunAnalyzer(this);*/

    }

    public ComponentStatus getStatus()
    {
        return super.getStatus();
    }

    //Status final comprises COMPLETE or ABORTED or FAILED.
    public boolean isStatusFinal() {
        return super.getStatus() == TASK_ABORTED ||
               super.getStatus() == TASK_FAILED ||
               super.getStatus() == TASK_ABORTED;
    }

    private IInputPin createInputPin(InputPin pin) {

        try {
            if ( pin == null ) {
                return null;
            }

            if (this.input_pin_ == null) {
                this.input_pin_ = new LinkedHashMap<>();
            }


            //Adds a instance of pin into reference list
            this.input_pin_.put(pin.getUUID(), pin);

            //Adds a copy of pins data
            this.inputPins.put(pin.getUUID(), pin);

            //Update component height according to the number of base pin types
            this.updateComponentHeight();

            this.numberOfPins = getNumberOfInstancedPins();

            /*if ( this.job_ != null && this.dispatchEvents) {
                this.job_.onCreatePin(this.getId(), PinInfo.copy(pin));
            }*/

            return pin;
        }
        catch(Exception e) {

            return null;
        }
    }

    public boolean createInputPin(PinInfo pin) {

        try {

            if ( pin == null ) {
                return false;
            }

            InputPin newPin = InputPin.create(this, pin);

            if ( this.createInputPin(newPin) == null ) {
                return false;
            }

            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    public IInputPin createInputPin(UUID uuid, String name, boolean connectionRequired)
    {
        InputPin pin = new InputPin(this, uuid, name, connectionRequired, null);

        return createInputPin(pin);
    }

    public IInputPin createInputPin(UUID uuid, String name)
    {
        return createInputPin( uuid, name, false, PinType.add(PinType.PIN_TYPE_ANY));
    }

    public IInputPin createInputPin(UUID uuid, String name, List<PinType> defaultPinTypes)
    {
        return createInputPin( uuid, name, false, defaultPinTypes);
    }

    public IInputPin createInputPin(UUID uuid, String name, boolean connectionRequired, List<PinType> defaultPinTypes)
    {
        InputPin pin = new InputPin(this, uuid, name, connectionRequired, defaultPinTypes );

        return createInputPin(pin);
    }

    public IInputPin createInputPin(UUID uuid, String name, List<PinType> defaultPinTypes, boolean doNotBlockComponent, boolean ignoreComponentStatus)
    {

        return createInputPin( uuid, name, false, defaultPinTypes, doNotBlockComponent, ignoreComponentStatus);
    }

    public IInputPin createInputPin(UUID uuid, String name, boolean connectionRequired, List<PinType> defaultPinTypes, boolean doNotBlockComponent, boolean ignoreComponentStatus)
    {

        InputPin pin = new InputPin(this, uuid, name, connectionRequired, defaultPinTypes, doNotBlockComponent, ignoreComponentStatus);

        return createInputPin(pin);
    }

    public IInputPin createInputPin(UUID uuid, String name, boolean connectionRequired, List<PinType> defaultPinTypes, boolean doNotBlockComponent, boolean ignoreComponentStatus, boolean notifyParameterChanges)
    {

        InputPin pin = new InputPin(this, uuid, name, connectionRequired, defaultPinTypes, doNotBlockComponent, ignoreComponentStatus, notifyParameterChanges);

        return createInputPin(pin);
    }

    public IInputPin createInputPin(UUID uuid, String name, boolean connectionRequired, List<PinType> defaultPinTypes, boolean doNotBlockComponent, boolean ignoreComponentStatus, boolean notifyParameterChanges, boolean modifyOutputPins)
    {

        InputPin pin = new InputPin(this, uuid, name, connectionRequired, defaultPinTypes, doNotBlockComponent, ignoreComponentStatus, notifyParameterChanges, modifyOutputPins);

        return createInputPin(pin);
    }

    public IInputPin createInputPinFrom(IInputPin inputPin) {
        return this.createInputPinFrom(inputPin.getUUID(), "");
    }

    public IInputPin createInputPinFrom(IPinInfo inputPin) {
        return this.createInputPinFrom(inputPin.getUUID(), "");
    }

    public IInputPin createInputPinFrom(UUID id) {
        return this.createInputPinFrom(id, "");
    }

    public IInputPin createInputPinFrom(IInputPin inputPin, String name) {
        return this.createInputPinFrom(inputPin.getUUID(), name);
    }

    public IInputPin createInputPinFrom(IPinInfo inputPin, String name) {
        return this.createInputPinFrom(inputPin.getUUID(), name);
    }

    public IInputPin createInputPinFrom(UUID id, String name) {

        InputPin input = this.input_pin_.get(id);
        if ( input == null ) {
            return null;
        }

        String newName = name;
        if ( name.isEmpty() ) {
            //locate input pins starting with same name as in the input
            //found by searching for id.
            newName = input.getName() + " " + this.input_pin_.values().stream()
                    .filter( p -> p.getName().startsWith(input.getName()))
                    .map( p-> getName() )
                    .count();
        }

        InputPin pin = new InputPin(this, UUID.randomUUID(),
                newName,
                input.getConnectionRequired(),
                input.getPinTypes(),
                input.shouldBlockComponent(),
                input.ignoreComponentStatus());

        return createInputPin(pin);
    }

    // -------------------------------------------------

    private IOutputPin createOutputPin(OutputPin pin) {
        try {

            if ( pin == null ) {
                return null;
            }

            if (this.output_pin_ == null) {
                this.output_pin_ = new LinkedHashMap<>();
            }

            //Adds a instance of pin into reference list
            this.output_pin_.put(pin.getUUID(), pin);

            //Adds a copy of pins data
            this.outputPins.put(pin.getUUID(), pin);

            //Update component height according to the number of base pin types
            this.updateComponentHeight();

            this.numberOfPins = getNumberOfInstancedPins();

            /*if ( this.job_ != null && this.dispatchEvents) {
                this.job_.onCreatePin(this.getId(), PinInfo.copy(pin));
            }*/

            return pin;
        }
        catch(Exception e) {
            return null;
        }
    }

    public boolean createOutputPin(PinInfo pin)
    {
        try {

            if ( pin == null ) {
                return false;
            }

            OutputPin newPin = OutputPin.create(this, pin);

            if ( this.createOutputPin(newPin) == null ) {
                return false;
            }
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    public IOutputPin createOutputPin(OutputPin model, int sequencial)
    {
        OutputPin pin = new OutputPin(this, model, UUID.randomUUID(), sequencial);

        return this.createOutputPin(pin);
    }

    public IOutputPin createOutputPin(UUID uuid, String name)
    {
        OutputPin pin = new OutputPin(this, uuid, name);

        return this.createOutputPin(pin);
    }

    public IOutputPin createOutputPin(UUID uuid, String name, List<PinType> defaultPinTypes)
    {

        OutputPin pin = new OutputPin(this, uuid, name, defaultPinTypes);

        return this.createOutputPin(pin);
    }

    public OutputPin getOutputPin(UUID uuid)
    {
        if ( this.output_pin_ == null ) {
            return null;
        }

        OutputPin outPin = this.output_pin_.get(uuid);
        if ( outPin == null ) {
            return null;
        }

        return outPin;
    }

    public InputPin getInputPin(UUID uuid)
    {
        if ( this.input_pin_ == null ) {
            return null;
        }

        InputPin inPin = this.input_pin_.get(uuid);
        if ( inPin == null ) {
            return null;
        }

        return inPin;
    }

    private int getParentInputPinCount(Map<UUID, InputPin> pinList) {

        int count = 0;

        List<UUID> ids = new ArrayList<>(pinList.keySet());
        for (UUID uuid : ids) {
            Pin pin = pinList.get(uuid);
            if (!pin.isChildren()) {
                count++;
            }
        }

        return count;
    }

    private int getParentOutputPinCount(Map<UUID, OutputPin> pinList) {

        int count = 0;

        List<UUID> ids = new ArrayList<>(pinList.keySet());
        for (UUID uuid : ids) {
            Pin pin = pinList.get(uuid);
            if (!pin.isChildren()) {
                count++;
            }
        }

        return count;
    }

    //Update component height according to the number of base pin types
    private void updateComponentHeight()
    {
        int max = getParentInputPinCount(this.input_pin_);
        int temp = getParentOutputPinCount(this.output_pin_);
        if ( temp > max) {
            max  = temp;
        }

        int heightInPixels = max * 14 + 9;
        if ( heightInPixels > this.height /*current component height*/ ) {
            this.height = heightInPixels;
        }
    }

    /*public OutputPin createEquivalentOutputPin(UUID uuid)
    {
       OutputPin newOutputPin = OutputPin.createEquivalentOutputPin(this, this.output_pin_, this.getOutputPin(uuid));
       if (  newOutputPin == null ) {
           return null;
       }

        RxJavaUtils.safeOnNext(this.subjectComponentOutputPins, Info.create(this.getId(), this.getOutputPinInstances()));

       return newOutputPin;
    }*/

    /*public OutputPin getNextAvailableOutputPin(UUID uuid)
    {
        return OutputPin.getNextAvailableOutputPin(this, this.output_pin_, getOutputPin(uuid));
    }*/

    public boolean runInternal(InputPin pin)
    {
        try {
            ThreadUtils.lock(this.lock);

            try {

                if (this.getStatus() == TASK_ABORTING ||
                    this.getStatus() == TASK_ABORTED ) {
                    //Returns true because this is not a failure,
                    //but a command requested by the operator / system.
                    return true;
                }
                else if ( this.getStatus() == TASK_FAILED ) {
                    //However if this component failed, the upstream must be notified that
                    //something down here is incorrect and abort any new attempt to send us data.
                    return false;
                }

                //Verifies component property to prevent the thread to get started.
                if (this.input_pin_ != null && this.input_pin_.size() > 0 &&
                        this.property_ == TASK_EXPECTS_ALL_THE_INPUT_PINS) {
                    //TODO:  Checks if there is a pin not ready to allow thread to continue
                    //       its own execution
                    if (this.input_pin_.values()
                            .stream()
                            .filter(p -> !p.isReady())
                            .filter(p -> p != pin)
                            .findAny()
                            .orElse(null) != null) {
                        return true;
                    }
                }

                //Resets all the output pins (connected or not, don't care)
                //We do this to allow the Job controller identify
                //whether the workflow is done or not (see do_internal_consume method).
                this.resetOutputPins();

                //Force progress to get updated (in case user implementation missed that)
                this.setProgress(0);

                //set this task now as running!
                this.setStatus(TASK_RUNNING);

                //starts the thread
                startThread();

                return true;
            }
            finally {
                ThreadUtils.unlock(this.lock);
            }
        }
        catch (Exception e) {
            //Internal Exception (UNKNOWN error)
            end_internal(TASK_FAILED);
            this.error(e.getMessage());

            return false;
        }


    }

    private void startThread()
    {
        try {

            //Sets an alternative (run) method, creates the thread and starts it!
            Runnable runnable = () -> do_internal_consume();

            String threadName = this.getName().replace(" ","_") + "_Thread";

            this.thread_ = new Thread(runnable,threadName);
            this.thread_.start();

        }
        catch (Exception e) {
            //Internal Exception (UNKNOWN error)
            end_internal(TASK_FAILED);
            this.error(e.getMessage());

            return;
        }
    }

    private void resetOutputPins() {
        if ( this.output_pin_ == null ) {
            return;
        }

        this.output_pin_.values().forEach(OutputPin::reset);
    }

    private void resetInputPins() {
        if ( this.input_pin_ == null ) {
            return;
        }

        this.input_pin_.values().forEach(InputPin::reset);
    }

    @Override
    public void setStatus(final ComponentStatus status)
    {
        super.setStatus(status);

        /*if ( this.job_ != null ) {
            this.job_.onComponentStatusChanged(this.getId(), status);
            this.job_.onComponentChanged(this.getId(), this.getName(), this.getPluginInfo().getId(), this.getPluginInfo().getName(), this.getStatus(), this.getProgress());
        }*/
    }

    public void setProgress(final Integer progress)
    {
        super.setProgress(progress > 100 ? 100 : progress < 0 ? 0 : progress);

        /*if ( this.job_ != null &&
            this.old_progress != this.progress) {

            //System.out.println(this.getName() + " progress: " + this.getProgress());

            this.job_.onComponentProgressChanged(this.getId(), this.getProgress());
            this.job_.onComponentChanged(this.getId(), this.getName(), this.getPluginInfo().getId(), this.getPluginInfo().getName(), this.getStatus(), this.getProgress());
        }*/

        this.old_progress = this.progress;

    }

    public Pin getPin(UUID pinId) {

        if (this.input_pin_ == null &&
            this.output_pin_ == null) {
            return null;
        }

        Pin pin = this.input_pin_.values().stream()
                            .filter( p -> p.getUUID().equals(pinId))
                            .findAny()
                            .orElse(null);

        if ( pin != null ) {
            return pin;
        }

        pin = this.output_pin_.values().stream()
                .filter( p -> p.getUUID().equals(pinId))
                .findAny()
                .orElse(null);

        return pin;
    }

    public Pin getPin(PinDirection pindir, int pos)
    {
        if (pindir == PINDIR_INPUT)
        {

            if (this.input_pin_ == null ||
                this.input_pin_.size() <= pos) {
                return null;
            }

            return this.input_pin_.get(pos-1);
        }

        if (this.output_pin_ == null ||
            this.output_pin_.size() <= pos) {
            return null;
        }

        return this.output_pin_.get(pos-1);
    }

    public void addPin(Pin pin) {
        if (pin.getPinDirection() == PINDIR_INPUT) {
            if( this.input_pin_ == null ) {
                this.input_pin_ = new LinkedHashMap<>();
            }
            input_pin_.put(pin.getUUID(), (InputPin) pin);
        } else {
            if( this.output_pin_ == null ) {
                this.output_pin_ = new LinkedHashMap<>();
            }
            output_pin_.put(pin.getUUID(), (OutputPin) pin);
        }
    }

    /*public void removePin(Pin pin) {
        if (pin.getPinDirection() == PINDIR_INPUT) {
            if( this.input_pin_ == null ) {
                return;
            }
            input_pin_.remove((InputPin) pin);
        } else {
            if( this.output_pin_ == null ) {
                return;
            }
            output_pin_.remove((OutputPin) pin);
        }
    }*/

    public void deleteInputPin(UUID pinId, boolean notify) {

        if( this.input_pin_ == null ) {
            return;
        }

        InputPin removedPin = this.input_pin_.remove(pinId);
        if ( removedPin == null ) {
            return;
        }

        removedPin.dispose(notify);
    }

    public void deleteOutputPin(UUID pinId, boolean notify) {

        if( this.output_pin_ == null ) {
            return;
        }

        OutputPin removedPin = this.output_pin_.remove(pinId);
        if ( removedPin == null ) {
            return;
        }

        removedPin.dispose(notify);
    }

    public Map<UUID, InputPin> getInputPinInstances() {

        if( this.input_pin_ == null ) {
            this.input_pin_ = new LinkedHashMap<>();
        }

        return this.input_pin_;
    }

    public Map<UUID, OutputPin> getOutputPinInstances() {

        if( this.output_pin_ == null ) {
            this.output_pin_ = new LinkedHashMap<>();
        }

        return this.output_pin_;
    }

    public Integer getNumberOfInstancedPins() {
        int total = 0;

        if( this.input_pin_ != null ) {
            total += input_pin_.size();
        }
        if ( this.output_pin_ != null ) {
            total += output_pin_.size();
        }

        return total;
    }

    public Set<UUID> getDisconnectedInputPinIds()
    {
        if ( this.input_pin_ == null ) {
            return null;
        }
        return this.input_pin_.entrySet().stream()
                .filter( p -> !p.getValue().isConnected())
                .map( Map.Entry::getKey )
                .collect(Collectors.toSet());
    }

    public List<InputPin> getDisconnectedInputPins()
    {
        //If the plugin has no inputpin, it should return null.
        if ( this.input_pin_ == null || this.input_pin_.size() == 0 ) {
            return null;
        }
        return this.input_pin_.values().stream()
                .filter( p -> !p.isConnected())
                .collect(Collectors.toList());
    }

    public boolean disconnect()
    {
        try {
            if (this.input_pin_ != null) {
                this.input_pin_.values().forEach(pin -> {
                    pin.disconnect();
                });
            }

            if (this.output_pin_ != null) {
                this.output_pin_.values().forEach(pin -> {
                    pin.disconnect();
                });
            }

            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    public void onCompleteConnect(PinInfo pin, PinConnection pinConnection) {
        /*if ( this.job_ != null ) {
            this.job_.onCompleteConnect(this.getId(), pin, pinConnection);
        }*/
    }

    public void onConnectionUpdated(PinConnection pinConnection) {
       /* if ( this.job_ != null ) {
            this.job_.onConnectionUpdated(pinConnection);
        }*/
    }

    public void onCompleteDisconnect(PinInfo pin) {
       /* if ( this.job_ != null ) {
            this.job_.onCompleteDisconnect(this.getId(), pin);
        }*/
    }

    /*public rx.Observable<Info<UUID, Integer>> getComponentProgressObservable()
    {
        return this.subjectComponentProgress.asObservable();
    }

    public rx.Observable<Info<UUID, ComponentStatus>> getComponentStatusObservable()
    {
        return this.subjectComponentStatus.asObservable();
    }

    public rx.Observable<Info<UUID, Profile>> getComponentProfileObservable()
    {
        return this.subjectComponentProfile.asObservable();
    }

    public rx.Observable<Info<UUID, List<InputPin>>> getComponentInputPinsObservable()
    {
        return this.subjectComponentInputPins.asObservable();
    }

    public rx.Observable<Info<UUID, List<OutputPin>>> getComponentOutputPinsObservable()
    {
        return this.subjectComponentOutputPins.asObservable();
    }*/

    //Called from input/output pins while they're disposed.
    public void onRemovePin(PinInfo pin) {
        /*if ( this.job_ != null ) {
            this.job_.onDeletePin(this.getId(), pin);
        }*/
    }

    // ------------------ Plugin Log Messages--------------------------
    public void information(String message) {
        /*if (this.job_ != null) {
            this.job_.onInformation(this.getId(), message, this.getName() + " (" + this.getPluginName() + ")");
        }*/
    }

    public void warning(String message) {
        /*if (this.job_ != null) {
            this.job_.onWarning(this.getId(), message, this.getName() + " (" + this.getPluginName() + ")");
        }*/
    }

    public void error(String message) {
        /*if (this.job_ != null) {
            this.job_.onError(this.getId(), message, this.getName() + " (" + this.getPluginName() + ")");
        }*/
    }

    // workflow-end analysis
    public ComponentStatus runAnalysis() {

        if ( super.getStatus() != TASK_COMPLETE ||
             this.getOutputPinInstances() == null ||
             this.getOutputPinInstances().size() == 0) {
            //My status is none of those so-called "finished" status, so I don't need go further to figure out the
            //workflow hasn't finished yet.
            return super.getStatus();
        }

        //My status (COMPLETE) urges me to ask downstream components for checking sub components.
        final MyHolder<ComponentStatus> MyHolder = new MyHolder<>();
        MyHolder.value = TASK_COMPLETE;

        //For each output pin (connected and having data sent flag true) get an interface to analyze
        //the sub component status as I did myself.
        this.getOutputPinInstances().values().stream().filter(pin -> pin.isConnected() && pin.isDispatched())
                    .map(Pin::getConnectedPin)
                    .filter(Objects::nonNull).anyMatch( pin -> {

            ComponentStatus ret = pin.runAnalysis();

            if ( ret == TASK_FAILED ||
                 ret == TASK_ABORTED ) {
                MyHolder.value = ret;
                return true;
            } else if ( ret == TASK_RUNNING ||
                        ret == TASK_IDLE ) {
                MyHolder.value = ret;
            }

            return false;
        });

        return MyHolder.value;
    }

    @Override
    public IInputPin getInputPin(String name) {
        if ( this.input_pin_ == null ) {
            return null;
        }

        return this.input_pin_.values()
                .stream()
                .filter( p -> p.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }

    @Override
    public IOutputPin getOutputPin(String name) {
        if ( this.output_pin_ == null ) {
            return null;
        }

        return this.output_pin_.values()
                .stream()
                .filter( p -> p.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }

    public void onBeforeQueryProfile(IProfile customProfile) {

    }

    public void onReceive(IInputPin pin) {

    }

    //Searches for a specific plugin based upon the type specified.
    /*public IComponentControl findPluginPassThru(IOutputPin pin, PluginType type) {
        return this.findPluginPassThru(pin, type, false);
    }*/

    public IComponentControl findPluginPassThru(IOutputPin pin, PluginType type, boolean skip) {

        if ( this.getType().equals(type)) {

            //This is the plugin itself, we may return with its reference.
            return this;
        }

        //This is not the current plugin try the upstream plugins.
        return this.getInputPinInstances()
                .values()
                .stream()
                .filter(Pin::isConnected)
                .map(p-> p.findPluginPassThru(type, skip))
                .findFirst()
                .orElse(null);


    }

    public IPin getInputPinByType(PinType type) {

        return this.getInputPinInstances().values().stream()
                .filter( p -> p.getPinTypes().stream()
                        .filter( p2 -> p2.equals(type))
                        .findAny()
                        .orElse(null) != null)
                .findAny()
                .orElse(null);
    }

    public IPin getOutputPinByType(PinType type) {

        return this.getOutputPinInstances().values().stream()
                .filter( p -> p.getPinTypes().stream()
                        .filter( p2 -> p2.equals(type))
                        .findAny()
                        .orElse(null) != null)
                .findAny()
                .orElse(null);
    }

    public IPin getPinByType(PinDirection dir, PinType type) {

        if ( dir == PINDIR_INPUT ) {
            return this.getInputPinByType(type);
        } else if ( dir == PINDIR_OUTPUT ) {
            return this.getOutputPinByType(type);
        }
        else {
            return null;
        }
    }

    public IPin getPinByType(PinType type) {

        IPin pin = this.getInputPinByType(type);
        if ( pin == null ) {
            return this.getOutputPinByType(type);
        }

        return pin;
    }

    /*public IJobExecuter getJob() {
        return this.job_;
    }*/

    public void waitForCompletion() {

        try (Pin.Autolock alock = new Pin.Autolock(waitForCompletionLock)) {

            while ( !this.isAbortRequested() &&
                     this.getStatus() == TASK_RUNNING ){

                try {
                    Thread.sleep(250);
                }
                catch(Exception e) {

                }

            }
        }
    }
}
