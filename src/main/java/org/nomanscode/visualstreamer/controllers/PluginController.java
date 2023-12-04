package org.nomanscode.visualstreamer.controllers;

//import org.nomanscode.visualstreamer.RxJavaUtils;
import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.common.ComponentExecuter;
import org.nomanscode.visualstreamer.exceptions.SSEException;
import org.nomanscode.visualstreamer.types.Plugin;
import org.nomanscode.visualstreamer.database.PluginRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.Flux;
//import rx.Subscription;
//import rx.subjects.ReplaySubject;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.stream.Collectors;

@Slf4j
@Controller
public class PluginController extends ClassLoader {

    @Autowired
    LogController logController;

    private Lock lock;

    private Map<UUID, Plugin> plugins_ = new LinkedHashMap<>();

    //private rx.subjects.Subject<Update<UUID, PluginInfo>, Update<UUID, PluginInfo>> subject;
    //private Subscription pluginSubscription;

    @Autowired
    private PluginRepository pluginRepository;

    private CybertronEmitterProcessor<Update<UUID, PluginInfo>> pluginEmitter = CybertronEmitterProcessor.create(false);

    PluginController() {
        lock = new ReentrantLock();
    }

    @PostConstruct
    private void postConstruct() throws Exception {


        /*subject = ReplaySubject.<Update<UUID, PluginInfo>>createWithTime(1, TimeUnit.SECONDS, rx.schedulers.Schedulers.io()).toSerialized();

        rx.Observable<Update<UUID, PluginInfo>> pluginObservable = pluginRepository.getObservable()
                .onBackpressureBuffer()
                .observeOn(rx.schedulers.Schedulers.io());

        pluginSubscription = RxJavaUtils.safeSubscribe("Plugin subscription", pluginObservable, this::processPlugins);
*/

        /*
        //Built-in plugin initializations...
        this.addEntryPlugin();
        this.addDecisionPlugin();
        this.addScriptConditionPlugin();
        this.addForPlugin();
        this.addWhilePlugin();
        this.addEndWhilePlugin();
        this.addWorkflowPlugin();
        this.addWatchFolderPlugin();
        this.addJoinPlugin();
        this.addMediaProcessor();*/
    }

    @PreDestroy
    private void preDestroy() {
        this.plugins_.clear();


        //pluginSubscription.unsubscribe();
        //pluginSubscription = null;
    }

    // --------------- Default Plugin list control ---------------------
    public Plugin findPlugin(UUID pluginId)
    {
        try {
            ThreadUtils.lock(lock);
            try {
                return this.plugins_.get(pluginId);
            }
            finally {
                ThreadUtils.unlock(lock);
            }

        }
        catch(Exception e) {
            return null;
        }
    }

    /*public String getPluginIcon(UUID pluginId)
    {

        Plugin plugin = findPlugin(pluginId);

        if ( plugin == null ) {
            return "";
        }

        return plugin.getIcon();
    }*/

    public UUID getPluginProductId(UUID pluginId, /* nullable */ MyHolder<String> errorMessage)
    {
        PluginInfo ti = findPlugin(pluginId);
        if ( ti == null ) {
            HolderHelper.setHolderValue(errorMessage, "Plugin not found!");
            return null;
        }

        return ti.getProductIdentificationId();
    }

    //
    //
    //Removes the plugin from plugin list.
    //Returns the removed plugin or null
    //
    public PluginInfo removePlugin(UUID pluginId, /* nullable */MyHolder<String> errorMessage)
    {
        try {

            Plugin plugin = this.findPlugin(pluginId);
            if ( plugin == null ) {
                HolderHelper.setHolderValue(errorMessage,"Plugin not found!");
                return null;
            }

            if (!plugin.getDeletable()) {
                //plugin not found!
                HolderHelper.setHolderValue(errorMessage, "This plugin may not be removed!");
                return null;
            }

            //We make a copy of plugin information
            PluginInfo pluginInfo = PluginInfo.copy(plugin);
            if ( pluginInfo == null ) {
                HolderHelper.setHolderValue(errorMessage,"Plugin not found!");
                return null;
            }

            //There is no component using this plugin,
            //so we can instruct repository to kill plugin.
            //Plugin will be removed from plugin list but
            //our plugin info (copied before will base safe)
            if( !pluginRepository.del(pluginId) ) {
                //database failure
                HolderHelper.setHolderValue(errorMessage, "Error while removing plugin from database!");
                return null;
            }

            return pluginInfo;

        } catch (Exception e) {
            e.printStackTrace();
            HolderHelper.setHolderValue(errorMessage, "Error while removing plugin. Error reason: " + e.getMessage());
            log.error("PluginController::removePlugin Error: ", e);
            return null;
        }

    }

    //Plugins can have updated fields such as description and name only.
    //No more fields will be updated.
    public PluginInfo updatePlugin(UUID pluginId, PluginRequest request, /*nullable*/ MyHolder<String> errorMessage)
    {
        if ( request == null ) {
            HolderHelper.setHolderValue(errorMessage,"Plugin information is missing!");
            return null;
        }

        PluginInfo current = this.findPlugin(pluginId);
        if ( current == null ) {
            HolderHelper.setHolderValue(errorMessage, "Plugin not found!");
            return null;
        }

        PluginInfo pluginInfo = isPluginNameDuplicated(request.getName());
        if ( pluginInfo != null &&
             !pluginInfo.getId().equals(pluginId) ) {
            HolderHelper.setHolderValue(errorMessage, "Plugin name in use!");
            return null;
        }

        current.setName(request.getName());
        current.setDescription(request.getDescription());
        current.setEnabled(request.getEnabled());

        try {
            return pluginRepository.set(current);
        }
        catch(Exception e)
        {
            HolderHelper.setHolderValue(errorMessage, "Error while updating plugin. Error: " + e.getMessage());
            log.error("PluginController::updatePlugin Error: ", e);
            e.getStackTrace();
            return null;
        }

    }

    //
    //
    //Given the profile set, checks if the plugin accepts it;
    //Responses are true/false.
    //
    public boolean checkProfileChanges(UUID pluginId, Profile profile, /*nullable*/ MyHolder<String> errorMessage)
    {
        try {
            //Locate the plugin
            Plugin plugin = this.findPlugin(pluginId);
            if (plugin == null) {
                //The plugin not found
                HolderHelper.setHolderValue(errorMessage, "Plugin not found!");
                return false;
            }

            //Tries to set the desired profile in base plugin
            if (!plugin.checkProfileChanges(profile, errorMessage)) {
                //Profile not accepted
                HolderHelper.setHolderValue(errorMessage, "Error plugin has returned the following message: " + errorMessage.value);
                return false;
            }

            //Plugin accepted the submitted profile.
            return true;
        }
        catch(Exception e) {
            HolderHelper.setHolderValue(errorMessage, "Error while updating checking profile. Error: " + e.getMessage());
            log.error("PluginController::updatePlugin Error: ", e);
            return false;
        }
    }

    /*public UUID getSpecificPluginProductId(String path)
    {
        try {
            Plugin newPlugin = new Plugin(path);

            if (!newPlugin.isReady()) {
                return null; //Plugin did not load , so we're going to
                //discard it.
            }

            return newPlugin.getProductIdentificationId();
        }
        catch(Exception e)
        {
            return null;
        }
    }*/

    //
    //
    //Instantiates a new plugin without merging any other data
    //Returns the newly created plugin reference or null.
    //
    public Plugin getNewPlugin(String path, MyHolder<String> errorMessage)
    {
        Plugin plugin = null;
        try {
            if (path == null || path.isEmpty()) {
                HolderHelper.setHolderValue(errorMessage, "Plugin path is missing!");
                return null;
            }

            plugin = new Plugin(path);

            if (!plugin.isReady()) {
                HolderHelper.setHolderValue(errorMessage, "The plugin is not compliant with Glookast CYBERTRON!");
                log.warn("Plugin " + plugin.getPath() + " is not compliant and won't be used!!");
                return null;
            }

            return plugin;
        }
        catch(Exception e)
        {
            if ( plugin != null ) {
                plugin.dispose();
                plugin = null;
            }

            HolderHelper.setHolderValue(errorMessage, "Error while getting new plugin. Error: " + e.getMessage());
            log.error("PluginController::getNewPlugin: Error: ", e);
            e.printStackTrace();
            return null;
        }
    }

    //
    //
    //Instantiates a new plugin based on path field and updates
    //Name, Description and Enabled/Disabled fields during
    //the instantiation.
    //Returns a new reference to the plugin or null.
    //
    public Plugin getNewPlugin(PluginRequest request, MyHolder<String> errorMessage)
    {
        try {
            if (request == null) {
                HolderHelper.setHolderValue(errorMessage, "Not enough information to get a new Plugin!");
                return null;
            }

            if (request.getPath() == null ||
                    request.getPath().isEmpty()) {
                HolderHelper.setHolderValue(errorMessage, "Plugin path is missing!");
                return null;
            }

            Plugin plugin = Plugin.create(request);

            if (!plugin.isReady()) {
                HolderHelper.setHolderValue(errorMessage, "The plugin is not compliant with Glookast CYBERTRON!");
                log.warn("Plugin " + request.getPath() + " is not compliant and won't be used!!");
                return null;
            }

            return plugin;
        }
        catch(Exception e) {
            HolderHelper.setHolderValue(errorMessage, "Error while getting new plugin. Error: " + e.getMessage());
            log.error("PluginController::getNewPlugin: Error: ", e);
            e.printStackTrace();
            return null;
        }
    }

    private Plugin isPluginIdInPluginList(UUID id, boolean useLock) {
        try {

            if ( useLock ) {
                ThreadUtils.lock(this.lock);
            }

            try {
                return this.plugins_.entrySet()
                        .stream()
                        .filter(k -> k != null && k.getKey() == id)
                        .map(Map.Entry::getValue)
                        .findAny()
                        .orElse(null);
            }
            finally {
                if ( useLock ) {
                    ThreadUtils.unlock(this.lock);
                }
            }
        }
        catch(Exception e) {
            return null;
        }

    }

    public PluginInfo checkPlugin(String path, MyHolder<String> errorMessage) {

        Plugin plugin = null;
        try {

            //Check if the file is a valid plugin
            plugin = this.getNewPlugin(path, errorMessage);
            if (plugin == null) {
                return null;
            }

            return PluginInfo.copy(plugin);

        }
        catch(Exception e) {
            HolderHelper.setHolderValue(errorMessage, e.getMessage());
            return null;
        }
        finally {
            if ( plugin != null) {
                plugin.dispose();
            }
        }

    }
    //
    //
    //Checks whether there is a plugin already loaded with same name,
    //path or product id.
    //Returns a plugin reference or null
    private PluginInfo isPluginDuplicated(Plugin plugin)
    {
       return isPluginDuplicated(plugin, true);
    }

    private PluginInfo isPluginDuplicated(Plugin plugin, boolean useLock)
    {
        try {
            if ( useLock ) {
                ThreadUtils.lock(lock);
            }

            //check for duplicated name or path
            return this.plugins_.values().stream()
                    .filter(t -> t != null &&
                            (t.getName().equalsIgnoreCase(plugin.getName()) ||
                                    t.getPath().equalsIgnoreCase(plugin.getPath()) ||
                                    t.getPluginInfo().getProductIdentificationId().equals(plugin.getProductIdentificationId())))
                    .findAny()
                    .orElse(null);
        }
        catch(Exception e)
        {
            e.getStackTrace();
            return null;
        }
        finally {
            if ( useLock ) {
                ThreadUtils.unlock(lock);
            }
        }
    }

    //
    //
    //Checks whether there is a plugin already loaded with same name only!
    //Returns a plugin reference or null
    private PluginInfo isPluginNameDuplicated(String name)
    {
        return isPluginNameDuplicated(name, true);
    }

    private PluginInfo isPluginNameDuplicated(String name, boolean useLock)
    {
        try {
            if ( useLock ) {
                ThreadUtils.lock(lock);
            }

            //check for duplicated name or path
            return this.plugins_.values().stream()
                    .filter(t -> t != null &&
                            (t.getName().equalsIgnoreCase(name)))
                    .findAny()
                    .orElse(null);
        }
        catch(Exception e)
        {
            e.getStackTrace();
            return null;
        }
        finally {
            if ( useLock ) {
                ThreadUtils.unlock(lock);
            }
        }
    }

    //
    //
    //Processes a given pluginInfo to instantiate a new plugin
    //and adds it into plugin list (through AddPlugin method).
    //Returns a plugin reference or null.
    //
    public PluginInfo addPlugin(PluginRequest request, MyHolder<String> errorMessage)
    {
        Plugin plugin = null;

        try {
            plugin = getNewPlugin(request, errorMessage);
            if ( plugin == null ) {
                return null;
            }

            PluginInfo info = addPlugin(plugin, errorMessage);
            if ( info == null ) {
                plugin.dispose();
                return null;
            }

            return info;
        }
        catch(Exception e)
        {
            HolderHelper.setHolderValue(errorMessage, "Error while getting new plugin. Error: " + e.getMessage());
            log.error("PluginController::addPlugin: Error: ", e);

            if ( plugin != null ) {
                plugin.dispose();
            }
            return null;
        }
    }

    //
    //
    //Adds a given plugin (already instantiated) to
    //plugin list, except in scenario where the plugin
    //name, path or product id already exists in plugin list.
    //Returns a plugin reference or null.
    //
    public PluginInfo addPlugin(Plugin plugin, MyHolder<String> errorMessage)
    {
        if ( plugin == null )
        {
            HolderHelper.setHolderValue(errorMessage, "Plugin data is missing!");
            return null;
        }

        //Checks possible plugin duplication attempt based
        //on Name, path or product Id attributes.
        PluginInfo found = isPluginDuplicated(plugin);
        if ( found != null ) {
            HolderHelper.setHolderValue(errorMessage, "Plugin name or the plugin itself already exists in the system!");
            return null;
        }

        //
        //
        //Since our plugin JAR file is loaded in memory, we need to prevent
        //that another instance is created. To do so, we add the newly created
        //plugin in the list and then insert it in the repository. Repository
        //will, when time comes, to try to update our plugin list. That try will
        //be discarded because our list already has our plugin id.
        try {

            ThreadUtils.lock(lock);

            //We add our newly created plugin
            this.plugins_.put( plugin.getId(), plugin);

            //Updates the repository, but the new plugin will be instantiated.
            try {
                pluginRepository.set(plugin);
            }
            catch(Exception e) {
                //Failed to add plugin in repository.
                //We need to remove plugin from plugin list;

                this.plugins_.remove(plugin.getId());

                HolderHelper.setHolderValue(errorMessage, "Error while recording plugin into database!");
                log.error("PluginController::addPlugin: Error: ", e);
                return null;
            }

            //Returns created plugin
            return plugin;

        }
        catch(Exception e)
        {
            HolderHelper.setHolderValue(errorMessage, "Error while adding a new plugin. Error: " + e.getMessage());
            log.error("PluginController::addPlugin: Error: ", e);
            return null;
        }
        finally {
            ThreadUtils.unlock(lock);
        }
    }

    //
    //
    //
    //
    //
    //
    //
    private void processPlugins(Update<UUID, PluginInfo> u)
    {
        try {
            ThreadUtils.lock(lock);

            Plugin plugin = null;

            try {
                switch ( u.type ) {
                    case Set:

                        //Check if plugin list contains plugin
                        plugin = this.isPluginIdInPluginList(u.value.getId(), false);
                        if ( plugin == null ) {

                            plugin = Plugin.create(u.value);

                            if (plugin.isReady() == false) {
                                //this plugin is not working well... We need to discard it.
                                //TODO: Should I ignore, disable or remove it!?
                                log.error("Plugin " + plugin.getName() + " cannot be loaded due to constraints errors");
                                return;
                            }

                            //Check if new plugin will have a duplicated entry
                            if ( this.isPluginDuplicated(plugin, false) != null ) {
                                log.error("Plugin " + plugin.getName() + " has another entry and will be not loaded into the system!");
                                return;
                            }

                            this.plugins_.put(u.key, plugin);

                        }
                        else {
                            //Update plugin changeable values
                            plugin.setName(u.value.getName());
                            plugin.setDescription(u.value.getDescription());
                            plugin.setEnabled(u.value.getEnabled());
                        }

                        //RxJavaUtils.safeOnNext(subject, new Update<>(u.type, u.key, PluginInfo.copy(plugin)));

                        break;
                    case Delete:

                        plugin = this.plugins_.remove(u.key);
                        if ( plugin != null ) {
                            plugin.dispose();
                        }

                        //RxJavaUtils.safeOnNext(subject, new Update<>(u.type, u.key, u.value));
                        break;
                }

                this.pluginEmitter.onNext( u );

            }
            catch(Exception e) {
                log.error("PluginController::processPlugins (inner): Error:", e);
            }
            finally {
                ThreadUtils.unlock(lock);
            }
        } catch (InterruptedException ex) {
            log.error("PluginController::processPlugins: Error:", ex);
        }
    }

    //
    //
    //Get the plugin list otherwise null
    //
    //
    public Map<UUID, PluginInfo> getPlugins(MyHolder<String> errorMessage)
    {
        try {
            ThreadUtils.lock(lock);

            try {
                return this.plugins_.values()
                        .stream()
                        .collect(Collectors.toMap(PluginInfo::getId, PluginInfo::copy));
            }
            finally {
                    ThreadUtils.unlock(lock);
            }

        }
        catch(Exception e)
        {
            HolderHelper.setHolderValue(errorMessage, "Error while listing plugins. Error: " + e.getMessage());
            log.error("PluginController::getPlugins: Error: ", e);
            e.getStackTrace();
            return null;
        }

    }

    // ------------------ FLUX ----------------------------

    public Flux<Update<UUID, PluginInfo>> getPluginsFlux() {
        try {
            return Flux.defer(this::createPluginListFlux)
                    .concatWith(this.pluginEmitter.getProcessor())
                    .onBackpressureBuffer()
                    .doOnComplete(() -> log.warn("Plugins: Complete"))
                    .doOnCancel(() -> log.warn("Plugins: Cancel"));
        }
        catch(Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }

    private Flux<Update<UUID, PluginInfo>> createPluginListFlux()
    {
        try {
            ThreadUtils.lock(this.lock);
            try {
                return Flux.fromIterable(this.plugins_.entrySet()
                        .stream()
                        .map(e -> new Update<UUID,PluginInfo>(Update.Type.Set, e.getKey(), PluginInfo.copy(e.getValue())))
                        .collect(Collectors.toList()));
            }
            finally {
                ThreadUtils.unlock(this.lock);
            }
        }
        catch (Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }
    // --------------------------- OBSERVABLES ----------------------------------

    /*public rx.Observable<Update<UUID, PluginInfo>> getObservable()
    {
        return rx.Observable.defer(this::createObservable)
                .concatWith(subject);
    }

    private rx.Observable<Update<UUID, PluginInfo>> createObservable()
    {
        try {
            ThreadUtils.lock(lock);
            try {
                return rx.Observable.from(plugins_.entrySet()
                        .stream()
                        .map(e -> new Update<>(Update.Type.Set, e.getKey(), PluginInfo.copy(e.getValue().getPluginInfo())))
                        .collect(Collectors.toList()));
            } finally {
                ThreadUtils.unlock(lock);
            }
        } catch (Throwable t) {
            return rx.Observable.error(t);
        }
    }*/

    // ---------------------------------------------------

    private boolean addBuiltInPlugin(UUID id, ComponentExecuter instance) {
        try {
            ThreadUtils.lock(this.lock);
            try {

                //Need to keep same Id (because usually EntryComponent (thru ComponentExecutor generates its own Id).
                instance.setId(id);

                Plugin plugin = Plugin.create(instance, true, false);
                if ( plugin == null ) {
                    log.error("Failed to load Built-In Plugin");
                    return false;
                }

                this.plugins_.put(plugin.getId(), plugin);

                Update<UUID, PluginInfo> u = new Update<>(Update.Type.Set, plugin.getId(), PluginInfo.copy(plugin));
                //RxJavaUtils.safeOnNext(this.subject, u);
                this.pluginEmitter.onNext( u );

                return true;
            } finally {
                ThreadUtils.unlock(this.lock);
            }
        } catch (Exception e) {
            log.error("PluginController:addBuiltInPlugin error: " + e.getMessage());
            return false;
        }
    }

    //Built-in plugin initializations------------------------------

    /*private boolean addEntryPlugin() {
        return this.addBuiltInPlugin(EntryComponent.GK_ENTRY_TASK_PLUGIN_ID,
                                     new EntryComponent(null, null));
    }

    private boolean addDecisionPlugin() {
        return this.addBuiltInPlugin(DecisionComponent.GK_DECISION_TASK_PLUGIN_ID,
                new DecisionComponent(null, null));
    }

    private boolean addScriptConditionPlugin() {
        return this.addBuiltInPlugin(ScriptConditionComponent.GK_SCRIPT_DECISION_TASK_PLUGIN_ID,
                new ScriptConditionComponent(null, null));
    }

    private boolean addForPlugin() {
        return this.addBuiltInPlugin(ForComponent.GK_FOR_TASK_PLUGIN_ID,
                new ForComponent(null, null));
    }

    private boolean addWhilePlugin() {
        return this.addBuiltInPlugin(WhileComponent.GK_WHILE_TASK_PLUGIN_ID,
                new WhileComponent(null, null));
    }

    private boolean addEndWhilePlugin() {
        return this.addBuiltInPlugin(EndWhileComponent.GK_END_WHILE_TASK_PLUGIN_ID,
                new EndWhileComponent(null, null));
    }

    private boolean addWorkflowPlugin() {
        return this.addBuiltInPlugin(RunComponent.GK_RUN_TASK_PLUGIN_ID,
                new RunComponent(null, null));
    }

    private boolean addWatchFolderPlugin() {
        return this.addBuiltInPlugin(WatchFolderComponent.GK_WATCH_FOLDER_TASK_PLUGIN_ID,
                new WatchFolderComponent(null, null));
    }

    private boolean addJoinPlugin() {
        return this.addBuiltInPlugin(JoinComponent.GK_JOIN_TASK_PLUGIN_ID,
                new JoinComponent(null, null));
    }

    private boolean addMediaProcessor() {
        return this.addBuiltInPlugin(MediaProcessor.GK_MEDIA_PROCESSOR_PLUGIN_ID,
                new MediaProcessor(null, null));
    }*/
}
