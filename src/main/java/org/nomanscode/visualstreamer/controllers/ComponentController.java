package org.nomanscode.visualstreamer.controllers;

import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.database.*;
import org.nomanscode.visualstreamer.exceptions.SSEException;
import org.nomanscode.visualstreamer.rest.ServerResponse;
import org.nomanscode.visualstreamer.types.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
//import rx.Subscription;
//import rx.subjects.ReplaySubject;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
//import javax.xml.ws.Holder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class ComponentController {

    @Autowired
    LogController logController;

    private Lock lock;

    private Map<UUID, Component> components_ = new LinkedHashMap<>();

    //private rx.subjects.Subject<Update<UUID, ComponentInfo>, Update<UUID, ComponentInfo>> subject;
    //private Subscription componentSubscription;
    //private Subscription pluginSubscription;

    @Autowired
    private PluginController pluginController;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ComponentPropertyRepository componentPropertyRepository;

    //@Autowired
    private WorkflowController workflowController;

    //@Autowired
    private WorkflowEditorController workflowEditorController;

    //@Autowired
    //private ResourceController resourceController;

    //@Autowired
    //private TemplateController templateController;

    private CybertronEmitterProcessor<Update<UUID, ComponentInfo>> componentEmitter = CybertronEmitterProcessor.create(false);

    ComponentController() {
        lock = new ReentrantLock();
    }

    @PostConstruct
    private void postConstruct() throws Exception {

        /*subject = ReplaySubject.<Update<UUID, ComponentInfo>>createWithTime(1, TimeUnit.SECONDS, rx.schedulers.Schedulers.io()).toSerialized();

        rx.Observable<Update<UUID, ComponentInfo>> componentObservable = componentRepository.getObservable()
                .onBackpressureBuffer()
                .observeOn(rx.schedulers.Schedulers.io());

        componentSubscription = RxJavaUtils.safeSubscribe("Component subscription", componentObservable, this::processComponents);

        rx.Observable<Update<UUID, PluginInfo>> pluginObservable = pluginController.getObservable()
                .onBackpressureBuffer()
                .observeOn(rx.schedulers.Schedulers.io());

        pluginSubscription = RxJavaUtils.safeSubscribe("Plugin subscription", pluginObservable, this::processPluginChanges);
*/
    }

    @PreDestroy
    private void preDestroy() {
        /*componentSubscription.unsubscribe();
        componentSubscription = null;

        pluginSubscription.unsubscribe();
        pluginSubscription = null;*/

        this.components_.clear();

    }


    // ------------------------- TEMPLATE ROUTINES ---------------------------

    //Adds a new plugin and creates a brand new component pointing to it.
    public PluginInfo addPluginAndComponent(PluginRequest request, MyHolder<String> errorMessage) {

        PluginInfo plugin = this.pluginController.addPlugin(request, errorMessage);
        if (plugin == null) {
            return null;
        }

        //CHECK IF PLUGIN HAS ALL NECESSARY INFORMATION TO ALLOW COMPONENT CREATION:
        //THAT WOULD ALLOW US TO CREATE A COMPONENT AT THE SAME TIME THE PLUGIN
        //GETS INSTALLED.
        //REMARKS: DISABLED FOR FUTURE USE
        /*
        MissingDataContainerInfo missing = Profile.getMissingFields(plugin.getId(),
                plugin.getName(),
                plugin.getProfile());

        if (missing != null) {
            //There is at least one field missing. This will prevent the
            //system from creating a component automatically.
            return plugin;
        }*/

        //By default every time a plugin is created a component must be created too.
        //Otherwise user would need to create it manually!
        //However if a plugin hasn't all the information to run at once we shall need
        //to force operator to create the component manually. It depends of the
        //perspective. It is well agreed that a component must be created at once
        //but on the other hand we would like to enforce the necessity of creating
        //a component. Anyway if we want to prevent all components to get created
        //while the plugin is installed we just need to uncomment the above piece
        //of code.

        ComponentRequest componentRequest = new ComponentRequest(plugin,
                                                                 true,
                                                                 request.getViewerIds());

        if (this.addComponent( componentRequest, errorMessage) == null) {
            //Failed but add plugin works just fine!

            //TODO: Just log a warning!
        }

        return plugin;
    }

    //
    //
    //Verifies whether there is a component in component list that makes use
    //of the given plugin. If yes, the routine will fail, otherwise
    //the plugin will be removed.
    //Returns a plugin reference or null.
    //
    public PluginInfo removePlugin(UUID pluginId, /*nullable*/ MyHolder<String> errorMessage) {

        //Get the plugin instance and checks if there are resources defined on it.
        Plugin p = this.pluginController.findPlugin(pluginId);
        if ( p == null ) {
            HolderHelper.setHolderValue(errorMessage, "Module not found!");
            return null;
        }

        /*List<String> resources = new ArrayList<>();
        if ( p.getInterface().getResourceList().stream().anyMatch(res -> {
            List<Resource> list = this.resourceController.getResourcesByPluginResourceId(res.getId());
            if ( list == null || list.size() == 0 ) {
                return false;
            }

            list.stream().forEach(res2 -> {
                resources.add(res2.getName());
            });

            return true;

        }) ) {
            //There is at least one resource linked to this plugin.
            HolderHelper.setHolderValue(errorMessage, "The selected module cannot be removed due to the following resources: " + this.convertToString(resources) + "!");
            return null;
        }*/
         /*


        log.info(res.getName());

        resources.add(new MinimalInfo(res.getName(), res.getDescription()));

        .stream().anyMatch( resource -> {
            List<? extends MinimalInfo> d = this.resourceController.getResourcesByPluginResourceId(resource.getId());

            resources.add(d.get(0));

            return (resources.size() > 0 );
        })) {


        }*/

        Map<UUID, ? extends MinimalInfo> components = this.findComponentsByPluginId(pluginId);
        if (components != null &&
            components.size() > 0) {
            //There are components using this plugin.
            //We can't delete it.

            HolderHelper.setHolderValue(errorMessage,
                    "The selected module cannot be removed due to the following components: " +
                            this.convertToString(components.values().stream().map(MinimalInfo::getName).collect(Collectors.toList())) + "!");
            return null;
        }

        //Checks if there is projects or Active Projects using components in which are based on this plugin.
        /*Map<UUID, String> projects = this.workflowEditorController.findProjectsByPlugin(pluginId);
        if (projects == null) {
            HolderHelper.setHolderValue(errorMessage, "Unable to check if the module is being used in workflow projects!");
            return null;
        }

        if (projects.size() > 0) {
            String workflowList = "";
            for (String value : projects.values()) {
                workflowList = workflowList + value + ", ";
            }
            workflowList = workflowList.substring(0, workflowList.length() - 2);
            HolderHelper.setHolderValue(errorMessage, "This selected nodule is being used by the following workflow projects: " + workflowList + "!");
            return null;
        }*/

        //There is no component using this plugin, let's delete it.
        return pluginController.removePlugin(pluginId, errorMessage);
    }


    //
    //Plugin changes callback. Used for updating PluginInfo structure
    //whenever a plugin gets updated.
    //
    void processPluginChanges(Update<UUID, PluginInfo> u) {
        try {
            ThreadUtils.lock(lock);
            try {
                switch (u.type) {
                    case Set:

                        //We are receiving a notification that a built-in plugin
                        //has been added. We need to add it as a component now.
                        if ( u.value.isBuiltIn() ) {
                            this.addComponent(new ComponentRequest(u.value, true, null), null);
                        }
                        else {
                            this.components_.values()
                                    .stream()
                                    .filter(t -> t.getPluginId().equals(u.key))
                                    .forEach(t2 -> {

                                        t2.setPluginInfo(u.value);

                                        //Emit signal to force workflows to change their component entries
                                        //RxJavaUtils.safeOnNext(subject, new Update<>(Update.Type.Set, t2.getId(), ComponentInfo.copy(t2.getComponentInfo())));
                                    });
                        }
                        break;
                    case Delete:

                        //Whenever a plugin is removed, the associated component must be too.

                        //Creates a temporary list
                        List<UUID> idsToDelete = this.components_.values().stream()
                                                             .filter(t -> t.getPluginId().equals(u.key))
                                                             .map(Component::getId)
                                                             .collect(Collectors.toList());
                        idsToDelete.forEach(id -> {
                            this.components_.remove(id);
                            //RxJavaUtils.safeOnNext(subject, new Update<>(Update.Type.Delete, id, null));
                        });

                        break;
                }
            } finally {
                ThreadUtils.unlock(lock);
            }
        } catch (Exception e) {
            log.error("ComponentController::processPluginChanges: ", e);
        }


    }

    // --------------- COMPONENT ROUTINES -----------------------------------
    //
    //
    //
    //
    //
    //
    //
    public Profile getProfile(UUID componentId, /*nullable*/ MyHolder<String> errorMessage) {
        try {
            //Locates the component
            Component component = this.findComponent(componentId);
            if (component == null) {
                //Component not found
                HolderHelper.setHolderValue(errorMessage, "Component not found!");
                return null;
            }

            //Get component profile
            Profile profile = component.getProfile();
            if (profile == null) {
                //Profile not found, we must get the plugin profile instead.

                Plugin plugin = pluginController.findPlugin(component.getPluginId());
                if (plugin == null) {
                    HolderHelper.setHolderValue(errorMessage, "Error while getting component profile!");
                    return null;
                }

                return plugin.getProfile();
            }

            //return component profile
            return profile;
        } catch (Exception e) {
            HolderHelper.setHolderValue(errorMessage, "Error while getting component profile! Error: " + e.getMessage());
            log.error("ComponentController::getProfile: Error ", e);
            e.getStackTrace();
            return null;
        }
    }

    //
    //
    //
    //
    //
    //
    //
    public Profile setProfile(UUID componentId, Profile profile, /*nullable*/ MyHolder<String> errorMessage) {
        try {
            //Gets the component info to obtain pluginId
            Component component = findComponent(componentId);
            if (component == null) {
                //The component id was not found in component list.
                HolderHelper.setHolderValue(errorMessage, "Component not found!");
                return null;
            }

            //Extracts plugin id
            UUID pluginId = component.getPluginId();

            //Checks if plugin accepts the submitted profile.
            //The routine will fail if the profile is not accepted by the plugin.
            if (!pluginController.checkProfileChanges(pluginId, profile, errorMessage)) {
                //Somehow the plugin has failed in update profile.
                HolderHelper.setHolderValue(errorMessage, "The profile was not accepted by the plugin due to the following error(s): " + errorMessage.value);
                return null;
            }

            //Merge profile structures
            component.mergeProfile(profile);

            //Stores the newly created plugin into detabase
            if (!componentRepository.setComponent(component, errorMessage)) {
                return null;
            }

            //The profile was accepted by the plugin. We can save it on the database.
            /*if (!componentRepository.setProfile(componentId, profile, errorMessage)) {
                return null;
            }*/

            //Returns a copy of the profile.
            return profile;
        } catch (Exception e) {
            HolderHelper.setHolderValue(errorMessage, "Error while setting up a component profile! Error: " + e.getMessage());
            log.error("ComponentController::setProfile: Error ", e);
            e.getStackTrace();
            return null;
        }
    }

    //
    //Gets all the components in component list.
    //Returns the component list or null
    //
    //
    public Map<UUID, ComponentInfo> getComponents(MyHolder<String> errorMessage) {
        try {
            ThreadUtils.lock(lock);

            try {
                return this.components_.values()
                        .stream()
                        .collect(Collectors.toMap(ComponentInfo::getId, ComponentInfo::copy));
            } finally {
                ThreadUtils.unlock(lock);
            }
        } catch (Exception e) {
            HolderHelper.setHolderValue(errorMessage, "Error getting component list. Error: " + e.getMessage());
            e.getStackTrace();
            return null;
        }

    }

    public Map<UUID, ComponentItemInfo> getComponentsAsIcons(String sufix, MyHolder<String> errorMessage) {
        try {
            ThreadUtils.lock(lock);

            try {

                final boolean personal = (sufix != null && sufix.equalsIgnoreCase("TOOLBAR_TYPE_PERSONAL"));
                boolean glookast = ( sufix != null && sufix.equalsIgnoreCase("TOOLBAR_TYPE_GLOOKAST"));

                final Map<UUID, ComponentItemInfo> groups = new LinkedHashMap<>();

                //Adds General group (all components make part of it)
                ComponentItemInfo generalGroup = new ComponentItemInfo(PluginGroupType.PLUGIN_GROUP_TYPE_GENERAL.getFriendlyName(), PluginGroupType.PLUGIN_GROUP_TYPE_GENERAL.getDescription());
                groups.put(generalGroup.getId(), generalGroup);

                ComponentItemInfo builtInGroup = new ComponentItemInfo("Built-in", "System basic modules");
                if ( !personal ) {
                    groups.put(builtInGroup.getId(), builtInGroup);
                }

                this.components_.values().stream()
                        .filter(BasicInfo::getEnabled)
                        .filter(component -> (glookast && component.getPluginInfo().getGroupTypes().contains(PluginGroupType.PLUGIN_GROUP_TYPE_GLOOKAST)) ||
                                             (!glookast && !personal && (component.isPublic())))
                        .forEach(component -> {

                    ComponentInfo info = ComponentInfo.copy(component);

                    component.getPluginInfo().getGroupTypes().forEach(group -> {

                        ComponentItemInfo componentGroup = groups.values().stream()
                                .filter(g -> g.getName().equalsIgnoreCase(group.getFriendlyName()))
                                .findAny()
                                .orElse(null);

                        if (componentGroup == null) {
                            componentGroup = new ComponentItemInfo(group.getFriendlyName(), group.getDescription());
                            groups.put(componentGroup.getId(), componentGroup);
                        }


                        componentGroup.getComponents().put(component.getId(), info);


                    });

                    if (!personal && component.isBuiltIn()) {
                        builtInGroup.getComponents().put(component.getId(), info); //Built in
                    }
                });

                return groups;

            } finally {
                ThreadUtils.unlock(lock);
            }
        } catch (Exception e) {
            HolderHelper.setHolderValue(errorMessage, "Error getting component list. Error: " + e.getMessage());
            e.getStackTrace();
            return null;
        }

    }

    //
    //
    //Locates the component accordingly to a given pluginId.
    //Returns UUID, Component pairs or null.
    //
    public Map<UUID, Component> findComponentsByPluginId(UUID pluginId) {
        try {
            ThreadUtils.lock(lock);

            try {
                return this.components_.entrySet().stream()
                        .filter(t -> t != null &&
                                t.getValue() != null &&
                                t.getValue().getPluginId() != null &&
                                t.getValue().getPluginId().equals(pluginId))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            } finally {
                ThreadUtils.unlock(lock);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //
    //
    //Locates the component accordingly to a given productId.
    //Returns UUID, Component pairs or null.
    //
    public Map<UUID, Component> findComponentsByProductId(UUID productId) {
        try {
            ThreadUtils.lock(lock);

            try {
                Map<UUID, Component> resp = null;

                resp = this.components_.entrySet().stream()
                        .filter(t -> t != null &&
                                t.getValue() != null &&
                                t.getValue().getPluginInfo() != null &&
                                t.getValue().getPluginInfo().getProductIdentificationId() != null &&
                                t.getValue().getPluginInfo().getProductIdentificationId().equals(productId))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                return resp;
            } finally {
                ThreadUtils.unlock(lock);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //
    //
    //Locates the component accordingly to a given Id.
    //Returns a Component reference or null.
    //
    public Component findComponent(UUID componentId) {
        try {
            ThreadUtils.lock(lock);

            try {
                if (!this.components_.containsKey(componentId)) {
                    return null;
                }

                return this.components_.get(componentId);
            } finally {
                ThreadUtils.unlock(lock);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //
    //
    //Locates the component accordingly to a given name.
    //Returns a Component reference or null.
    //
    public Component findComponentByName(String name) {
        try {
            ThreadUtils.lock(lock);
            try {
                Component component = this.components_.values().stream().filter(t -> t != null &&
                        name.equalsIgnoreCase(t.getName()))
                        .findAny()
                        .orElse(null);

                if (component == null) {
                    return null;
                }

                return component;
            } finally {
                ThreadUtils.unlock(lock);
            }
        } catch (Exception e) {
            return null;
        }

    }

    //
    //Adds a component in component list
    //
    //ComponentInfo structure must have at least the following
    //valid fields:
    //              PluginId (required)
    //              Name       (required)
    //              Description(optional)
    //              Profile    (optional)
    //
    public Component addComponent(ComponentRequest request, MyHolder<String> errorMessage) {
        try {

            //Checks if name was supplied
            if (request.getName() == null ||
                    request.getName().isEmpty()) {
                HolderHelper.setHolderValue(errorMessage, "Component name is missing!");
                return null;
            }

            //There is at least one more component with same name
            if (this.findComponentByName(request.getName()) != null) {
                HolderHelper.setHolderValue(errorMessage, "There is a component with same name!");
                return null; //Two components cannot have same name
            }

            //The plugin Id was supplied
            if (request.getPluginId() == null) {
                HolderHelper.setHolderValue(errorMessage, "Plugin Id is missing!");
                return null;
            }

            //Gets the plugin info from the plugin itself and encapsulates it into
            //component plugin structure
            PluginInfo pluginInfo = pluginController.findPlugin(request.getPluginId());
            if (pluginInfo == null) {
                HolderHelper.setHolderValue(errorMessage, "Plugin not found!");
                return null;
            }

            //Creates a new component object and sets some basic information
            //extracted from the given component info object.
            Component newComponent = null;
            if ( pluginInfo.isBuiltIn() ) {
                //Built-in components may not be deleted!
                newComponent = Component.create(request, pluginInfo.getId(), pluginInfo.getDeletable(), pluginInfo.isBuiltIn(), pluginInfo.getIcon(), pluginInfo.getColor());
            } else {
                //Ordinary component being created.
                newComponent = Component.create(request);

            }

            if (newComponent == null) {
                HolderHelper.setHolderValue(errorMessage, "Component could not be created!");
                return null;
            }

            newComponent.setIcon(pluginInfo.getIcon());
            newComponent.setType(pluginInfo.getType());
            newComponent.setGroupTypes(pluginInfo.getGroupTypes());

            //Makes a copy of the plugin information into component.
            newComponent.setPluginInfo(pluginInfo);

            //Makes a copy of the plugin profile into new component profile.
            newComponent.setProfile(pluginInfo.getProfile());

            /*
            //Test Purpose only
            Profile pool = Profile.findResourcePool(newComponent.getProfile());
            if ( pool != null ) {
                ProfileResource res = pool.getResourceValue();
                if ( res != null ) {
                    log.info(res.getResourceList().toString());
                }
            }*/

            //Is profile set?
            if (request.getProfile() != null) {
                //We've got a profile!

                //Creates a copy of the new component to check if
                //profile is acceptable by plugin without compromising
                //current component.
                Component tempComponent = Component.copyComponent(newComponent);

                //Merges the request profile with plugin profile
                tempComponent.mergeProfile(request.getProfile());

                //Checks if merged profile is accepted by the plugin
                if (!pluginController.checkProfileChanges(tempComponent.getPluginId(),
                        tempComponent.getProfile(),
                        errorMessage)) {
                    //Profile was not accepted
                    return null;
                }

                //Copies the checked profile into new component profile
                newComponent.setProfile(tempComponent.getProfile());
            }

            if ( pluginInfo.getType() == PluginType.PLUGIN_TYPE_TASK ) {
                //Since this is an ordinary task plugin stores created component into database.
                if (!componentRepository.setComponent(newComponent, errorMessage)) {
                    return null;
                }
            }
            else {
                //Since this component is derived from a built-in plugin we don't want to save it.
                //The plugin is dynamically generated in the service starting.
                try {
                    ThreadUtils.lock(this.lock);
                    this.components_.put(newComponent.getId(), newComponent);
                }
                finally {
                    ThreadUtils.unlock(this.lock);
                }
            }

            return newComponent;

        } catch (Exception e) {
            HolderHelper.setHolderValue(errorMessage, "Component was not added. Error: " + e.getMessage());
            log.error("ComponentController::addComponent. Error:", e);
            return null;
        }
    }

    //
    //
    //Removes from the database a component of a given component id
    //Returns the removed component or null
    //
    public ComponentInfo removeComponent(UUID componentId, /* nullable */ MyHolder<String> errorMessage) {

        Component component = this.findComponent(componentId);
        if (component == null) {
            //Component not found!
            HolderHelper.setHolderValue(errorMessage, "Component not found!");
            return null;
        }

        if (!component.getDeletable()) {
            //Component not found!
            HolderHelper.setHolderValue(errorMessage, "This component may not be removed!");
            return null;
        }

        try {

            //Check if this component is currently in use for a workflow or workflow project
            Map<UUID, Workflow> workflows = this.workflowController.findWorkflowsByComponentId(componentId);
            if (workflows == null) {
                HolderHelper.setHolderValue(errorMessage, "Unable to check component usage!");
                return null;
            }

            if (workflows.size() > 0) {
                String workflowList = "";
                workflows.values().forEach(w -> workflowList.concat(w.getName()));
                HolderHelper.setHolderValue(errorMessage, "This component is being used in the following workflows published: " + workflowList);
                return null;
            }

            //Removes the component in repository.
            //The reactive programming will erase component entry from the list.
            if (!componentRepository.del(componentId)) {
                HolderHelper.setHolderValue(errorMessage, "Error while removing component from database!");
                return null;
            }

        } catch (Exception e) {
            HolderHelper.setHolderValue(errorMessage, "Error while removing component. Error: " + e.getMessage());
            log.error("ComponentController::removeComponent. Error:", e);
            e.printStackTrace();
            return null;
        }

        return ComponentInfo.copy(component);

    }

    public ComponentInfo updateComponent(UUID componentId, ComponentRequest request, MyHolder<String> errorMessage) {
        try {

            if (request == null) {
                HolderHelper.setHolderValue(errorMessage, "Component information is missing!");
                return null;
            }

            Component current = this.findComponent(componentId);
            if (current == null) {
                HolderHelper.setHolderValue(errorMessage, "Component not found!");
                return null;
            }

            //There is at least one more component with same name
            Component found = this.findComponentByName(request.getName());
            if (found != null &&
                    !found.getId().equals(current.getId())) {
                HolderHelper.setHolderValue(errorMessage, "There is a component under same name!");
                return null; //Two components cannot have same name
            }

            current.setName(request.getName());
            current.setDescription(request.getDescription());
            current.setEnabled(request.getEnabled());
            current.setPublic(request.isPublic());

            //Gets the plugin info from the plugin itself and encapsulates it into
            //component plugin structure
            PluginInfo pluginInfo = pluginController.findPlugin(current.getPluginId());

            //Makes a copy of the plugin information into component.
            current.setPluginInfo(pluginInfo);

            //Is profile set?
            if (request.getProfile() != null) {
                //We've got a profile!

                //Creates a copy of the current component to check if
                //profile is acceptable by plugin without compromising
                //current component.
                Component tempComponent = Component.copyComponent(current);

                //Merges the request profile with plugin profile
                tempComponent.mergeProfile(request.getProfile());

                //Checks if merged profile is accepted by the plugin
                if (!pluginController.checkProfileChanges(tempComponent.getPluginId(),
                        tempComponent.getProfile(),
                        errorMessage)) {
                    //Profile was not accepted
                    return null;
                }

                //Copies the checked profile into current component profile
                current.setProfile(tempComponent.getProfile());
            } else {
                //Makes a copy of the plugin profile into new component profile.
                current.setProfile(pluginInfo.getProfile());
            }

            //Stores the updated component into detabase
            if (!componentRepository.setComponent(current, errorMessage)) {
                return null;
            }

            return current;
        } catch (Exception e) {
            HolderHelper.setHolderValue(errorMessage, "Error while updating component. Error: " + e.getMessage());
            log.error("ComponentController::updateComponent. Error: ", e);
            e.getStackTrace();
            return null;
        }

    }

    private Component isComponentIdInComponentList(UUID id) {
        return isComponentIdInComponentList(id, true);
    }

    private Component isComponentIdInComponentList(UUID id, boolean useLock) {

        try {
            if (useLock) {
                ThreadUtils.lock(this.lock);
            }

            try {
                return this.components_.entrySet().stream()
                        .filter(e -> e != null && e.getKey() == id)
                        .map(Map.Entry::getValue)
                        .findAny()
                        .orElse(null);
            } finally {
                if (useLock) {
                    ThreadUtils.unlock(this.lock);
                }
            }
        } catch (Exception e) {
            log.error("ComponentController::isComponentInComponentList. Error:", e);
            return null;
        }

    }

    //
    //
    //
    //
    //
    private void processComponents(Update<UUID, ComponentInfo> u) {
        try {
            ThreadUtils.lock(lock);

            Component component = null;
            try {
                switch (u.type) {
                    case Set:

                        //Since database loading and add and update component calls
                        //change profiles, names, descriptions, etc we need to update
                        //component list without changing component Id that's why we're
                        //calling method fromInfo instead of create (because create would
                        //initialize a new id)
                        component = Component.fromInfo(u.value);

                        //We need to check if plugin info is already loaded
                        //Since pluin info is immutable i.e. it doesn't change over the time
                        //we can assure that an already defined info is the correct one!
                        if (component.getPluginInfo() == null) {
                            //Component data hadn't plugin info, so we load it (probably during database loading)
                            component.setPluginInfo(pluginController.findPlugin(component.getPluginId()));
                            component.setIcon(component.getPluginInfo().getIcon());
                            component.setType(component.getPluginInfo().getType());
                        }

                        this.components_.put(u.key, component);

                        //Emits component changes or insertion
                        //RxJavaUtils.safeOnNext(subject, new Update<>(u.type, u.key, Component.copyComponent(component)));
                        break;
                    case Delete:
                        this.components_.remove(u.key);
                        //RxJavaUtils.safeOnNext(subject, new Update<>(u.type, u.key, u.value));
                        break;
                }

                componentEmitter.onNext(u);

            } finally {
                ThreadUtils.unlock(lock);
            }
        } catch (InterruptedException ex) {
            log.error("ComponentController::processComponents", ex);
        }
    }

    // ------------------ FLUX ----------------------------

    public Flux<Update<UUID, ComponentInfo>> getComponentsFlux() {
        try {
            return Flux.defer(this::createComponentListFlux)
                    .concatWith(this.componentEmitter.getProcessor())
                    .onBackpressureBuffer()
                    .doOnComplete(() -> log.warn("PublishedWorkflowList: Complete"))
                    .doOnCancel(() -> log.warn("PublishedWorkflowList: Cancel"));
        } catch (Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }

    private Flux<Update<UUID, ComponentInfo>> createComponentListFlux() {
        try {
            ThreadUtils.lock(this.lock);
            try {
                return Flux.fromIterable(components_.entrySet()
                        .stream()
                        .map(e -> new Update<UUID, ComponentInfo>(Update.Type.Set, e.getKey(), ComponentInfo.copy(e.getValue())))
                        .collect(Collectors.toList()));
            } finally {
                ThreadUtils.unlock(this.lock);
            }
        } catch (Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }


    // ----------------- OBSERVABLES ----------------------

    /*public rx.Observable<Update<UUID, ComponentInfo>> getObservable() {
        return rx.Observable.defer(this::createObservable)
                .concatWith(subject);
    }

    private rx.Observable<Update<UUID, ComponentInfo>> createObservable() {
        try {
            ThreadUtils.lock(lock);
            try {
                return rx.Observable.from(components_.entrySet()
                        .stream()
                        .map(e -> new Update<>(Update.Type.Set, e.getKey(), ComponentInfo.copy(e.getValue().getComponentInfo())))
                        .collect(Collectors.toList()));
            } finally {
                ThreadUtils.unlock(lock);
            }
        } catch (Throwable t) {
            return rx.Observable.error(t);
        }
    }*/

    private String convertToString(Collection<String> list)
    {
        String message = "";
        for (String item : list) {
            message = message + item + ", ";
        }
        return message.substring(0, message.length() - 2);
    }

}
