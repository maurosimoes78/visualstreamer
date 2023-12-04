package org.nomanscode.visualstreamer.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
//import rx.Subscription;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Controller
public class WorkflowEditorController /*implements IJobController*/ {

    @Autowired
    LogController logController;

    /*@Autowired
    ResourceController resourceController;

    @Autowired
    StorageController storageController;

    @Autowired
    TemplateController templateController;

    @Autowired
    PluginController pluginController;

    @Autowired
    private WorkflowController workflowController;

    @Autowired
    private ComponentController componentController;

    @Autowired
    private JobController jobController;

    @Autowired
    private WatchFolderController watchFolderController;

    @Autowired
    private WorkflowDesignerRepository workflowDesignerRepository;

    @Autowired
    private UserSessionController userSessionController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SourceController sourceController;

    private Lock activeProjectLock;
    private Map<UUID, Job> activeProjectMap_ = new LinkedHashMap<>();

    private CybertronEmitterProcessor<Update<UUID, JobInfo>> emitterActiveProjectListFlux_ = CybertronEmitterProcessor.create(false);


    //---------- Temporary Workflow List ------------

    private Lock projectLock;
    private Map<UUID, Workflow> projectMap_ = new LinkedHashMap<>();

    private Subscription projectSubscription;

    private CybertronEmitterProcessor<Update<UUID, WorkflowInfo>> emitterProjectListFlux_ = CybertronEmitterProcessor.create(false);

    */
    @PostConstruct
    private void postConstruct() throws Exception {

        /*this.projectLock = new ReentrantLock();

        rx.Observable<Update<UUID, WorkflowInfo>> workflowObservable = workflowDesignerRepository.getObservable()
                .onBackpressureBuffer()
                .observeOn(rx.schedulers.Schedulers.io());

        projectSubscription = RxJavaUtils.safeSubscribe("Workflow subscription", workflowObservable, this::processTemporaryWorkflows);

        activeProjectLock = new ReentrantLock();*/

    }

    @PreDestroy
    private void preDestroy() {

        /*this.emitterActiveProjectListFlux_.dispose();
        this.emitterProjectListFlux_.dispose();

        this.projectSubscription.unsubscribe();

        this.activeProjectMap_.values().forEach(j -> j.dispose());
        this.activeProjectMap_.clear();

        this.projectMap_.clear();*/
    }

    /*
    @Override
    public JobInfo submit(String name, JobEnvelope envelope, UUID workflowId, JobPriority priority, TriggerType sourceType, String source, UUID parentJobId, UUID ownerId, String ownerName, Holder<String> errorMessage) {
        return jobController.submit(name, envelope, workflowId, priority, sourceType, source, parentJobId, ownerId, ownerName, errorMessage);
    }

    @Override
    public JobInfo submit(String name, JobEnvelope envelope, UUID workflowId, JobPriority priority, TriggerType sourceType, String source, UUID parentJobId, UUID ownerId, String ownerName, Holder<String> errorMessage, boolean autorun) {
        return jobController.submit(name, envelope, workflowId, priority, sourceType, source, parentJobId, ownerId, ownerName, errorMessage, autorun);
    }

    @Override
    public JobInfo submit(String name, JobEnvelope envelope, UUID workflowId, JobPriority priority, TriggerType sourceType, String source, UUID parentJobId, UUID ownerId, String ownerName, Holder<String> errorMessage, boolean autorun, String publishAs) {
        return jobController.submit(name, envelope, workflowId, priority, sourceType, source, parentJobId, ownerId, ownerName, errorMessage, autorun, publishAs);
    }

    @Override
    public JobInfo submit(String name, JobEnvelope envelope, UUID workflowId, JobPriority priority, TriggerType sourceType, String source, UUID parentJobId, UUID ownerId, String ownerName, Holder<String> errorMessage, boolean autorun, String publishAs, boolean registerJob) {
        return jobController.submit(name, envelope, workflowId, priority, sourceType, source, parentJobId, ownerId, ownerName, errorMessage, autorun, publishAs, registerJob);
    }

    @Override
    public boolean run(UUID jobId, Holder<String> errorMessage) {
        Job job = findTemporaryWorkflowJob(jobId);
        if (job == null) {
            HolderHelper.setHolderValue(errorMessage, "Temporary job not found!");
            return false;
        }

        return job.run(errorMessage);
    }

    @Override
    public void abort(UUID jobId) {
        Job job = findTemporaryWorkflowJob(jobId);
        if (job == null) {
            return;
        }

        job.abort();
    }

    @Override
    public void pause(UUID jobId) {
        Job job = findTemporaryWorkflowJob(jobId);
        if (job == null) {
            return;
        }

        job.pause();
    }

    @Override
    public void step(UUID jobId) {
        Job job = findTemporaryWorkflowJob(jobId);
        if (job == null) {
            return;
        }

        job.step();
    }

    @Override
    public JobStatus getJobStatus(UUID jobId)
    {

        Job job = findTemporaryWorkflowJob(jobId);
        if (job == null) {
            return null;
        }

        return job.getStatus();
    }

    @Override
    public int getJobProgress(UUID jobId)
    {
        Job job = findTemporaryWorkflowJob(jobId);
        if (job == null) {
            return 0;
        }

        return job.getProgress();
    }

    @Override
    public Object getPluginController()
    {
        return this.pluginController;
    }

    @Override
    public Resource getResource(UUID resourceId, List<UUID> excludeResourceids) {
        return this.resourceController.getResource(resourceId, excludeResourceids);
    }

    // --------------------------- Resources ------------------------------
    @Override
    public IResource getResource(UUID resourceId)
    {
        return this.resourceController.getResource(resourceId);
    }

    @Override
    public List<IResource> getResources(List<UUID> resourceIds) {
        return this.resourceController.getResources(resourceIds);
    }

    @Override
    public List<IStorageInfo> getStorages() {
        return new ArrayList<>(this.storageController.getStorages().values());
    }

    @Override
    public AbstractStorage getStorage(UUID id) {
        return this.storageController.findStorage(id);
    }

    // ----------------------- Watch Folders ----------------------------
    @Override
    public List<IWatchFolderInfo> getWatchFolders() {
        return new ArrayList<>(watchFolderController.getWatchFolders().values());
    }

    @Override
    public IWatchFolder getWatchFolder(UUID id) {
        return watchFolderController.findWatchFolder(id, true);
    }

    @Override
    public List<IFileSource> getFileSources(List<IFileSource> fileSources) {
        return sourceController.getFileSources(fileSources);
    }

    @Override
    public void onJobChange(UUID jobId, JobInfo job, ChangeEnum what) {

        if ( what == ChangeEnum.CHANGE_NAME ||
                what == ChangeEnum.CHANGE_WORKFLOW ) {
            this.emitterActiveProjectListFlux_.onNext(new Update<UUID, JobInfo>(Update.Type.Set, jobId, JobInfo.copy(job)));
        }
    }

    @Override
    public void onComponentChanged(UUID jobId, UUID componentId, String name, UUID moduleId, String moduleName, ComponentStatus status, Integer progress) {

    }

    // --------------------- Temporary Workflow List ----------------------

    //
    //
    //Gets the workflow list.
    //
    //
    public Map<UUID, Workflow> getWorkflows() {
        try {
            ThreadUtils.lock(projectLock);
            try {
                return this.projectMap_.values()
                        .stream()
                        .collect(Collectors.toMap(WorkflowInfo::getId, Workflow::copy));
            }
            finally {
                ThreadUtils.unlock(projectLock);
            }

        } catch (Exception e) {

            log.error("WorkflowEditorController::getWorkflows: Error: ", e);
            e.getStackTrace();
            return null;
        }
    }

    //
    //
    //Process temporary workflow changes within workflow designer repository and inserts or removes
    //the workflow to/from the list.
    //
    //
    private void processTemporaryWorkflows(Update<UUID, WorkflowInfo> u) {
        try {
            ThreadUtils.lock(projectLock);
            try {
                switch (u.type) {
                    case Set:

                        if (this.findTemporaryWorkflow(u.value.getId(), false) == null) {
                            this.projectMap_.put(u.key, Workflow.create(u.value));
                        }

                        break;
                    case Delete:
                        this.projectMap_.remove(u.key);
                        break;
                }

                this.emitterProjectListFlux_.onNext(new Update<UUID, WorkflowInfo>(u.type, u.key, WorkflowInfo.copy(u.value)));


            } finally {
                ThreadUtils.unlock(projectLock);
            }
        } catch (InterruptedException ex) {
            log.error("Thread Interrupted", ex);
        }
    }

    //
    //
    //Locates a temporary workflow by the Id
    //Returns a reference for the located workflow or null.
    //
    //
    private Workflow findTemporaryWorkflow(UUID workflowId) {
        return this.findTemporaryWorkflow(workflowId, true);
    }

    private Workflow findTemporaryWorkflow(UUID workflowId, boolean useLock) {
        try {

            if (useLock) {
                ThreadUtils.lock(this.projectLock);
            }

            try {
                return this.projectMap_.get(workflowId);
            } finally {
                if (useLock) {
                    ThreadUtils.unlock(this.projectLock);
                }
            }

        } catch (Exception e) {
            log.error("WorkflowDesignerController:findTemporaryWorkflow. Error: ", e);
            e.printStackTrace();
            return null;
        }
    }

    private Workflow findTemporaryWorkflow(String name, boolean useLock) {
        try {
            if (useLock) {
                ThreadUtils.lock(this.projectLock);
            }

            try {
                return this.projectMap_.entrySet().stream()
                        .filter(e -> e.getValue().getName().equalsIgnoreCase(name))
                        .map(Map.Entry::getValue)
                        .findAny()
                        .orElse(null);
            } finally {
                if (useLock) {
                    ThreadUtils.unlock(this.projectLock);
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    private Workflow findTemporaryWorkflow(String name) {
        return findTemporaryWorkflow(name, true);
    }

    // -------------------- Workflow Editor Operations --------------------

    private Job findTemporaryWorkflowJobAndLock(UUID workflowId, UserSession userSession, Holder<String> errorMessage, boolean takeProjectOwnership, boolean useLock) {

        //locates the temporary workflow job
        Job job = findTemporaryWorkflowJob(workflowId, useLock);
        if (job == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project not found!");
            return null;
        }

        //Checks if the job has a owner and I'm the job owner.
        //If I'm not but I'm Administrator or Supervisor, I may take
        //the job ownership (if I want)
        if (isWorkflowJobLocked(job, userSession)) {
            HolderHelper.setHolderValue(errorMessage, "This workflow is locked by someone else!");
            return null;
        }

        if ( takeProjectOwnership ) {
            //Takes the job ownership.
            job.setOwnerId(userSession.getId());
        }

        return job;
    }

    //
    //
    //Checks if this job was created by this user and takes the job ownership
    //
    //
    private boolean isWorkflowJobLocked(Job job, UserSession userSession) {

        if (job.getOwnerId() != null &&
                !job.getOwnerId().equals(userSession.getId()) &&
                !userSession.getUser().isAdmin() &&
                !userSession.getUser().isSupervisor()) {
            return true;
        }

        return false;
    }

    private Job findTemporaryWorkflowJob(UUID workflowId, boolean useLock) {
        try {
            if (useLock) {
                ThreadUtils.lock(this.activeProjectLock);
            }

            try {
                return this.activeProjectMap_.entrySet().stream()
                        .filter(e -> e.getKey().equals(workflowId))
                        .map(Map.Entry::getValue)
                        .findAny()
                        .orElse(null);
            } finally {
                if (useLock) {
                    ThreadUtils.unlock(this.activeProjectLock);
                }
            }

        } catch (Exception e) {
            return null;
        }
    }

    private Job findTemporaryWorkflowJob(UUID workflowId) {
        return findTemporaryWorkflowJob(workflowId, true);
    }

    private Job findTemporaryWorkflowJob(String name, boolean useLock) {
        try {
            if (useLock) {
                ThreadUtils.lock(this.activeProjectLock);
            }

            try {
                return this.activeProjectMap_.entrySet().stream()
                        .filter(e -> e.getValue().getName().equalsIgnoreCase(name))
                        .map(Map.Entry::getValue)
                        .findAny()
                        .orElse(null);
            } finally {
                if (useLock) {
                    ThreadUtils.unlock(this.activeProjectLock);
                }
            }

        } catch (Exception e) {
            return null;
        }
    }

    private Job findTemporaryWorkflowJob(String name) {
        return findTemporaryWorkflowJob(name, true);
    }

    //
    //Saves the workflow project in temporary workflow list
    //
    //
    //
    public Workflow saveTemporaryWorkflowJob(UserSession userSession, UUID workflowId, Holder<String> errorMessage) {
        return this.saveTemporaryWorkflowJob(userSession, workflowId, errorMessage, true);
    }

    public Workflow saveTemporaryWorkflowJob(UserSession userSession, UUID workflowId, Holder<String> errorMessage, boolean useLock) {
        try {

            if ( useLock ) {
                ThreadUtils.lock(this.activeProjectLock);
            }

            try {
                Job job = findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, false);
                if (job == null) {
                    return null;
                }

                if (job.getWorkflow() == null) {
                    HolderHelper.setHolderValue(errorMessage, "Invalid workflow project!");
                    return null;
                }

                //Saves the workflow project in a new copy and
                //change job saved status.
                Workflow workflow = job.save(errorMessage);
                if ( workflow == null ) {
                    return null;
                }

                //Writes json structure in the log
                log.info(workflow.toJSONPretty());

                //Saves the workflow. Note that workflow structure will be
                //saved in the repository and so will be loaded into memory again.
                //However the workflow data structure would be processed during
                //reload. To prevent this we add the workflow data structure (actually
                //the entire workflow) into memory before inserting it into the database.
                this.projectMap_.put(workflow.getId(), workflow);

                //Sends the workflow to database.
                if (!workflowDesignerRepository.setWorkflow(workflow, errorMessage)) {
                    return null;
                }

                return workflow;
            } finally {
                if ( useLock ) {
                    ThreadUtils.unlock(this.activeProjectLock);
                }

            }

        } catch (Exception e) {
            return null;
        }
    }

    public ServerResponse saveTemporaryWorkflowJobAs(UserSession userSession, UUID workflowId, WorkflowRequest request) {

        Holder<String> errorMessage = new Holder<>();

        try {

            ThreadUtils.lock(this.activeProjectLock);
            try {
                if (request == null) {
                    return ServerResponse.getServerResponseFailed("Invalid request!");
                }

                Job job = this.findTemporaryWorkflowJobAndLock( workflowId, userSession, errorMessage, false, false);
                if ( job == null ) {
                    return ServerResponse.getServerResponseFailed(errorMessage);
                }

                //Removes the current job from open job list
                this.activeProjectMap_.remove(job.getId());

                job.setId(UUID.randomUUID());
                job.setName(request.getName());

                //TODO:
                //tempJob.setDescription(request.getDescription());
                //tempJob.setGroupName(request.getGroupName());
                //tempJob.setKeywords(request.getKeywords());

                job.setOwnerId(userSession.getId());

                //Writes json structure in the log
                log.info(job.getWorkflow().toJSONPretty());

                //Adds the project job into job project list
                this.activeProjectMap_.put(job.getId(), job);

                //Stores the workflow project into database (it will be loaded into workflow list right after)
                if (!this.workflowDesignerRepository.setWorkflow(job.getWorkflow(), errorMessage)) {

                    //Remove and dispose workflow job
                    this.activeProjectMap_.remove(job.getId());
                    job.dispose();

                    return ServerResponse.getServerResponseFailed(errorMessage);
                }

                emitterActiveProjectListFlux_.onNext(new Update<UUID,JobInfo>(Update.Type.Set, job.getId(), JobInfo.copy(job)));

                return ServerResponse.getServerResponseSuccess(errorMessage);
            }
            finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }

        }
        catch (Exception e) {
            log.error("JobController::saveTemporaryWorkflowJobAs: ", e);
            return ServerResponse.getServerResponseFailed(e.getMessage());
        }

    }

    private boolean closeAllOtherTemporaryWorkflowJob(UserSession userSession, UUID workflowId, boolean closeAnyway, Holder<ServerResponse> serverResponse, boolean useLock) {

        try {
            if ( useLock ) {
                ThreadUtils.lock(this.activeProjectLock);
            }

            try {
                boolean ret = !this.activeProjectMap_.values().stream()
                        .filter(j -> j.getOwnerId().equals(userSession.getId()) && !j.getId().equals(workflowId))
                        .anyMatch(j -> {


                            if (!closeTemporaryWorkflowJob(userSession, j.getId(), closeAnyway, serverResponse, false, true, true)) {
                                return true;
                            }

                            return false;
                        });

                return ret;
            } finally {
                if ( useLock ) {
                    ThreadUtils.unlock(this.activeProjectLock);
                }
            }
        }
        catch(Exception e) {
            return false;
        }

    }

    //Closes temporary projects based on project ownership. Clearance is not applied here
    //otherwise high clearance user would close regular user projects.
    public boolean closeUserTemporaryWorkflowJobs(UserSession userSession, boolean closeAnyway, Holder<ServerResponse> serverResponse, boolean useLock) {

        try {

            boolean ret = false;

            if (useLock) {
                ThreadUtils.lock(this.activeProjectLock);
            }

            try {
                List<UUID> temp = this.activeProjectMap_.entrySet()
                        .stream()
                        .filter(j -> j.getValue().getOwnerId().equals(userSession.getId()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                ret = !temp.stream()
                        .anyMatch(id -> {


                            if (!closeTemporaryWorkflowJob(userSession, id, closeAnyway, serverResponse, false, true, true)) {
                                return true;
                            }

                            return false;
                        });
            } finally {
                if ( useLock ) {
                    ThreadUtils.unlock(this.activeProjectLock);
                }
            }

            return ret;
        }
        catch(Exception e) {
            log.error("WorkflowEditorController::closeUserTemporaryWorkflowJobs: " + e.getMessage());
            return false;
        }

    }

    //
    //
    //Returns a list containing all the opened temporary workflow jobs or just the unsaved ones.
    //
    public Map<UUID, JobBasicInfo> getUserTemporaryWorkflowJobs(UserSession userSession, boolean unsavedOnly, Holder<ServerResponse> serverResponse, boolean useLock) {
        try {

            if ( useLock ) {
                ThreadUtils.lock(this.activeProjectLock);
            }

            try {
                return this.activeProjectMap_.entrySet().stream()
                        .filter(j -> {
                            Job job = j.getValue();

                            if (j.getValue().getOwnerId().equals(userSession.getId())) {
                                return !unsavedOnly || job.getWorkflow().isModified();
                            } else {
                                return false;
                            }
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> new JobBasicInfo(entry.getKey(), entry.getValue().getName(), entry.getValue().getOwnerId(), entry.getValue().getOwnerName())));
            } finally {
                if ( useLock ) {
                    ThreadUtils.unlock(this.activeProjectLock);
                }
            }
        }
        catch(Exception e) {
            return null;
        }
    }*/

    /*
    //Returns all the opened temporary workflow jobs.
    public Map<UUID, JobBasicInfo> getUserTemporaryWorkflowJobs(UserSession userSession, Holder<ServerResponse> serverResponse, boolean useLock) {
        return this.getUserTemporaryWorkflowJobs(userSession, false, serverResponse, useLock);
    }

    //Returns all the opened temporary workflow jobs that are still unsaved.
    public Map<UUID, JobBasicInfo> getUserUnsavedTemporaryWorkflowJobs(UserSession userSession, Holder<ServerResponse> serverResponse, boolean useLock) {
        return this.getUserTemporaryWorkflowJobs(userSession, true, serverResponse, useLock);
    }

    //Returns all unsaved projects of a particular user or all users
    private Map<UUID, String> getUnsavedProjects(UserSession userSession, boolean useLock) {
        try {

            if (useLock) {
                ThreadUtils.lock(this.activeProjectLock);
            }
            try {
                return this.activeProjectMap_.entrySet()
                        .stream()
                        .filter( e ->
                                e.getValue() != null &&
                                        (userSession == null || e.getValue().getOwnerId().equals(userSession.getId())) &&
                                        e.getValue().getWorkflow().isModified() )
                        .collect(Collectors.toMap(Map.Entry::getKey, e-> e.getValue().getName()));
            } finally {
                if (useLock) {
                    ThreadUtils.unlock(this.activeProjectLock);
                }
            }
        }
        catch(Exception e) {
            return null;
        }
    }

    //
    //
    //Terminates executing jobs and removes them from temporary workflow job list.
    //
    public ServerResponse closeTemporaryWorkflowJobs(UserSession userSession, Map<UUID, Boolean> jobIds) {

        try {

            ThreadUtils.lock(this.activeProjectLock);
            try {
                //We filter just what we have opened.
                Map<UUID, Job> temporary = this.activeProjectMap_.entrySet().stream()
                        .filter(e -> e.getValue() != null &&
                                e.getValue().getOwnerId().equals(userSession.getId()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                List<UUID> projectsClosed = new ArrayList<>();

                //We start closing everything that can be closed (unchanged)
                temporary.entrySet().stream()
                        .filter(e -> !e.getValue().getWorkflow().isModified())
                        .map(Map.Entry::getValue)
                        .forEach(job -> {
                            projectsClosed.add(job.getId());
                            this.closeTemporaryWorkflowJob(userSession, job.getId(), true,  null,false, true, false);
                        });

                //Remove every project closed right before
                projectsClosed.forEach( temporary::remove );

                //TODO: Enviar para o frontend a lista de projetos fechados de um única vez.
                //public void notifyMultipleProjectClosing(List<UUID> projectsClosed) {
                //
                //List<Update<UUID, JobInfo>> list = new ArrayList<Update<UUID, JobInfo>>();
                //projectsClosed.forEach( id -> {
                //    list.add(new Update<UUID, JobInfo>(Update.Type.Delete, id, null));
                //});
                //
                // emitterActiveProjectListFlux_.onNext(list);
                //}


                if ( jobIds == null || jobIds.size() == 0 ) {
                    //If jobIds is not set then the user wants to receive the list of
                    //workflows that has been changed over the time and decide which
                    //one needs to be saved. We provide such list.

                    Map<UUID, String> projects = temporary.entrySet().stream()
                            .collect(Collectors.toMap(e -> e.getKey(), e-> e.getValue().getName()));

                    if (projects.size() > 0) {
                        return ServerResponse.getServerResponseFailed("There are still open projects", projects);
                    }
                }
                else {
                    //jobIds is set. We will decode which one and they'll be saved or discarded
                    //We get just those marked to save
                    Map<UUID, Boolean> idsToSave = temporary.entrySet().stream()
                            .filter( e-> jobIds.containsKey(e.getKey()) && jobIds.get(e.getKey()))
                            .collect(Collectors.toMap(Map.Entry::getKey, e-> Boolean.TRUE));

                    //We get those not marked to save or not in jobIds list
                    Map<UUID, Boolean> idsNotToSave = temporary.entrySet().stream()
                            .filter( e-> !jobIds.containsKey(e.getKey()) ||
                                    (jobIds.containsKey(e.getKey()) && !jobIds.get(e.getKey())))
                            .collect(Collectors.toMap(Map.Entry::getKey, e-> Boolean.FALSE));

                    Map<UUID, Boolean> map3 = Stream.concat(idsToSave.entrySet().stream(), idsNotToSave.entrySet().stream())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue));

                    final Holder<Integer> f = new Holder<>(0);

                    final Holder<String> errorMessage = new Holder<>();

                    //Saving...
                    boolean bRet = map3.entrySet().stream().noneMatch(project -> {

                        f.value++;
                        if ( project.getValue() ) {
                            //save
                            //Saves the project
                            if (this.saveTemporaryWorkflowJob(userSession, project.getKey(), errorMessage) == null) {
                                return true; //Error
                            }
                        }

                        //closes it
                        this.closeTemporaryWorkflowJob(userSession, project.getKey(), true,  null,false, true, f.value == map3.size());

                        return false;
                    });

                    if( !bRet ) {
                        //We got an error!
                        return ServerResponse.getServerResponseFailed(errorMessage);
                    }

                }

                return ServerResponse.getServerResponseSuccess();
            } finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }
        }
        catch(Exception e) {
            log.error("WorkflowEditorController::closeTemporaryWorkflowJobs: Error: ", e);
            e.getStackTrace();
            return ServerResponse.getServerResponseFailed(e.getMessage());
        }
    }

    //
    //
    //Terminates the job execution, saves and removes the job from temporary workflow job list.
    //
    public boolean saveAndCloseTemporaryWorkflowJob(UserSession userSession, UUID workflowId, Holder<ServerResponse> serverResponse, boolean emitEvent, boolean updateUI) {

        try {
            ThreadUtils.lock(this.activeProjectLock);

            try {

                Holder<String> errorMessage = new Holder<>();
                if (this.saveTemporaryWorkflowJob(userSession, workflowId, errorMessage, false) == null) {
                    HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed(errorMessage));
                    return false;
                }

                if ( !this.closeTemporaryWorkflowJob(userSession, workflowId, true, serverResponse, false, emitEvent, updateUI) ) {
                    return false;
                }

                return true;
            } finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }
        }
        catch(Exception e) {
            HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed("Failed to save and remove workflow project id " + workflowId + " from memory! Server error: " + e.getMessage() ));
            return false;
        }
    }

    //
    //
    //Terminates the job execution and removes the job from temporary workflow job list.
    //
    public boolean closeTemporaryWorkflowJob(UserSession userSession, UUID workflowId, boolean closeAnyway, Holder<ServerResponse> serverResponse, boolean useLock, boolean emitEvent, boolean updateUI) {

        try {
            if (useLock) {
                ThreadUtils.lock(this.activeProjectLock);
            }

            try {
                Holder<String> errorMessage = new Holder<>();
                Job job = findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, false, false);
                if (job == null) {
                    HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed(errorMessage));
                    return false;
                }

                //Close Anyway tells cybertron to ignore changes and close workflow project.
                if (!closeAnyway) {
                    //Checks if workflow has changed since last save
                    if (job.requireSave()) {
                        HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed("Workflow project " + job.getName() + " is still open but some of the changes are not saved. Do you want to save the changes before closing it?", ServerResponse.RESPONSE_ERROR_ACTION.SHOW_COMPLEX_QUESTION));
                        return false;
                    }
                }

                //Abort job operations
                job.abort();

                //Removes the job from the temporary job list
                Job removedJob = this.activeProjectMap_.remove(job.getId());
                if (removedJob == null) {
                    HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed("Failed to remove workflow project " + job.getName() + " from memory!"));
                    return false;
                }

                if ( emitEvent ) {

                    //Send workflow Job information to from end won't work either
                    //because the Workflow Filtering will wipe the information out
                    //and because we can't ensure the sorting order while sending
                    //data to the front end.

                    JobInfo jobInfo = JobInfo.copy(removedJob);
                    jobInfo.setRequireUIUpdate(updateUI);

                    emitterActiveProjectListFlux_.onNext(new Update<UUID, JobInfo>(Update.Type.Delete, removedJob.getId(), jobInfo));
                }

                //Close job component instances
                removedJob.dispose();

                return true;
            } finally {
                if (useLock) {
                    ThreadUtils.unlock(this.activeProjectLock);
                }
            }
        }
        catch(Exception e) {
            HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed("Failed to remove workflow project id " + workflowId + " from memory! Server error: " + e.getMessage() ));
            return false;
        }


    }

    //
    //Loads up a workflow definition in temporary workflow repository (in the memory)
    //and creates a job based on it.
    //
    public Workflow loadTemporaryWorkflowJob(UserSession userSession, UUID workflowId, boolean loadAnyway, Holder<ServerResponse> serverResponse) {

        try {

            ThreadUtils.lock(this.activeProjectLock);

            Job job = null;

            try {

                //If we're not the Workflow Supervisor we need to
                if ( !userSession.getUser().isWorkflowSupervisor() &&
                        !userSession.getUser().isAdmin()) {
                    //Closes all workflows but this (this one will remain opened, if so).
                    if (!this.closeAllOtherTemporaryWorkflowJob(userSession, workflowId, loadAnyway, serverResponse, false)) {
                        return null;
                    }
                }

                //Since temporary workflow uses the same identification as
                //temporary job entry we just need to use workflowid to locate one or another.
                job = this.findTemporaryWorkflowJob(workflowId, false);
                if (job != null) {
                    //The job already loaded in the workflow job list
                    //lets check if current user session owns the same user id
                    //or an user with some high privileges to take the ownership.
                    if (this.isWorkflowJobLocked(job, userSession)) {
                        HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed( "This workflow project is locked for another user!"));
                        return null;
                    }

                    //Takes the job ownership.
                    job.setOwnerId(userSession.getId());

                    return job.getWorkflow();
                }

                //Job workflow is not in memory, We need to locate workflow project
                Workflow workflow = this.findTemporaryWorkflow(workflowId);
                if (workflow == null) {

                    //Workflow project not found. Lets check for it in the published workflows
                    Workflow publishedWorkflow = this.workflowController.findWorkflow(workflowId);
                    if ( publishedWorkflow == null ) {
                        HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed( "Workflow project not found!"));
                        return null;
                    }

                    //Workflow project will be loaded from Published workflow
                    workflow = Workflow.copy(publishedWorkflow);
                }

                //Creates a job based upon the workflow found
                job = Job.createTemporaryJob(workflow, TriggerType.TRIGGER_TYPE_WORKFLOW_DESIGNER,"Workflow Designer", null, userSession.getId(), userSession.getUser().getFullName(), this);
                if ( job == null ) {
                    HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed(  "Temporary workflow job creation failure!"));
                    return null;
                }

                //Try to build that workflow
                Holder<String> errorMessage = new Holder<>();
                if ( !job.build(errorMessage) ) {
                    HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed(errorMessage.value));
                    return null;
                }

                this.activeProjectMap_.put(job.getId(), job);

            } finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }

            emitterActiveProjectListFlux_.onNext(new Update<UUID,JobInfo>(Update.Type.Set, job.getId(), JobInfo.copy(job)));

            return job.getWorkflow();

        } catch (Exception e) {
            HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed(e.getMessage()));
            log.error("JobController::loadTemporaryWorkflowJob: ", e);
            return null;
        }
    }

    private String getNextWorkflowValidName(final String originalName) {

        String name = new String (originalName);
        int counter = 0;

        while (true) {
            if( this.findTemporaryWorkflow(name) == null ) {
                break;
            }

            name = new String (originalName) + "(" + ++counter + ")";
        }

        return name;
    }

    public Workflow createTemporaryWorkflowJob(UserSession userSession, WorkflowRequest workflowRequest, boolean createAnyway, Holder<ServerResponse> serverResponse) {

        try {
            ThreadUtils.lock(this.activeProjectLock);

            try {
                if (workflowRequest == null) {
                    HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed("Invalid request!"));
                    return null;
                }


                //If we're not the Workflow Supervisor we need to
                if ( !userSession.getUser().isWorkflowSupervisor() &&
                        !userSession.getUser().isAdmin()) {
                    //Closes all other windows (this one will remain opened, if so).
                    if (!this.closeUserTemporaryWorkflowJobs(userSession, createAnyway, serverResponse, false)) {
                        return null;
                    }
                }


                String name = getNextWorkflowValidName(workflowRequest.getName());

                Workflow workflow = Workflow.create(name,
                        workflowRequest.getDescription(),
                        workflowRequest.getGroupId(),
                        userSession.getId(),
                        userSession.getUser().getName(),
                        userSession.getId());
                if (workflow == null) {
                    HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed("Error while creating a new temporary workflow!"));
                    return null;
                }

                Job job = Job.createTemporaryJob(workflow, TriggerType.TRIGGER_TYPE_WORKFLOW_DESIGNER,"Workflow Designer", null, userSession.getId(), userSession.getUser().getFullName(), this);

                //Adds the project job into job project list

                this.activeProjectMap_.put(job.getId(), job);

                //Stores the workflow project into database (it will be loaded into workflow list right after)
                Holder<String> errorMessage = new Holder<>();
                if (!this.workflowDesignerRepository.setWorkflow(workflow, errorMessage)) {

                    //Remove and dispose workflow job
                    this.activeProjectMap_.remove(job.getId());
                    job.dispose();

                    HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed(errorMessage));
                    return null;
                }

                emitterActiveProjectListFlux_.onNext(new Update<UUID,JobInfo>(Update.Type.Set, job.getId(), JobInfo.copy(job)));

                return workflow;
            } finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }

        }
        catch (Exception e) {
            HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed(e.getMessage()));
            log.error("JobController::createTemporaryWorkflow: ", e);
            return null;
        }

    }

    //Clears all the existing components in the workflow project.
    public Workflow clear(UserSession userSession, UUID workflowId, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return null;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return null;
        }

        if (!job.removeAllComponents()) {
            return null;
        }

        return job.getWorkflow();
    }

    //Kills the workflow project. To do so, the project must not be running
    public Workflow deleteTemporaryWorkflowProject(UserSession userSession, UUID workflowId, boolean deleteAnyway, Holder<ServerResponse> serverResponse) {

        //Try to remove it from temporary database (we don't know if it is in there, though)
        try {

            ThreadUtils.lock(this.activeProjectLock);

            try {
                //locates the temporary workflow job
                Job job = findTemporaryWorkflowJob(workflowId, false);
                if (job != null) {
                    //Workflow Project is running as a job... We need to check if we can take
                    //the project ownership.
                    if (isWorkflowJobLocked(job, userSession)) {
                        //The project is locked by someone else or user has not
                        //permission to take the ownership.
                        HolderHelper.setHolderValue(serverResponse, ServerResponse.getServerResponseFailed("This workflow is locked by someone else!"));
                        return null;
                    }

                    //Takes the job ownership.
                    job.setOwnerId(userSession.getId());

                    //User has taken project ownership.
                    //Let's close and remove it from the workflow project job list
                    if (!this.closeTemporaryWorkflowJob(userSession, workflowId, deleteAnyway, serverResponse, false, true, true)) {
                        //Couldn't close job.
                        return null;
                    }
                }

                Workflow workflow = Workflow.copy(this.findTemporaryWorkflow(workflowId));
                if (workflow == null) {
                    //should never be here
                    return null;
                }

                workflowDesignerRepository.del(workflowId);

                return workflow;
            } finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    } */

    /*

    //Changes component name and description (whenever a field is set)
    public boolean setComponentNameDescription(UserSession userSession, UUID workflowId, UUID componentId, MinimalInfo request, Holder<String> errorMessage ) {

        if (request == null || workflowId == null || componentId == null) {
            HolderHelper.setHolderValue(errorMessage, "Invalid request!");
            return false;
        }

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return false;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return false;
        }

        return job.setComponentNameDescription(componentId, request);

    }

    //Saves a component as a template
    public boolean saveComponentAsTemplate(UserSession userSession, UUID workflowId, UUID componentId, MinimalInfo request, Holder<String> errorMessage ) {

        if (request == null || workflowId == null || componentId == null) {
            HolderHelper.setHolderValue(errorMessage, "Invalid request!");
            return false;
        }

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return false;
        }

        ComponentExecuter component = job.getComponent(componentId, errorMessage);
        if ( component == null ) {
            HolderHelper.setHolderValue(errorMessage, "Component not found!");
            return false;
        }

        if ( this.componentController.addComponent(userSession.getUser(), new ComponentRequest(request.getName(),
                        request.getDescription(),
                        component.getReferenceId(),
                        true,
                        component.getProfile(),
                        userSession.getId(),
                        true,
                        null),
                errorMessage) == null ) {
            return false;
        }

        return true;
    }

    public Job findTemporaryWorkflowJob(UserSession userSession, UUID workflowId, Holder<String> errorMessage) {
        return this.findTemporaryWorkflowJob(userSession, workflowId, errorMessage, true);
    }

    public Job findTemporaryWorkflowJob(UserSession userSession, UUID workflowId, Holder<String> errorMessage, boolean useLock) {
        Job job = this.findTemporaryWorkflowJob(workflowId, useLock);
        if (job == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found!");
            return null;
        }

        return job;
    }

    public List<WorkflowInfo> findTemporaryWorkflowJobs(UserSession userSession, Holder<String> errorMessage) {
        try {

            return this.activeProjectMap_.values().stream().map(Job::getWorkflow)
                    .filter(workflow -> workflow != null && workflow.getOwnerId().equals(userSession.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            HolderHelper.setHolderValue(errorMessage, e.getMessage());
            log.error("WorkflowEditorController::findTemporaryWorkflowJobs", e);
            return null;
        }
    }

    public List<WorkflowInfo> findWorkflowProjects(UserSession userSession, Holder<String> errorMessage) {
        try {

            return this.projectMap_.values().stream()
                    .filter(workflow -> workflow != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            HolderHelper.setHolderValue(errorMessage, e.getMessage());
            log.error("WorkflowEditorController::findWorkflowProjects", e);
            return null;
        }
    }

    public List<WorkflowInfo> findPersonalWorkflowProjects(UserSession userSession, Holder<String> errorMessage) {
        try {

            return this.projectMap_.values().stream()
                    .filter(workflow -> workflow != null && workflow.getOwnerId().equals(userSession.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            HolderHelper.setHolderValue(errorMessage, e.getMessage());
            log.error("WorkflowEditorController::findPersonalWorkflowProjects", e);
            return null;
        }
    }

    //Checks if the project is in the job list and returns who has its ownership
    private boolean isTemporaryWorkflowJobLoaded(UUID workflowId, Holder<User> lockedByUser )
    {
        try {

            ThreadUtils.lock(this.activeProjectLock);
            try {
                Workflow workflow = this.projectMap_.get(workflowId);
                if ( workflow == null ) {
                    lockedByUser.value = null;
                    return false;
                }

                lockedByUser.value = this.userRepository.get(workflow.getOwnerId());
                return true;
            } finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }
        }
        catch(Exception e) {
            log.error("WorkflowEditorController::isTemporaryWorkflowJobLoaded", e);
            return false;
        }
    }

    //Transfers the workflow project to another user
    public WorkflowInfo transferWorkflowProject(UserSession userSession, UUID workflowId, UUID newOwnerId, Holder<String> errorMessage)
    {

        try {

            ThreadUtils.lock(this.projectLock);

            try {

                //Checks if we got a valid owner id
                if (!this.userSessionController.isValidUser(newOwnerId)) {
                    HolderHelper.setHolderValue(errorMessage, "Target user not found!");
                    return null;
                }

                //Checks if this project is open (it must be closed)
                //no matter by whom.
                Holder<User> user = new Holder<>();
                if ( this.isTemporaryWorkflowJobLoaded(workflowId, user) ) {
                    HolderHelper.setHolderValue(errorMessage, "Failed to assign a new user to the project. A project must be closed prior to transferring ownership. The project is currently assigned to " + user.value.getName());
                    return null;
                }

                //Locate the proper workflow project
                Workflow workflow = this.findTemporaryWorkflow(workflowId, false);
                if (workflow == null) {
                    HolderHelper.setHolderValue(errorMessage, "Workflow not found!");
                    return null; //workflow not found
                }

                if (!workflow.getOwnerId().equals(userSession.getId())) {
                    HolderHelper.setHolderValue(errorMessage, "Just workflow owner or authorized personnel may transfer workflow ownership!");
                    return null;
                }

                Job job = this.findTemporaryWorkflowJob(workflowId);
                if (job != null) {
                    HolderHelper.setHolderValue(errorMessage, "The workflow is in use! The workflow must be closed before changing its ownership.");
                    return null;
                }

                //Sets workflows new owner
                workflow.setOwnerId(newOwnerId);

                //Saves the workflow. Note that workflow structure will be
                //saved in repository and so will be loaded into memory again.
                //However the workflow data structure will not be processed during
                //reload. To prevent this we add the workflow data structure (actually
                //the entire workflow into memory before insert it into database.
                this.projectMap_.put(workflow.getId(), workflow);

                //Sends the workflow to database.
                if (!workflowDesignerRepository.setWorkflow(workflow, errorMessage)) {
                    return null;
                }

                return workflow;
            } finally {
                ThreadUtils.unlock(this.projectLock);
            }
        }
        catch(Exception e) {
            HolderHelper.setHolderValue(errorMessage, e.getMessage());
            log.error("WorkflowEditorController::transferWorkflowProject", e);
            return null;
        }
    }

    public Workflow changeWorkflowName(UserSession userSession, UUID workflowId, String name) {

        Holder<String> errorMessage = new Holder<>();

        try {

            ThreadUtils.lock(this.activeProjectLock);

            try {

                Job job = this.findTemporaryWorkflowJob(userSession, workflowId, errorMessage, false);
                if ( job == null ) {
                    return null;
                }

                job.setName(name);

                return job.getWorkflow();
            } finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }

        }
        catch(Exception e) {
            HolderHelper.setHolderValue(errorMessage, e.getMessage());
            log.error("WorkflowEditorController::changeWorkflowName", e);
            return null;
        }
    }

    public Workflow changeWorkflowDescription(UserSession userSession, UUID workflowId, String description) {

        Holder<String> errorMessage = new Holder<>();

        try {

            ThreadUtils.lock(this.activeProjectLock);

            try {

                Job job = this.findTemporaryWorkflowJob(userSession, workflowId, errorMessage, false);
                if (job == null) {
                    return null;
                }

                job.setDescription(description);

                return job.getWorkflow();

            } finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }

        }
        catch(Exception e) {
            HolderHelper.setHolderValue(errorMessage, e.getMessage());
            log.error("WorkflowEditorController::changeWorkflowDescription", e);
            return null;
        }
    }

    public Workflow changeWorkflowGroupName(UserSession userSession, UUID workflowId, String groupName) {

        Holder<String> errorMessage = new Holder<>();

        try {
            ThreadUtils.lock(this.activeProjectLock);

            try {

                Job job = this.findTemporaryWorkflowJob(userSession, workflowId, errorMessage, false);
                if (job == null) {
                    return null;
                }

                job.setGroup(groupName);

                return job.getWorkflow();

            } finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }

        }
        catch(Exception e) {
            HolderHelper.setHolderValue(errorMessage, e.getMessage());
            log.error("WorkflowEditorController::changeWorkflowGroupName", e);
            return null;
        }
    }

    //----------------------------------------------------------

    //The component is added without any profile modifications. It will use
    //default profile from plugin specifications.
    //To change profile information operators must issue profile through setComponentProfile method.
    public Workflow addComponent(UserSession userSession, UUID workflowId, WorkflowComponentRequest request, Holder<String> errorMessage) {

        if (request == null) {
            HolderHelper.setHolderValue(errorMessage, "Invalid request!");
            return null;
        }

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return null;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return null;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return null;
        }

        Component sourceComponent = this.componentController.findComponent(request.getComponentId());
        if ( sourceComponent == null ) {
            HolderHelper.setHolderValue(errorMessage, "Component not found!");
            return null;
        }
        //Creates a new component based in the original component
        Component newComponent = Component.create(sourceComponent, request.getXCoord(), request.getYCoord());

        if (!job.addComponent(newComponent, errorMessage)) {
            HolderHelper.setHolderValue(errorMessage, "Error while adding a component to temporary workflow!");
            return null;
        }

        return job.getWorkflow();
    }

    //Deletes a component and everithing related to it from the workflow project.
    public Workflow deleteComponent(UserSession userSession, UUID workflowId, List<UUID> componentIds, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return null;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return null;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return null;
        }

        for (UUID componentId : componentIds) {
            if (!job.removeComponent(componentId)) {
                HolderHelper.setHolderValue(errorMessage, "Error while removing a component to temporary workflow!");
                return null;
            }
        }

        return job.getWorkflow();
    }

    public Workflow deleteComponent(UserSession userSession, UUID workflowId, UUID componentId, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return null;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return null;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return null;
        }

        if (!job.removeComponent(componentId)) {
            HolderHelper.setHolderValue(errorMessage, "Error while removing a component to temporary workflow!");
            return null;
        }

        return job.getWorkflow();

    }

    public Profile getComponentProfile(UserSession userSession, UUID workflowId, UUID componentId, Holder<String> errorMessage) {

        Job job = findTemporaryWorkflowJob(workflowId);
        if (job == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found!");
            return null;
        }

        return job.getComponentProfile(componentId, this.resourceController, this.templateController);
    }

    //Sets a component profile in a workflow project. This will impact exclusively for the component within this project.
    public Profile setComponentProfile(UserSession userSession, UUID workflowId, UUID componentId, Profile profile, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return null;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return null;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return null;
        }

        if (!job.setComponentProfile(componentId, profile, errorMessage)) {
            return null;
        }

        return profile;
    } */

    /*

    public ConnectionAttributes getConnectionAttributes(UserSession userSession, UUID workflowId, UUID connectionId, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return null;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return null;
        }

        return job.getConnectionAttributes(connectionId, errorMessage);
    }

    public boolean setConnectionAttributes(UserSession userSession, UUID workflowId, ConnectionAttributesRequest request, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, true);
        if (job == null) {
            return false;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return false;
        }

        return job.setConnectionAttributes(request, errorMessage);
    }

    //Gets a specific component pin
    public PinInfo getPinInformation(UserSession userSession, UUID workflowId, UUID componentId, UUID id, Holder<String> errorMessage) {
        try {
            ThreadUtils.lock(this.projectLock);
            try {

                Job job = findTemporaryWorkflowJob(workflowId, false);
                if (job == null) {
                    HolderHelper.setHolderValue(errorMessage, "Workflow not found!");
                    return null;
                }

                return job.getPin(componentId, id);
            }
            finally {
                ThreadUtils.unlock(this.projectLock);
            }
        }
        catch(Exception e) {
            return null;
        }
    }

    //Makes connections between component pins.
    public PinConnection connectComponents(UserSession userSession, UUID workflowId, ConnectionRequest request, Holder<String> errorMessage) {

        if (request == null) {
            HolderHelper.setHolderValue(errorMessage, "Invalid request!");
            return null;
        }

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, true);
        if (job == null) {
            return null;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return null;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return null;
        }

        return job.connect(request.getSourceComponentId(), request.getSourcePinId(), request.getTargetComponentId(), request.getTargetPinId(), errorMessage);
    }

    public PinConnection disconnectComponents(UserSession userSession, UUID workflowId, UUID connectionId, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, true);
        if (job == null) {
            return null;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return null;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return null;
        }

        return job.disconnect(connectionId);
    }

    public boolean disconnectComponentConnections(UserSession userSession, UUID workflowId, UUID componentId, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, true);
        if (job == null) {
            return false;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return false;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return false;
        }

        return job.disconnectComponent(componentId);
    }

    public boolean disconnectAllComponents(UserSession userSession, UUID workflowId, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, true);
        if (job == null) {
            return false;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return false;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return false;
        }

        return job.disconnect();

    }

    //-------------------------------------------------------------------------------

    //
    //
    //Finds projects in which a given component is being used
    //Returns all the workflows involved or null.
    //
    //
    public Map<UUID, String> findProjectsByComponentId(UUID componentId)
    {
        try {

            ThreadUtils.lock(this.projectLock);
            try {

                return this.projectMap_.entrySet()
                        .stream()
                        .filter(w -> {
                            Map<UUID,ComponentInfo> components = w.getValue().getComponents();
                            if ( components == null ) {
                                return false;
                            }

                            if ( !components.containsKey(componentId) ) {
                                return false;
                            }

                            return true;
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));
            }
            finally {
                ThreadUtils.unlock(this.projectLock);
            }

        }
        catch (Exception e) {
            log.error("WorkflowEditorController:findProjectsByComponentId. Error: ", e);
            e.printStackTrace();
            return new LinkedHashMap<>();
        }

    }

    //
    //
    //Finds active projects in which a given component is being used.
    //Returns all the workflows involved or null.
    //
    //
    public Map<UUID, String> findActiveProjectsByComponentId(UUID componentId)
    {
        try {


            ThreadUtils.lock(this.activeProjectLock);

            try {

                return this.activeProjectMap_.entrySet()
                        .stream()
                        .filter(w -> {
                            WorkflowInfo workflow = w.getValue().getWorkflow();
                            if ( workflow == null ) {
                                return false;
                            }

                            Map<UUID, ComponentInfo> components = workflow.getComponents();

                            if ( components == null || !components.containsKey(componentId) ) {
                                return false;
                            }

                            return true;
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));
            }
            finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }

        }
        catch (Exception e) {
            log.error("WorkflowEditorController:findActiveProjectsByComponentId. Error: ", e);
            e.printStackTrace();
            return new LinkedHashMap<>();
        }

    }

    //Searches for a given component in both closed and active projects.
    public Map<UUID, String> findProjectByComponent(UUID componentId) {
        Map<UUID, String> list1 = findProjectsByComponentId(componentId);
        if ( list1 == null ) {
            return null;
        }

        Map<UUID, String> list2 = this.findActiveProjectsByComponentId(componentId);
        if ( list2 == null )  {
            return null;
        }

        list1.putAll(list2);
        return list1;
    }

    //
    //
    //Finds projects in which a given plugin is being used
    //Returns all the workflows involved or null.
    //
    //
    public Map<UUID, String> findProjectsByPluginId(UUID pluginId)
    {
        try {

            ThreadUtils.lock(this.projectLock);
            try {

                return this.projectMap_.entrySet()
                        .stream()
                        .filter(w -> {
                            Map<UUID,ComponentInfo> components = w.getValue().getComponents();
                            if ( components == null ) {
                                return false;
                            }

                            if ( components.values().stream()
                                    .filter(c -> c.getPluginId().equals(pluginId))
                                    .findAny().orElse(null) == null ) {
                                return false;
                            }

                            return true;
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));
            }
            finally {
                ThreadUtils.unlock(this.projectLock);
            }

        }
        catch (Exception e) {
            log.error("WorkflowEditorController:findProjectsByPluginId. Error: ", e);
            e.printStackTrace();
            return new LinkedHashMap<>();
        }

    }

    //
    //
    //Finds active projects in which a given plugin is being used.
    //Returns all the workflows involved or null.
    //
    //
    public Map<UUID, String> findActiveProjectByPluginId(UUID pluginId)
    {
        try {

            ThreadUtils.lock(this.activeProjectLock);
            try {
                return this.activeProjectMap_.entrySet()
                        .stream()
                        .filter(w -> {
                            WorkflowInfo workflow = w.getValue().getWorkflow();
                            if ( workflow == null ) {
                                return false;
                            }

                            Map<UUID, ComponentInfo> components = workflow.getComponents();

                            if ( components == null ) {
                                return false;
                            }

                            if ( components.values().stream()
                                    .filter(c -> c.getPluginId().equals(pluginId))
                                    .findAny().orElse(null) == null ) {
                                return false;
                            }

                            return true;
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));
            }
            finally {
                ThreadUtils.unlock(this.activeProjectLock);
            }

        }
        catch (Exception e) {
            log.error("WorkflowEditorController:findActiveProjectsByPluginId. Error: ", e);
            e.printStackTrace();
            return new LinkedHashMap<>();
        }

    }

    //Searches for a given plugin in both closed and active projects.
    public Map<UUID, String> findProjectsByPlugin(UUID pluginId) {
        Map<UUID, String> list1 = findProjectsByPluginId(pluginId);
        if ( list1 == null ) {
            return null;
        }

        Map<UUID, String> list2 = this.findActiveProjectByPluginId(pluginId);
        if ( list2 == null )  {
            return null;
        }

        list1.putAll(list2);
        return list1;
    }

    //---------------------------------------------------------------------------------

    //Copies a component in the workflow workbench
    public boolean copyComponent(UserSession userSession, UUID workflowId, ComponentLocation request, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, true);
        if (job == null) {
            return false;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return false;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return false;
        }

        return job.copyComponent(request);
    }

    //Moves a component in the workflow workbench
    public boolean moveComponent(UserSession userSession, UUID workflowId, ComponentLocation request, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, true);
        if (job == null) {
            return false;
        }

        if (job.isStatusRunning() || job.isStatusPaused() || job.isStatusStep() ) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project is running!");
            return false;
        }

        if (job.getWorkflow() == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow not found in temporary workflow list!");
            return false;
        }

        return job.moveComponent(request);


    }

    //
    //Publishes the temporary workflow defined by temporary job.
    //
    //
    //
    public Workflow publishTemporaryWorkflow(UserSession userSession, WorkflowRequest request, Holder<String> errorMessage) {

        Job job = findTemporaryWorkflowJobAndLock(request.getId(), userSession, errorMessage, true, true);
        if (job == null) {
            HolderHelper.setHolderValue(errorMessage, "Workflow project not found!");
            return null;
        }

        //Checks if there is a workflow and components on it
        if (job.getWorkflow() == null ) {
            HolderHelper.setHolderValue(errorMessage, "The workflow project is invalid!");
            return null;
        }

        if (job.getComponentCount() == 0) {
            HolderHelper.setHolderValue(errorMessage, "The workflow project is empty! There must have at least one component on it!");
            return null;
        }

        //Prevents a workflow to be published without all the resources assigned.
        Holder<List<String>> componentList = new Holder<>();
        if (!job.getMissingResourceComponentNameList(componentList)) {

            String componentListNameStr = "";
            for (String s : componentList.value) {
                componentListNameStr = componentListNameStr.concat(s + "\r\n");
            }
            HolderHelper.setHolderValue(errorMessage, "Some of the component resources are missing! Please check the resource attribution in the following component(s): " + componentListNameStr);
            return null;
        }

        //Checks if resources set in component profiles are still valid in resource controller.
        if (!job.getInvalidResourceComponentNameList(this.resourceController, componentList)) {

            String componentListNameStr = "";
            for (String s : componentList.value) {
                componentListNameStr = componentListNameStr.concat(s + "\r\n");
            }
            HolderHelper.setHolderValue(errorMessage, "Some of the component resources are not valid! Please check the resource attribution in the following component(s): " + componentListNameStr);
            return null;
        }

        //Makes a copy of this workflow because we're gonna need to change
        //locker property.
        Workflow workflow = Workflow.copy(job.getWorkflow());

        //We want to publish the workflow as a new one. So we need to generate the workflow id.
        if( request.getRegen() ) {
            workflow.setId(UUID.randomUUID());
        }

        //We can change some information in the workflow before publishing it.
        if ( request.getName() != null && !request.getName().isEmpty() ) {
            workflow.setName(request.getName());
        }

        if ( request.getDescription() != null && !request.getDescription().isEmpty() ) {
            workflow.setDescription(request.getDescription());
        }

        if ( request.getGroupId() != null ) {
            workflow.setWorkflowGroupId(request.getGroupId());
        }

        //Event number must be set to zero. So that next time
        //events will be processed always from the beginning.
        workflow.setEventNumber(0);

        //Resets locker
        workflow.setLockedById(null);

        //Inserts the current workflow into main workflow list and returns
        return this.workflowController.create(workflow, errorMessage);
    }

    public List<MissingDataContainerInfo> getWorkflowMissingFields(UserSession userSession, UUID workflowId, Holder<String> errorMessage ) {
        try {
            Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, true);
            if (job == null) {
                return null;
            }

            return job.getMissingFields(errorMessage);
        }
        catch(Exception e){
            return null;
        }
    }

    public List<MissingDataContainerInfo> setWorkflowMissingFields(UserSession userSession, UUID workflowId, List<MissingDataContainerInfo> missingValues, Holder<String> errorMessage ) {
        try {
            Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, true);
            if (job == null) {
                return null;
            }

            return job.setMissingFields(missingValues, errorMessage);
        }
        catch(Exception e) {
            return null;
        }
    }

    public boolean runTemporaryWorkflowJob(UserSession userSession, UUID workflowId, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true, true);
        if (job == null) {
            return false;
        }

        return job.run(errorMessage);
    } */

    /*
    public boolean pauseTemporaryWorkflowJob(UserSession userSession, UUID workflowId, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return false;
        }

        job.pause();

        return true;
    }

    public boolean stopTemporaryWorkflowJob(UserSession userSession, UUID workflowId, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return false;
        }

        job.abort();

        return true;
    }

    public boolean stepTemporaryWorkflowJob(UserSession userSession, UUID workflowId, Holder<String> errorMessage) {

        Job job = this.findTemporaryWorkflowJobAndLock(workflowId, userSession, errorMessage, true,true);
        if (job == null) {
            return false;
        }

        job.step();

        return true;
    } */

    /*

    public Flux<Update<UUID,JobInfo>> getActiveProjectListFlux() {
        try {
            return Flux.defer(this::createActiveProjectList)
                    .concatWith(this.emitterActiveProjectListFlux_.getProcessor())
                    .onBackpressureBuffer()
                    .doOnComplete(() -> log.warn("ActiveProjectList: Complete"))
                    .doOnCancel(() -> log.warn("ActiveProjectList: Cancel"));
        }
        catch(Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }

    Flux<Update<UUID,JobInfo>> createActiveProjectList() {
        try {
            ThreadUtils.lock(projectLock);
            try {
                return Flux.fromIterable(this.activeProjectMap_.entrySet()
                        .stream()
                        .map(e -> new Update<UUID, JobInfo>(Update.Type.Set, e.getKey(), JobInfo.copy(e.getValue())))
                        .collect(Collectors.toList()));
            }
            finally {
                ThreadUtils.unlock(projectLock);
            }
        }
        catch(Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }

    //------------------------------------------------------------------------------------------------
    public Flux<Update<UUID,WorkflowInfo>> getProjectListFlux() {
        try {

            return Flux.defer(this::createProjectList)
                    .concatWith(this.emitterProjectListFlux_.getProcessor())
                    .onBackpressureBuffer()
                    .doOnComplete(() -> log.warn("ProjectList: Complete"))
                    .doOnCancel(() -> log.warn("ProjectList: Cancel"));
        }
        catch(Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }

    Flux<Update<UUID,WorkflowInfo>> createProjectList() {
        try {
            ThreadUtils.lock(projectLock);
            try {
                return Flux.fromIterable(this.projectMap_.entrySet()
                        .stream()
                        .map(e -> new Update<UUID, WorkflowInfo>(Update.Type.Set, e.getKey(), WorkflowInfo.copy(e.getValue())))
                        .collect(Collectors.toList()));
            }
            finally {
                ThreadUtils.unlock(projectLock);
            }
        }
        catch(Throwable t) {
            return Flux.error(new SSEException(t.getMessage()));
        }
    }

    // --------------------------------------------------------------------------

    public Flux<InfoEx<UUID, Object>> getWorkflowEditorJobEventsFlux(UUID jobId, int eventNumber) {

        Job job = this.findTemporaryWorkflowJob(jobId);
        if (job == null) {
            return Flux.error(new ProjectNotFoundException("Project not found " + jobId));
        }

        return job.getJobEventsFlux(eventNumber);
    }

    //WARNING: This routine is not finished!
    public boolean clearLog(UUID id) {

        Job job = this.findTemporaryWorkflowJob(id);
        if (job == null) {
            return false;
        }

        //TODO: Clear job entries (if any)...
        //job.clearLog();

        return true;
    }*/
}