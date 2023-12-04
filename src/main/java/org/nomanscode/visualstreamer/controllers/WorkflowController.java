package org.nomanscode.visualstreamer.controllers;

//import org.nomanscode.visualstreamer.RxJavaUtils;
import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.database.RecentWorkflowRepository;
import org.nomanscode.visualstreamer.database.WorkflowPropertyRepository;
import org.nomanscode.visualstreamer.database.WorkflowRepository;
import org.nomanscode.visualstreamer.exceptions.SSEException;
import org.nomanscode.visualstreamer.types.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.Disposable;
import reactor.core.publisher.*;
//import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
//import javax.xml.ws.Holder;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class WorkflowController {

    @Autowired
    LogController logController;

    private Lock lock;

    private Lock workflowGroupLock;

    private Lock recentWorkflowLock;

    private Map<UUID, Workflow> workflows_  = new LinkedHashMap<>();

    private Map<UUID, RecentWorkflowInfo> recentWorkflows_ = new LinkedHashMap<>();

    /*private Subscription workflowSubscription;

    private Subscription workflowGroupSubscription;

    private Subscription recentWorkflowSubscription;*/

    @Autowired
    private RecentWorkflowRepository recentWorkflowRepository;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private WorkflowPropertyRepository workflowPropertyRepository;

    /*@Autowired
    private ComponentController componentController;*/

    @Autowired
    private PluginController pluginController;

    private CybertronEmitterProcessor<Update<UUID, RecentWorkflowInfo>> recentWorkflowEmitter = CybertronEmitterProcessor.create(false);

    private CybertronEmitterProcessor<Update<UUID, WorkflowInfo>> workflowEmitter = CybertronEmitterProcessor.create(false);


    WorkflowController()
    {

        lock = new ReentrantLock();
        workflowGroupLock = new ReentrantLock();
        recentWorkflowLock = new ReentrantLock();
    }

    @PostConstruct
    private void postConstruct() throws Exception
    {

        /*rx.Observable<Update<UUID, WorkflowInfo>> workflowObservable = workflowRepository.getObservable()
                .onBackpressureBuffer()
                .observeOn(rx.schedulers.Schedulers.io());

        workflowSubscription = RxJavaUtils.safeSubscribe("Workflow subscription", workflowObservable, this::processWorkflows);


        rx.Observable<Update<UUID, WorkflowGroupInfo>> workflowGroupObservable = workflowGroupRepository.getObservable()
                .onBackpressureBuffer()
                .observeOn(rx.schedulers.Schedulers.io());

        workflowGroupSubscription = RxJavaUtils.safeSubscribe("Workflow Group subscription", workflowGroupObservable, this::processWorkflowGroups);

        rx.Observable<Update<UUID, RecentWorkflowInfo>> recentWorkflowObservable = recentWorkflowRepository.getObservable()
                .onBackpressureBuffer()
                .observeOn(rx.schedulers.Schedulers.io());

        recentWorkflowSubscription = RxJavaUtils.safeSubscribe("Recent Workflow subscription", recentWorkflowObservable, this::processRecentWorkflows);
*/
    }

    @PreDestroy
    private void preDestroy()
    {
        //recentWorkflowSubscription.unsubscribe();
        //recentWorkflowSubscription = null;

        this.workflows_.clear();

        this.recentWorkflows_.clear();
    }

    // ---------------- TASK ROUTINES ----------------------------

    /*void processComponentChanges(Update<UUID, ComponentInfo> u)
    {
        try {

            ThreadUtils.lock(this.lock);

            switch (u.type) {
                case Set:

                    //On all the workflows...
                    this.workflows_.values().stream().forEach( w -> {

                        //...we remove the correspondent component...
                        UUID id = w.getComponents().keySet().stream()
                                .filter(k -> k.equals(u.key))
                                .findAny().orElse(null);

                        w.getComponents().entrySet().remove(id);

                        //... and add its new version
                        w.getComponents().put( u.key, new ComponentInfo(u.value));
                    });

                    break;
                case Delete:

                    //On all the workflows...
                    this.workflows_.values().stream().forEach( w -> {

                        //...remove the component on this workflow
                        UUID id = w.getComponents().keySet().stream()
                                .filter(k -> k.equals(u.key))
                                .findAny().orElse(null);

                        w.getComponents().entrySet().remove(id);
                    });

                    break;
            }
        }
        catch(Exception e)
        {

        }
        finally{
            ThreadUtils.unlock(this.lock);
        }
    }*/


    //
    //
    //Removes a given component from the component list, but first, checking
    //if the component is being used in the workflow component list.
    //
    /*public ComponentInfo removeComponent(UUID componentId, MyHolder<String> errorMessage)
    {
        //TODO: Need to check if this component is being used by
        //a workflow.
        try {
            Map<UUID, Workflow> workflows = getWorkflowsByComponentId(componentId);
            if ( workflows != null &&
                    workflows.size() > 0) {
                //There are workflows running this component. We cant remove it
                //until they are removed from the workflows.
                HolderHelper.setHolderValue(errorMessage, "The component is being used by");
                return null;
            }

            return componentController.removeComponent(componentId, errorMessage);
        }
        catch(Exception e)
        {
            HolderHelper.setHolderValue(errorMessage, "Error removing component. Error: " + e.getMessage());
            log.error("WorkflowController::removeComponent: ", e);
            return null;
        }

    }*/


    //----------------------- WORKFLOW ROUTINES -------------------------------

    //
    //
    //Process workflow changes within workflow repository and inserts or removes
    //the workflow to/from the list.
    //
    //
    private void processWorkflows(Update<UUID, WorkflowInfo> u)
    {
        try {
            ThreadUtils.lock(this.lock);
            try {
                switch ( u.type ) {
                    case Set:
                        this.workflows_.put( u.key, Workflow.create(u.value));
                        break;
                    case Delete:
                        this.workflows_.remove(u.key);
                        break;
                }

                this.workflowEmitter.onNext(new Update<UUID, WorkflowInfo>(u.type, u.key, WorkflowInfo.copy(u.value)));

            } finally {
                ThreadUtils.unlock(this.lock);
            }
        } catch (InterruptedException ex) {
            log.error("Thread Interrupted", ex);
        }
    }

    //
    //
    //Process Recent Workflows changes within Recent Workflow repository and inserts or removes
    //the recent workflow reference to/from the list.
    //
    //
    private void processRecentWorkflows(Update<UUID, RecentWorkflowInfo> u)
    {
        try {
            ThreadUtils.lock(workflowGroupLock);
            try {
                switch ( u.type ) {
                    case Set:

                        RecentWorkflowInfo i = this.recentWorkflows_.values().stream()
                                                                    .filter( w ->
                                                                    w.getUserId().equals(u.value.getUserId()) &&
                                                                    w.getWorkflowId().equals(u.value.getWorkflowId()))
                                                                    .findAny()
                                                                    .orElse(null);

                        if ( i == null ) {
                            i = RecentWorkflowInfo.copy(u.value);
                            this.recentWorkflows_.put(u.key, i);
                        }
                        else {
                            i.setDate(u.value.getDate());
                        }

                        this.recentWorkflowEmitter.onNext(new Update<UUID, RecentWorkflowInfo>(Update.Type.Set, i.getId(), RecentWorkflowInfo.copy(i)));

                        break;
                    case Delete:
                        this.recentWorkflows_.remove(u.key);

                        this.recentWorkflowEmitter.onNext(new Update<UUID, RecentWorkflowInfo>(Update.Type.Delete, u.key, null));
                        break;
                }

            } finally {
                ThreadUtils.unlock(workflowGroupLock);
            }
        } catch (InterruptedException ex) {
            log.error("Thread Interrupted", ex);
        }
    }

    //
    //
    //Gets the workflow list.
    //
    //
    public Map<UUID, WorkflowInfo> getWorkflows()
    {
        try {
            ThreadUtils.lock(this.lock);

            try {
                return this.workflows_.values()
                        .stream()
                        .collect(Collectors.toMap(WorkflowInfo::getId, WorkflowInfo::copy));
            }
            finally {
                ThreadUtils.unlock(this.lock);
            }
        }
        catch(Exception e) {
            log.error("WorkflowController::getWorkflows: Error: ", e);
            e.getStackTrace();
            return null;
        }

    }

    //
    //
    //Gets a list of workflows given a ComponentId
    //Returns a list of workflows or null.
    //
    //
    public Map<UUID, Workflow> getWorkflowsByComponentId(UUID componentId)
    {
        try {
            ThreadUtils.lock(this.lock);

            try {
                if (this.workflows_ == null) {
                    return null;
                }

                Map<UUID, Workflow> workflows = this.workflows_.entrySet()
                        .stream()
                        .filter(w -> {

                            Workflow workflow = w.getValue();
                            if (workflow.getComponents() == null) {
                                return false;
                            }

                            return workflow.getComponents().containsKey(componentId);
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                if (workflows.size() == 0) {
                    return null;
                }

                return workflows;
            }
            finally {
                ThreadUtils.unlock(this.lock);
            }

        }
        catch(Exception e)
        {
            e.getStackTrace();
            log.error("Workflow::getWorkflowsByComponentId: " +  e.getMessage());
            return null;
        }

    }

    //
    //
    //Finds workflows in which a given component is being used
    //Returns all the workflows involved or null.
    //
    //
    public Map<UUID, Workflow> findWorkflowsByComponentId(UUID componentId)
    {
        try {
            ThreadUtils.lock(this.lock);

            try {
                return this.workflows_.entrySet()
                        .stream()
                        .filter(w -> w.getValue().getComponents() != null && w.getValue().getComponents().containsKey(componentId))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
            finally {
                ThreadUtils.unlock(this.lock);
            }
        } catch (Exception e) {
            log.error("WorkflowController:findWorkflowsByComponentId. Error: ", e);
            e.printStackTrace();
            return null;
        }
    }


    //
    //
    //Locates a workflow by the Id
    //Returns a reference for the located workflow or null.
    //
    //
    public Workflow findWorkflow(UUID workflowId, boolean useLock)
    {
        try {

            if ( useLock ) {
                ThreadUtils.lock(this.lock);
            }
            try {
                return this.workflows_.get(workflowId);
            }
            finally {
                if ( useLock ) {
                    ThreadUtils.unlock(this.lock);
                }
            }

        }
        catch (Exception e) {
            log.error("WorkflowController:findWorkflow. Error: ", e);
            e.printStackTrace();
            return null;
        }

    }

    public Workflow findWorkflow(UUID workflowId)
    {
        return findWorkflow(workflowId, true);
    }

    //
    //
    //Locates a workflow by its name
    //Returns a reference for the located workflow or null.
    //
    //
    public Workflow findWorkflow(String name, boolean useLock)
    {
        try {

            if ( useLock ) {
                ThreadUtils.lock(this.lock);
            }

            try {
                return this.workflows_.values().stream()
                        .filter( f -> f.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
            }
            finally {
                if ( useLock ) {
                    ThreadUtils.unlock(this.lock);
                }
            }
        }
        catch (Exception e) {
            log.error("WorkflowController:findWorkflow. Error: ", e);
            e.printStackTrace();
            return null;
        }

    }

    public Workflow findWorkflow(String name)
    {
        return findWorkflow(name, true);
    }

    public Workflow create(Workflow workflow, MyHolder<String> errorMessage) {

        try {

            if (workflow == null) {
                HolderHelper.setHolderValue(errorMessage, "Invalid parameter!");
                return null;
            }

            Workflow foundWorkflow = this.findWorkflow(workflow.getName());
            if (foundWorkflow != null ) {
                HolderHelper.setHolderValue(errorMessage, "Name already exists! You must publish under a new name");
                return null;
            }

            Workflow newWorkflow = Workflow.copy(workflow);
            if (newWorkflow == null) {
                HolderHelper.setHolderValue(errorMessage, "Unable to copy workflow structure!");
                return null;
            }


            //By adding the workflow into repository will lead to workflow to be added to workflow list.
            if ( !this.workflowRepository.setWorkflow(workflow) ) {
                HolderHelper.setHolderValue(errorMessage, "Error while saving workflow structure into database!");
                return null;
            }

            return workflow;
        }
        catch(Exception e) {
            log.error("WorkflowController::createWorkflow: Interrupted Exception", e);
            HolderHelper.setHolderValue(errorMessage, "Error: " + e.getMessage());
            return null;
        }

    }

    public WorkflowInfo update(WorkflowRequest request, MyHolder<String> errorMessage) {

        try {

            if (request == null) {
                HolderHelper.setHolderValue(errorMessage, "Invalid parameter!");
                return null;
            }

            Workflow found = this.findWorkflow(request.getId());
            if (found == null ) {
                HolderHelper.setHolderValue(errorMessage, "Workflow not found!");
                return null;
            }

            found.setName(request.getName());
            found.setDescription(request.getDescription());

            Workflow updated = Workflow.copy(found);
            if (updated == null) {
                HolderHelper.setHolderValue(errorMessage, "Unable to copy workflow structure!");
                return null;
            }

            //By adding the workflow into repository will lead to workflow to be added to workflow list.
            if ( !this.workflowRepository.setWorkflow(updated) ) {
                HolderHelper.setHolderValue(errorMessage, "Error while saving workflow structure into database!");
                return null;
            }

            return updated;
        }
        catch(Exception e) {
            log.error("WorkflowController::createWorkflow: Interrupted Exception", e);
            HolderHelper.setHolderValue(errorMessage, "Error: " + e.getMessage());
            return null;
        }

    }

    public Workflow remove(UUID workflowId, boolean useLock) {
        try {
            if ( useLock ) {
                ThreadUtils.lock(this.lock);
            }
            try {
                return this.workflows_.remove(workflowId);
            }
            finally {
                if ( useLock ) {
                    ThreadUtils.unlock(this.lock);
                }
            }
        }
        catch(Exception e) {
            log.error("WorkflowController::remove: Exception", e);
            return null;
        }

    }
    //
    //
    //Removes a workflow, but checking if there are referenced jobs pointing to it.
    //Returns the workflow or null.
    //
    //
    public Workflow remove(UUID workflowId, MyHolder<String> errorMessage)
    {

        Workflow workflow = null;

        try {

            ThreadUtils.lock(this.lock);

            try {
                workflow = this.findWorkflow(workflowId, false);
                if (workflow == null) {
                    HolderHelper.setHolderValue(errorMessage, "Workflow not found!");
                    return null;
                }

                //Need to check if the workflow is locked by someone else.
                if (!workflow.lock()) {
                    //Workflow already locked or another error
                    HolderHelper.setHolderValue(errorMessage, "Workflow has been locked by someone else!");
                    return null;
                }

                //Checks if the workflow has any pending or running jobs
                /*Map<UUID, Job> jobList = jobController.getJobsByWorkflow(workflowId);
                if (jobList != null && jobList.size() > 0) {
                    //Cannot remove the workflow, there are active jobs referenced to
                    //this workflow. In order to remove a workflow, all the jobs must be
                    //released first.

                    String jobs = jobList.values().stream().map(Job::getName).collect(Collectors.toList()).toString();
                    HolderHelper.setHolderValue(errorMessage, "Error while removing the workflow. The workflow are being used for the following jobs: " + jobs);
                    return null;
                }*/

                //Removes the workflow record off the database
                if (!this.workflowRepository.del(workflowId)) {
                    return null;
                }

                //Returns the removed workflow entry
                return workflow;
            }
            finally {
                if ( workflow != null ) {
                    workflow.unLock();
                }

                ThreadUtils.unlock(this.lock);
            }
        }
        catch(Exception e)
        {
            HolderHelper.setHolderValue(errorMessage, "Error while removing a workflow. Error: " + e.getMessage());
            log.error("WorkflowController::remove: Exception", e);
            return null;
        }
    }
    //-------------------------------------------------------------------------

    public boolean deleteRecentWorkflow(UUID id, MyHolder<String> errorMessage) {

        try {

            this.recentWorkflowRepository.del(id);
            return true;

        }
        catch(Exception e) {
            HolderHelper.setHolderValue(errorMessage, "Error while deleting recent workflow entry!");
            return false;
        }

    }

    public Flux<Update<UUID, RecentWorkflowInfo>> getRecentWorkflowListFlux(UUID userId)
    {
        try {
            return Flux.defer( () -> this.createRecentWorkflowListFlux(userId))
                    .concatWith(this.recentWorkflowEmitter.getProcessor())
                    .onBackpressureBuffer()
                    .doOnComplete(() -> log.warn("RecentWorkflowList: Complete"))
                    .doOnCancel(() -> log.warn("RecentWorkflowList: Cancel"));
        }
        catch(Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }

    public Flux<Update<UUID, RecentWorkflowInfo>> createRecentWorkflowListFlux(UUID userId)
    {
        try {
            ThreadUtils.lock(this.recentWorkflowLock);
            try {
                return Flux.fromIterable(this.recentWorkflows_.entrySet()
                        .stream()
                        .filter(r -> r.getValue().getUserId().equals(userId))
                        .map(g -> new Update<UUID, RecentWorkflowInfo>(Update.Type.Set, g.getKey(), g.getValue()))
                        .collect(Collectors.toList()));
            }
            finally {
                ThreadUtils.unlock(this.recentWorkflowLock);
            }
        }
        catch(Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }

    }

    //----------------------- OBSERVABLES ------------------------------
    public Flux<Update<UUID, WorkflowInfo>> getPublishedWorkflowListFlux() {
        try {
            return Flux.defer(this::createPublishedWorkflowListFlux)
                    .concatWith(this.workflowEmitter.getProcessor())
                    .onBackpressureBuffer()
                    .doOnComplete(() -> log.warn("PublishedWorkflowList: Complete"))
                    .doOnCancel(() -> log.warn("PublishedWorkflowList: Cancel"));
        }
        catch(Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }

    private Flux<Update<UUID, WorkflowInfo>> createPublishedWorkflowListFlux()
    {
        try {
            ThreadUtils.lock(this.lock);
            try {
                return Flux.fromIterable(workflows_.entrySet()
                        .stream()
                        .map(e -> new Update<UUID,WorkflowInfo>(Update.Type.Set, e.getKey(), WorkflowInfo.copy(e.getValue().getWorkflowInfo())))
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
}
