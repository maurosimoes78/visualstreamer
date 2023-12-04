package org.nomanscode.visualstreamer.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nomanscode.visualstreamer.common.*;
//import org.nomanscode.visualstreamer.sdk.component.pin.InputPinInfo;
//import org.nomanscode.visualstreamer.sdk.component.pin.OutputPinInfo;
import org.nomanscode.visualstreamer.common.interfaces.IComponentControl;
//import org.nomanscode.visualstreamer.service.interfaces.IPlugin;
import org.nomanscode.visualstreamer.exceptions.CybertronException;
import org.nomanscode.visualstreamer.common.ComponentExecuter;

import lombok.extern.slf4j.Slf4j;

//import javax.xml.ws.Holder;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

@Slf4j
public class Plugin extends PluginInfo {

    @JsonIgnore
    private Class<?/* extends ComponentExecuter*/> class_;

    @JsonIgnore
    private IComponentControl interface_;

    @JsonIgnore
    private Object instance_;

    @JsonIgnore
    private Manifest manifest_;

    @JsonIgnore
    private URLClassLoader classLoader_;

    @JsonIgnore
    private String className_;

    @JsonIgnore
    private boolean ready_ = false;

    @JsonIgnore
    public Plugin(final String path) throws CybertronException
    {
        super();

        this.setId(UUID.randomUUID());
        this.setEnabled(true);

        if ( path == null || path.isEmpty() ) {
            throw new CybertronException (ErrorCode.PLUGIN_ERROR, "Error while loading plugin", "Undefined path");
        }

        if( !this.load(path) ) {
            throw new CybertronException(ErrorCode.PLUGIN_ERROR, "Error while loading plugin", "Path " + path + " not found");
        }

        this.setName( this.getPluginName());

        this.ready_ = true;
    }

    @JsonIgnore
    public Plugin(final String name, final String description, final String path, final boolean enabled) throws CybertronException
    {
        super();

        this.setId(UUID.randomUUID());

        if ( path != null ) {
            this.setPath(path);
        }

        if ( name != null ) {
            this.setName(name);
        }

        if ( description != null ) {
            this.setDescription(description);
        }

        this.setEnabled(enabled);

        if ( path == null || path.isEmpty() ) {
            throw new CybertronException (ErrorCode.PLUGIN_ERROR, "Error while loading plugin", "Undefined path");
        }

        if( !this.load(path) ) {
            throw new CybertronException(ErrorCode.PLUGIN_ERROR, "Error while loading plugin", "Path " + path + " not found");
        }

        this.ready_ = true;
    }

    @JsonIgnore
    public Plugin(final PluginInfo plugin) throws CybertronException
    {
        super (plugin.getId(),
                plugin.getName(),
                plugin.getDescription(),
                plugin.getPath(),
                plugin.getEnabled(),
                plugin.getDeletable(),
                plugin.isBuiltIn(),
                plugin.getIcon(),
                plugin.getColor());

        if( !this.load(this.getPath()) ) {
            throw new CybertronException(ErrorCode.PLUGIN_ERROR, "Error while loading plugin", "Path " + plugin.getPath() + " not found");
        }

        this.ready_ = true;
    }

    @JsonIgnore
    public Plugin(ComponentExecuter instance, boolean enabled, boolean deletable) throws CybertronException {
        super(instance.getId(), instance.getName(), instance.getDescription(), "", enabled, deletable, true, null, null);

        try {

            this.instance_ = instance;
            this.interface_ = (IComponentControl) this.instance_;

            super.setPluginName( instance.getName());

            if ( super.getName() == null ||
                    super.getName().isEmpty() ) {
                super.setName(super.getPluginName());
            }

            super.setVersion(instance.getVersion());
            super.setVendor( instance.getVendor());
            super.setProductIdentificationId( instance.getProductIdentificationId());
            super.setProfile( instance.getProfile());

            super.setNumberOfPins(instance.getNumberOfPins());
            super.getInputPins().putAll(instance.getInputPins());
            super.getOutputPins().putAll(instance.getOutputPins());

            super.setType( instance.getType());
            super.setGroupTypes( instance.getGroupTypes());

            super.setIcon( instance.getIcon() );
            super.setColor( instance.getColor() );

            this.ready_ = true;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {

        }
    }

    @JsonIgnore
    public void dispose() {

        super.setPluginName("");
        super.setName("");


        super.setVersion("");
        super.setVendor("");
        super.setProductIdentificationId(null);
        super.setProfile(null);

        super.setNumberOfPins(0);
        super.getInputPins().clear();
        super.getOutputPins().clear();

        super.setType(null);

        this.ready_ = false;
        this.class_ = null;
        this.className_ = "";
        this.instance_ = null;
        this.interface_ = null;

        if ( this.manifest_ != null) {
            this.manifest_.clear();
            this.manifest_ = null;
        }

        try {
            if ( this.classLoader_ != null ) {
                this.classLoader_.close();
                this.classLoader_ = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @JsonIgnore
    public boolean isReady()
    {
        return ready_;
    }

    @JsonIgnore
    public PluginInfo getPluginInfo()
    {
        return (PluginInfo) this;
    }

    @JsonIgnore
    private void enumerateManifestEntries(JarFile jf)
    {
        try {
            Enumeration<JarEntry> entries = jf.entries();
            while ( entries.hasMoreElements() ) {
                String entry = entries.nextElement().getName();
                System.out.println(entry);
            }
        }
        catch(Exception e)
        {

        }
        finally
        {

        }
    }

    @JsonIgnore
    private Manifest getManifest(JarURLConnection jarUrl)
    {
        JarFile jf = null;
        try {

            jf = jarUrl.getJarFile();

            this.enumerateManifestEntries(jf);

            return jf.getManifest();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try {
                if ( jf != null) {
                    jf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @JsonIgnore
    private Boolean loadManifest(String path)
    {
        try {
            //"jar:file:/e:/cybertron/out/artifacts/mp_transform/mp_transform.jar!/");
            URL url = new URL("jar:file:/" + path + "!/");

            JarURLConnection jarUrl = (JarURLConnection) (url.openConnection());

            this.manifest_ = this.getManifest(jarUrl);

            if (this.manifest_ == null) {
                return false; //Manifest is necessary to find main class
            }

            //this.manifest_.getEntries().forEach((key, value) -> System.out.println(key + ": " + value));

            this.className_ = this.manifest_.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
            if (this.className_ == null || this.className_.isEmpty()) {
                return false;
            }

            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally {

        }
    }

    //A new instance of a given plugin is created. input or output pins are created
    //by the plugin itself (that is as implemented to be).
    @JsonIgnore
    public ComponentExecuter getNewInstance(/*IJobExecuter job, IJobController jobController, */ComponentInfo component)
    {
        try {
            if (this.instance_ == null) {
                return null;
            }

            ComponentExecuter instance = (ComponentExecuter) this.instance_.getClass()
                    .getDeclaredConstructor(/*IJobExecuter.class, IJobController.class*/)
                    .newInstance(/*job, jobController*/);

            if (instance == null ) {
                //Error while instantiating the plugin...
                return null;
            }

            //instance.setType( this.getPluginType(this.instance_));

            if ( component != null ) {
                instance.setup( component.getId(),
                                component.getPluginId(),
                                component.getName(),
                                component.getDescription(),
                                component.getEnabled(),
                                component.getDeletable(),
                                component.getXCoord(),
                                component.getYCoord(),
                                component.getProfile());
            }
            else {
                instance.setup();
            }



            return instance;
        }
        catch(NoSuchMethodException e)
        {
            return null;
        }
        catch(Exception e)
        {
            return null;
        }
        finally
        {

        }
    }

    //Creates a plugin instance based on another plugin. However
    //
    @JsonIgnore
    public ComponentExecuter createNewInstanceBy(/*IJobExecuter job, IJobController jobController,*/PluginInfo pluginInfo, String nameSufix, long xCoord, long yCoord)
    {
        try {

            if (this.instance_ == null) {
                return null;
            }

            ComponentExecuter instance = (ComponentExecuter) this.instance_.getClass()
                    .getDeclaredConstructor(/*IJobExecuter.class, IJobController.class*/)
                    .newInstance(/*job, jobController*/);

            if (instance == null ) {
                //Error while instantiating the plugin...
                return null;
            }

            if ( pluginInfo == null ) {
                //There must have a plugin information
                return null;
            }

            instance.setup( UUID.randomUUID(),      //A new id is given
                    pluginInfo.getReferenceId(),             //this will be the reference id
                    !nameSufix.isEmpty() ? pluginInfo.getName() + " " + nameSufix : pluginInfo.getName(),
                    pluginInfo.getDescription(),
                    pluginInfo.getEnabled(),
                    pluginInfo.getDeletable(),
                    xCoord,
                    yCoord,
                    Profile.copy(pluginInfo.getProfile()),
                    null,
                    null);


            return instance;
        }
        catch(NoSuchMethodException e)
        {
            return null;
        }
        catch(Exception e)
        {
            return null;
        }
        finally
        {

        }
    }

    @JsonIgnore
    public ComponentExecuter getNewInstance(/*IJobExecuter job, IJobController jobController*/)
    {
        return this.getNewInstance(/*job, jobController, */null);
    }

    @JsonIgnore
    private boolean extractLibraries(String path, String destination, MyHolder<String> errorMessage) {

        try {
            String resources = this.manifest_.getMainAttributes().getValue("libraries");
            if (resources == null || resources.isEmpty()) {
                return true;
            }

            URL[] urls = new URL[]{new URL("file:/" + path)};
            URLClassLoader classLoader = new URLClassLoader(urls);

            boolean b = Arrays.stream(resources.split(";")).anyMatch(f -> {

                try {
                    InputStream resource = classLoader.getResourceAsStream("/src/main/resources/libraries/" + f);
                    //InputStream resource = new ClassPathResource("/libraries/" + f).getInputStream();
                    byte[] buffer = new byte[resource.available()];
                    resource.read(buffer);

                    String a = Paths.get(path).getParent().getFileName().toAbsolutePath().toString();
                    Path path1 = Paths.get(a, Paths.get(f).toAbsolutePath().toString());
                    String s = path1.toAbsolutePath().toString();
                    File targetFile = new File(s);
                    OutputStream outStream = new FileOutputStream(targetFile);
                    outStream.write(buffer);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
                    String employees = reader.lines().collect(Collectors.joining("\n"));

                    return false;

                } catch (Exception e) {
                    return true;
                }
            });

            return b;
        }
        catch (Exception e) {
            return false;
        }
    }
/*
            List<String> paths = Arrays.stream(items)
                    .map( p-> {
                        URL f = classLoader.getResource("/libraries/" + p);
                        if ( f == null ) {
                            return null;
                        }

                        String g = f.getFile();
                        return g;
                    } )
                    .collect(Collectors.toList());

            List<File> files = paths.stream().map(File::new)
                    .collect(Collectors.toList());

            try {
                FileCopy gFC = new FileCopy(32768, 1, 1);

                //returns FileCopyException exception when fails (return will always be true).
                gFC.copy(files, new File(destination), ChecksumType.CRC32_CHECKSUM);
            }
            catch(FileCopyException e) {
                log.error(e.getMessage(), e);
                HolderHelper.setHolderValue(errorMessage, e.getMessage());
                return false;
            }

            return true;
        }
        catch(Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }*/


    @JsonIgnore
    private Boolean loadClass(String path)
    {
        try {
            URL[] classLoaderUrls = new URL[]{new URL("file:/" + path)};
            //"file:/e:/cybertron/out/artifacts/mp_transform/mp_transform.jar")};

            this.classLoader_ = new URLClassLoader(classLoaderUrls);

            this.class_ = (Class<?/* extends ComponentExecuter*/>) this.classLoader_.loadClass(this.className_);

            this.instance_ = this.class_.getDeclaredConstructor(/*IJobExecuter.class, IJobController.class*/)
                                        .newInstance(/*null, null*/);

            this.interface_ = (IComponentControl) this.instance_;

            /*
            //For testing resource checking purpose (you may ignore it)
            PluginResourceEntry entry = this.interface_.getResourceList().get(0);
            Resource resource = new Resource(null, entry.getName(), entry.getDescription(), true, null, false,ResourceType.RESOURCE_TYPE_WEB_SERVICE,  "127.0.0.1" , "admin" , "admin", 0, true, 1000, 0, 0, null, entry.getId() );


            if (!this.interface_.checkResourceImpl(resource)) {
                log.info("Plugin XPTO resource check failed");
            }*/

            return true;
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
            return false;
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        catch(NoSuchMethodException e)
        {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {

        }

    }

    @JsonIgnore
    public boolean load(final String path) {

        try {

            this.setPath(path);

            if (!loadManifest(path)) {
                return false;
            }

            if (!extractLibraries(path, new File(path).getParent(), null)) {
                return false;
            }

            if (!loadClass(path)) {
                return false;
            }

            ComponentExecuter plugin = (ComponentExecuter) this.instance_;
            plugin.setup();

            //Get plugin data and metadata settings
            super.setPluginName( plugin.getName());

            if ( super.getName() == null ||
                    super.getName().isEmpty() ) {
                super.setName(super.getPluginName());
            }

            super.setVersion(plugin.getVersion());
            super.setVendor( plugin.getVendor());
            super.setProductIdentificationId( plugin.getProductIdentificationId());
            super.setProfile( plugin.getProfile());

            super.setNumberOfPins(plugin.getNumberOfPins());
            super.getInputPins().putAll(plugin.getInputPins());
            super.getOutputPins().putAll(plugin.getOutputPins());

            super.setType( plugin.getType());
            super.setGroupTypes(plugin.getGroupTypes());

            String icon = plugin.getIcon();
            if ( icon == null ) {
                icon = loadDefaultIcon();
            }

            super.setIcon( icon );

            /*
            //Get plugin data and metadata settings
            super.setPluginName( this.getName( this.instance_ ));

            if ( super.getName() == null ||
                 super.getName().isEmpty() ) {
                super.setName(super.getPluginName());
            }

            super.setVersion(this.getVersion( this.instance_ ));
            super.setVendor( this.getVendor(this.instance_));
            super.setProductIdentificationId( this.getProductIdentificationId(this.instance_));
            super.setProfile( this.getProfile(this.instance_));

            super.setNumberOfPins(this.getNumberOfPins(this.instance_));
            super.getInputPins().putAll(this.getInputPins(this.instance_));
            super.getOutputPins().putAll(this.getOutputPins(this.instance_));

            super.setType( this.getPluginType(this.instance_));

            String icon = this.loadIcon(this.instance_);
            if ( icon == null ) {
                icon = loadDefaultIcon();
            }

            super.setIcon( icon );*/

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {

        }
    }

    private String loadDefaultIcon() throws IOException {

        return "fas fa-puzzle-piece"; //font awesome component padrao
    }

    @JsonIgnore
    private Object callMethod(String name, /* nullable */ Object instance, Object obj) {
        try {

            if ( instance == null ) {
                instance = this.instance_;
            }

            Method method = this.class_.getMethod(name, Profile.class);
            return method.invoke(instance, obj);
        }
        catch(Exception e)
        {
            return null;
        }
        finally
        {

        }
    }

    @JsonIgnore
    private Object callMethod(String name, /* nullable */ Object instance, Object obj, /* nullable */ Object obj2) {
        try {

            if ( instance == null ) {
                instance = this.instance_;
            }

            Method method = this.class_.getMethod(name, Profile.class);
            return method.invoke(instance, obj, obj2);
        }
        catch(Exception e)
        {
            return null;
        }
        finally
        {

        }
    }

    @JsonIgnore
    public Object callMethod(String name, /* nullable */ Object instance) {
        try {

            if ( instance == null ) {
                instance = this.instance_;
            }

            Method method = this.class_.getMethod(name);
            return method.invoke(instance);
        }
        catch(Exception e)
        {
            return null;
        }
        finally
        {

        }
    }

    @JsonIgnore
    public List<?> getMinimumParameters()
    {
        try {
            /*Method method = this.class_.getMethod("getMinimumParameters");
            method.invoke(this.instance_);*/
            return (List<?>) callMethod("getMinimumParameters" , this.instance_);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {

        }
    }

    // --------------------------------------------------------------------------
/*
    @JsonIgnore
    private String getName(Object instance) { return (String) callMethod( "getName", instance); }

    @JsonIgnore
    private String getVendor(Object instance) { return (String) callMethod ( "getVendor", instance); }

    @JsonIgnore
    private String getVersion(Object instance) { return (String) callMethod ( "getVersion", instance); }

    @JsonIgnore
    private String getDescription(Object instance) { return (String) callMethod ( "getDescription", instance); }

    @JsonIgnore
    private UUID getProductIdentificationId(Object instance) { return (UUID) callMethod("getProductIdentificationId", instance); }

    @JsonIgnore
    private PluginType getPluginType(Object instance) { return (PluginType) callMethod( "getType", instance); }

    @JsonIgnore
    private String loadIcon(Object instance) { return (String) callMethod( "loadIcon", instance); }

    @JsonIgnore
    private Profile getProfile(Object instance) {
        return (Profile) callMethod( "getProfile", instance);
    }

    @JsonIgnore
    public void resetProfile(Object instance) {
        callMethod( "resetProfile", instance);
    }

    @JsonIgnore
    public Profile setCurrentProfile(Object instance, Profile profile) {
        return (Profile) callMethod( "setCurrentProfile", instance, profile);
    }
*/
/*
    @JsonIgnore
    public boolean checkProfileChanges(Object instance, Profile profile) {
        return (boolean) callMethod( "checkProfileChanges", instance, profile);
    }

    @JsonIgnore
    private Integer getNumberOfPins(Object instance) {
        return (Integer) callMethod( "getNumberOfInstancedPins", instance);
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    private Map<UUID, PinInfo> getInputPins(Object instance) { return (Map<UUID, PinInfo>) callMethod("getInputPins", instance); }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    private Map<UUID, PinInfo> getOutputPins(Object instance) { return (Map<UUID, PinInfo>) callMethod("getOutputPins", instance); }

    // --------------------------------------------------------------------------

    @JsonIgnore
    public String getTag(Object instance) {
        return (String) callMethod("getTag", instance);
    }

    @JsonIgnore
    public UUID getUUID(Object instance) {
        return (UUID) callMethod("getUUID", instance);
    }

    @JsonIgnore
    public void run(Object instance) {
        callMethod("run", instance);
    }

    @JsonIgnore
    public void pause(Object instance) {
        callMethod("pause", instance);
    }

    @JsonIgnore
    public void abort(Object instance) {
        callMethod("abort", instance);
    }

    @JsonIgnore
    public void step(Object instance) {
        callMethod("step", instance);
    }

    @JsonIgnore
    public int getProgress(Object instance) {
        return (Integer) callMethod("getProgress", instance);
    }

    @JsonIgnore
    public ComponentStatus getStatus(Object instance) {
        return (ComponentStatus) callMethod("getStatus", instance);
    }

    @JsonIgnore
    public void clearParams(Object instance) {
        callMethod("clearParams", instance);
    }

    @JsonIgnore
    public void addParams(Object instance, ParameterData params, Holder<String> errorMessage) {
        callMethod("addParams", instance, params, errorMessage);
    }
*/
    @JsonIgnore
    public boolean checkProfileChanges(Profile profile, MyHolder<String> errorMessage) {
        return this.interface_.checkProfileChanges(profile, errorMessage);
    }

    public IComponentControl getInterface()
    {
        return this.interface_;
    }

    //-----------------------------------
    @JsonIgnore
    public static Plugin create(PluginRequest request) {

        if ( request == null ) {
            return null;
        }

        return new Plugin(request.getName(),
                request.getDescription(),
                request.getPath(),
                request.getEnabled());
    }

    @JsonIgnore
    public static Plugin create(PluginInfo pluginInfo) {
        return new Plugin(pluginInfo);
    }

    @JsonIgnore
    public static Plugin create(ComponentExecuter instance,  boolean enabled, boolean deletable) {
        return new Plugin(instance, enabled, deletable);
    }
}