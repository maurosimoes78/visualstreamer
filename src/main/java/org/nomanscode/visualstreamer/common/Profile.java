package org.nomanscode.visualstreamer.common;


import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.*;
import org.nomanscode.visualstreamer.common.interfaces.IProfile;

//import org.nomanscode.visualstreamer.sdk.common.serializers.ResourceKeySerializer;

@SuppressWarnings("unchecked")
class Deserializer extends JsonDeserializer<Profile> {

    @Override
    public Profile deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        return getProfile(node);
    }

    private Object getValue(JsonNode node, String fieldName, String className) {

        try {

            Object value = node.get(fieldName);

            if (className == null || className.isEmpty()) {
                return null;
            }

            Class<?> c = null;
            try {
                c = Class.forName(className);
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
                //return null;
            }

            //Since new value has a different type of object, we're gonna
            //need to convert the new value to our object type
            /*if (c.isInstance(value)) {
                return value;
            }*/

            if (c != null && c.isEnum()) {

                Class<? extends Enum> v = (Class<? extends Enum>) c;

                if (value instanceof Integer ||
                        value instanceof Long ||
                        value instanceof LongNode ||
                        value instanceof IntNode) {

                    long v2 = Long.parseLong(String.valueOf(value));
                    for (Enum enumConstant : v.getEnumConstants()) {
                        if (enumConstant.ordinal() == v2) {
                            return enumConstant;
                        }
                    }

                    return null;

                } else if (value instanceof String ||
                        value instanceof TextNode) {
                    String name = value.toString().replace("\"", "");
                    return Enum.valueOf(v, name);
                }

                return null;
            }

            if (node.get(fieldName) == null || node.get(fieldName).isNull()) {
                return null;
            }

            switch (className) {
                case "org.nomanscode.visualstreamer.common.ComplexTypes":
                    return getComplexType(node.get(fieldName));
                /*case "org.nomanscode.visualstreamer.common.ProfileResource":
                    return getProfileResource(node.get(fieldName));
                case "org.nomanscode.visualstreamer.common.ProfileTemplate":
                    return getProfileTemplate(node.get(fieldName));
                case "org.nomanscode.visualstreamer.common.ProfileStorage":
                    return getProfileStorage(node.get(fieldName));
                case "org.nomanscode.visualstreamer.common.ProfileWatchFolder":
                    return getProfileWatchFolder(node.get(fieldName));
                case "org.nomanscode.visualstreamer.common.CybertronList":
                    return getCybertronList(node.get(fieldName));*/
                case "java.lang.Boolean":
                    return node.get(fieldName).asBoolean();
                case "java.lang.String":
                case "cybertron.lang.Script":
                    return node.get(fieldName).asText();
                case "java.lang.Integer":

                    if ( node.get(fieldName) instanceof TextNode ) {
                      if ( node.get(fieldName).asText().isEmpty()) {
                          return null;
                      }
                    }
                    return node.get(fieldName).asInt();
                case "java.lang.Long":
                    return node.get(fieldName).asLong();
                case "java.lang.Float":
                case "java.lang.Double":
                    return node.get(fieldName).asDouble();

                default:
                    //unknown class (return as text and let the plugin itself to resolve this)
                    return node.get(fieldName).asText();
            }


            /*if (value instanceof IntNode) {
                return ((IntNode) value).intValue();
            } else if (value instanceof Integer) {
                return (Integer) value;
            }

            if (value instanceof LongNode) {
                return ((LongNode) value).longValue();
            } else if (value instanceof Long) {
                return (Long) value;
            }

            if (value instanceof BooleanNode) {
                return ((BooleanNode) value).booleanValue();
            } else if (value instanceof Boolean) {
                return (Boolean) value;
            }

            if (value instanceof ObjectNode) {

            }

            if ( value instanceof String ||
                 value instanceof TextNode) {
                return value.toString().replace("\"", "");
            }

            return value.toString().replace("\"", "");*/
        }
        catch(Exception e) {
            return null;
        }
    }

    private JsonNode getNode(JsonNode node, String fieldName) {
        if ( node == null ) {
            return null;
        }

        JsonNode node2 = node.get(fieldName);
        if ( node2 == null || node2.isNull() ) {
            return null;
        }

        return node2;
    }

    private String getTextSafe(JsonNode node, String fieldName) {

        JsonNode node2 = getNode(node, fieldName);
        if( node2 == null ) {
            return null;
        }

        return node2.asText();
    }

    private UUID getUUIDSafe(JsonNode node, String fieldName) {

        String uuid = getTextSafe(node, fieldName);
        if ( uuid == null ) {
            return null;
        }

        return UUID.fromString(uuid);
    }

    private boolean getBooleanSafe(JsonNode node, String fieldName) {

        JsonNode node2 = getNode(node, fieldName);
        if ( node2 == null ) {
            return false;
        }

        return node2.asBoolean();
    }

    /*private ProfileResourceEntry getProfileEntryResource(JsonNode node) {

        try {
            String name = getTextSafe(node, "name");
            String description = getTextSafe(node,"description");

            UUID id = getUUIDSafe(node,"id");
            UUID userdefinedresourceid = getUUIDSafe(node, "userdefinedresourceid");

            ResourceType type = (ResourceType) getValue(node, "type", "org.nomanscode.visualstreamer.sdk.common.ResourceType");

            return new ProfileResourceEntry(id, userdefinedresourceid, name, description, type);
        }
        catch(Exception e) {
            return null;
        }
    }*/

    /*private CybertronList getCybertronList(JsonNode node) {
        try {
            if (node == null) {
                return null;
            }

            String selected = getTextSafe(node, "selected");
            return new CybertronList(selected);
        }
        catch(Exception e) {
            return null;
        }
    }*/

    /*private ProfileTemplate getProfileTemplate(JsonNode node) {
        try {
            if (node == null) {
                return null;
            }
            UUID templateId = getUUIDSafe(node, "templateid");
            return new ProfileTemplate(templateId);
        }
        catch(Exception e) {
            return null;
        }
    }*/

    /*private ProfileStorage getProfileStorage(JsonNode node) {
        try {
            if (node == null) {
                return null;
            }
            UUID storageId = getUUIDSafe(node, "storageid");
            return new ProfileStorage(storageId);
        }
        catch(Exception e) {
            return null;
        }
    }*/

    /*private ProfileWatchFolder getProfileWatchFolder(JsonNode node) {
        try {
            if (node == null) {
                return null;
            }
            UUID watchFolderId = getUUIDSafe(node, "watchfolderid");
            return new ProfileWatchFolder(watchFolderId);
        }
        catch(Exception e) {
            return null;
        }
    }*/

    /*private ProfileResource getProfileResource(JsonNode node) {

        try {
            if (node == null) {
                return null;
            }

            List<ProfileResourceEntry> entries = new ArrayList<ProfileResourceEntry>();
            ArrayNode obj = (ArrayNode) node.get("resources");
            for (JsonNode jsonNode : obj) {
                ProfileResourceEntry entry = getProfileEntryResource(jsonNode);
                if (entry != null) {
                    entries.add(entry);
                }
            }
            return new ProfileResource(entries);
        }
        catch(Exception e) {
            return null;
        }
    }*/

    private ComplexTypes getComplexType(JsonNode node) {
        try {
            if (node == null) {
                return null;
            }

            String html = node.get("html").asText();
            String classPath = node.get("classpath").asText();
            String script = node.get("script").asText();
            String data = node.get("data").asText();

            return new ComplexTypes( html, script, classPath, data);
        }
        catch(Exception e) {
            return null;
        }
    }

    private Profile getProfile(JsonNode node) throws IOException, JsonProcessingException {
        try {

            boolean b1 = node.isTextual();
            boolean b2 = node.isArray();
            boolean b3 = node.isBoolean();
            boolean b4 = node.isNumber();
            boolean b5 = node.isObject();
            boolean b6 = node.isContainerNode();

            String name = this.getTextSafe(node, "name");
            String ancestor = this.getTextSafe(node, "@ancestor");
            String cls = this.getTextSafe(node, "@class");

            boolean nullable = this.getBooleanSafe(node, "nullable");
            boolean removable = this.getBooleanSafe(node, "removable");

            //TODO: enum
            Object[] enums = null; //node.get("enum");

            Object value = getValue(node, "value" , cls);
            Object defaultValue = getValue(node, "defaultvalue", cls);;

            Object minValue = getValue(node, "minvalue", cls);
            Object maxValue = getValue(node, "maxvalue", cls);

            boolean folder = this.getBooleanSafe(node, "folder");
            boolean secure = this.getBooleanSafe(node, "secure");

            Object config = node.get("config");

            List<Profile> sub = new ArrayList<Profile>();
            ArrayNode obj = (ArrayNode) node.get("sub");
            for (JsonNode jsonNode : obj) {
                Profile profile = getProfile(jsonNode);
                if ( profile != null ) {
                    sub.add(profile);
                }
            }

            Profile newProfile = new Profile();

            newProfile.setName(name);
            newProfile.setValue(value);
            newProfile.setDefaultValue(defaultValue);
            newProfile.setAncestor(ancestor);
            newProfile.setObjectClass(cls);
            newProfile.setNullable(nullable);
            newProfile.setEnumOptions(enums); //Enum Options is not so importante while deserializing because this data comes from the plugin instead.
            newProfile.setMinValue(minValue);
            newProfile.setMaxValue(maxValue);
            newProfile.setConfig(config);
            newProfile.setRemovable(removable);
            newProfile.setFolder(folder);
            newProfile.setSecure(secure);
            newProfile.setSub(sub);

            return newProfile;
        }
        catch(Exception e) {
            return null;
        }
    }
}

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(value = {"empty"}) //ignoreUnknown = true)
/*@JsonSubTypes({
    @JsonSubTypes.Type(value = ProfileResource.class, name = "ProfileResource"),
    @JsonSubTypes.Type(value = ProfileResourceEntry.class, name = "ProfileResourceEntry")
})*/
@JsonDeserialize(using = Deserializer.class)
public class Profile extends ArrayList<Profile> implements Serializable, IProfile {

    @JsonIgnore
    private String name_ = "";
    @JsonIgnore
    private Object value_ = "";
    @JsonIgnore
    private Object defaultValue_;
    @JsonIgnore
    private String ancestor_ = "";
    @JsonIgnore
    private String class_ = "";
    @JsonIgnore
    private Boolean nullable_ = false;
    @JsonIgnore
    private Object[] enumOptions_ = null;
    @JsonIgnore
    private Object minValue_ = null;
    @JsonIgnore
    private Object maxValue_ = null;
    @JsonIgnore
    private boolean removable_ = true;
    @JsonIgnore
    private Object config_ = null;
    @JsonIgnore
    private boolean secure = false;
    @JsonIgnore
    private boolean folder_ = false;

    public Profile() {
    }

    /*@JsonCreator
    public Profile(@JsonProperty("name") String name,
                   @JsonProperty("value") Object value,
                   @JsonProperty("defaultvalue") Object defaultValue,
                   @JsonProperty("@ancestor") String ancestor,
                   @JsonProperty("@class") String cls,
                   @JsonProperty("nullable") Boolean nullable,
                   @JsonProperty("enum") Object[] enums,
                   @JsonProperty("minvalue") Object minValue,
                   @JsonProperty("maxvalue") Object maxValue,
                   @JsonProperty("removable") boolean removable,
                   @JsonProperty("folder") boolean folder) {
        this.class_ = cls;
        this.ancestor_ = ancestor;
        this.name_ = name;
        this.value_ = value; //this.convertValue(value);
        this.defaultValue_ = defaultValue; //this.convertValue(defaultValue);
        this.enumOptions_ = Profile.enumOptions(this.value_, this.defaultValue_);
        this.nullable_ = nullable;
        this.minValue_ = minValue;
        this.maxValue_ = maxValue;
        this.removable_ = removable;
        this.folder_ = folder;
    }*/

    /*public Profile(String name,
                   Object value,
                   Object defaultValue,
                   String ancestor,
                   String cls,
                   Boolean nullable,
                   Object[] enums,
                   Object minValue,
                   Object maxValue,
                   boolean removable,
                   boolean folder,
                   List<Profile> sub) {
        this.class_ = cls;
        this.ancestor_ = ancestor;
        this.name_ = name;
        this.value_ = this.createNewInstance(value);
        this.defaultValue_ = this.createNewInstance(defaultValue);
        this.enumOptions_ = Profile.enumOptions(this.value_, this.defaultValue_);
        this.nullable_ = nullable;
        this.minValue_ = minValue;
        this.maxValue_ = maxValue;
        this.removable_ = removable;
        this.folder_ = folder;
        if( sub != null ) {
            this.addAll(sub);
        }
    }*/

    //Creates a new instance of the current object specially if
    //the object is a known class.
    static private Object createNewInstance(Object object) {

        if ( object == null ) {
            return null;
        }

        return object;

    }

    @JsonProperty("name")
    public String getName() {
        return this.name_;
    }

    @JsonIgnore
    public void setName(String value) {
        this.name_ = value;
    }

    @JsonProperty("value")
    public Object getValue() {
        return this.value_;
    }

    @JsonIgnore
    public void setValue(Object value) {
        this.value_ = value;
    }

    @JsonProperty("defaultvalue")
    public Object getDefaultValue() {
        return this.defaultValue_;
    }

    @JsonIgnore
    public void setDefaultValue(Object value) {
        this.defaultValue_ = value;
    }

    @JsonProperty("minvalue")
    public Object getMinValue() {
        return this.minValue_;
    }

    @JsonIgnore
    public void setMinValue(Object value) {
        this.minValue_ = value;
    }

    @JsonProperty("maxvalue")
    public Object getMaxValue() {
        return this.maxValue_;
    }

    @JsonIgnore
    public void setMaxValue(Object value) {
        this.maxValue_ = value;
    }

    @JsonProperty("config")
    public Object getConfig() {
        return this.config_;
    }

    @JsonIgnore
    public void setConfig(Object value) {
        this.config_ = value;
    }

    @JsonProperty("secure")
    public boolean isSecure() {
        return this.secure;
    }

    @JsonProperty("secure")
    public void setSecure(boolean value) {
        this.secure = value;
    }

    @JsonProperty("@ancestor")
    public String getAncestor() {
        return this.ancestor_;
    }

    @JsonIgnore
    public void setAncestor(String value) {
        this.ancestor_ = value;
    }

    @JsonProperty("@class")
    public String getObjectClass() {
        return this.class_;
    }

    @JsonIgnore
    public void setObjectClass(String value) {
        this.class_ = value;
    }

    @JsonProperty("nullable")
    public Boolean isNullable() {
        return this.nullable_;
    }

    @JsonIgnore
    public void setNullable(Boolean value) {
        this.nullable_ = value;
    }

    @JsonProperty("sub")
    public List<Profile> getSub() {
        return this.subList(0, this.size());
    }

    @JsonProperty("sub")
    public void setSub(List<Profile> sub) {
        this.addAll(sub);
    }

    @JsonProperty("removable")
    public boolean isRemovable() {
        return this.removable_;
    }

    @JsonProperty("removable")
    public void setRemovable(boolean value) {
        this.removable_ = value;
    }

    @JsonProperty("enum")
    public Object[] getEnumOptions()
    {
        return this.enumOptions_;
    }

    @JsonProperty("folder")
    public boolean isFolder() {
        return this.folder_;
    }

    public void setFolder(boolean value) {
        this.folder_ = value;
    }

    @JsonIgnore
    public void setEnumOptions(Object[] value) {
        this.enumOptions_ = value;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "Profile={ name=" + name_ + ", value=" + value_ + ", children= " + this.getSub() + "}";
    }

    @JsonIgnore
    public String toJSONPretty() {

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch( Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public String toJSON() {

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        }
        catch( Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public static Profile fromJSON(String json) {

        if ( json == null || json.isEmpty() ) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Profile.class);
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
    }

    /*@JsonIgnore
    public boolean setProfile(Profile newProfile) {

        try {
            this.ancestor_ = newProfile.ancestor_;
            this.class_ = newProfile.class_;
            this.name_ = newProfile.name_;
            this.value_ = newProfile.value_;
            this.defaultValue_ = newProfile.defaultValue_;
            this.nullable_ = newProfile.nullable_;
            this.enumOptions_ = newProfile.enumOptions_;
            this.removable_ = newProfile.removable_;

            this.clear();

            for (Profile profile : newProfile) {
                this.add(Profile.copy(profile));
            }

            return true;

        } catch (Exception e) {

            return false;
        }
    }*/

    // --------------------------------------------------------------------
    @JsonIgnore
    public static Profile copy(Profile profile, boolean copyChild) {

        try {
            if (profile == null) {
                return null;
            }

            Profile newProfile = new Profile();
            newProfile.setName(profile.getName());
            newProfile.setValue(createNewInstance(profile.getValue()));
            newProfile.setDefaultValue(createNewInstance(profile.getDefaultValue()));
            newProfile.setAncestor(profile.getAncestor());
            newProfile.setObjectClass(profile.getObjectClass());
            newProfile.setNullable(profile.isNullable());
            newProfile.setEnumOptions(profile.getEnumOptions());
            newProfile.setMinValue(profile.getMinValue());
            newProfile.setMaxValue(profile.getMaxValue());
            newProfile.setRemovable(profile.isRemovable());
            newProfile.setSecure(profile.isSecure());
            newProfile.setConfig(profile.getConfig());
            newProfile.setFolder(profile.isFolder());

            if ( copyChild ) {
                for (Profile profile1 : profile.getSub()) {
                    Profile cp = Profile.copy(profile1);
                    newProfile.getSub().add(cp);
                }
            }

            return newProfile;
        }
        catch(Exception e) {
            return null;
        }
    }

    public static Profile copy(Profile profile)
    {

        return Profile.copy(profile, true);

    }

    @JsonIgnore
    public static Profile findCustom(Profile profile) {  return Profile.find(profile, "/configuration/custom"); }

    @JsonIgnore
    public static Profile getCustom(Profile profileRoot) {
        return find(profileRoot, "/configuration/custom");
    }

    @JsonIgnore
    protected static Profile getResource(Profile profileRoot) {
        return find(profileRoot, "/configuration/resource");
    }

    @JsonIgnore
    public IProfile find(String path) {
        return find(this, "/configuration/custom/" + path );
    }

    @JsonIgnore                        //profile root node
    //path I want to search and return: "/configuration/output/wrapper"
    public static Profile find(Profile profileRoot, String path)
    {
        return find("", profileRoot, path);
    }

    /*@JsonIgnore
    public static List<UUID> getResourceIds(Profile profileRoot)
    {
        try {
            Profile profile = find(profileRoot, "/configuration/resource/pool");
            if ( profile == null ) {
                return null;
            }

            List<IResource> resources = profile.getResourceValues();
            if ( resources == null || resources.size() == 0 ) {
                return null;
            }

            return resources.stream().map(IResource::getId).collect(Collectors.toList());
        }
        catch (Exception e) {
            return null;
        }
    }*/

    @JsonIgnore
    private static Profile find(String currentPath, Profile currentProfile, String path)
    {
        if (path == null || path.length() == 0 || currentProfile == null ) {
            return null; //Profile not found or insufficient parameters.
        }

        String profilePath = currentPath + "/" + currentProfile.getName();

        if ( profilePath.equalsIgnoreCase(path) ) {
            return currentProfile;
        }

        for (Profile subProfile : currentProfile.getSub()) {
            Profile retProfile = find( profilePath, subProfile, path );
            if ( retProfile != null ) {
                return retProfile; //returning sub profile found.
            }
        }

        return null; //No profile found.
    }

    @JsonIgnore
    public static Profile merge(final Profile currentProfile, final Profile profile)
    {
        return merge(currentProfile, profile, false);
    }

    @JsonIgnore
    public static Profile merge(final Profile currentProfile, final Profile profile, final boolean ignoreFieldIfNullEmpty)
    {

        if ( currentProfile == null ) {
            //if profile is null, we just copy the profile.
            return Profile.copy(profile);
        }

        Profile baseProfile = Profile.copy(currentProfile);
        if ( !merge("", baseProfile, profile, ignoreFieldIfNullEmpty) ) {
            //If we fail we don't change anything!
            //TODO: Check: Is this the best approach?
            //      Shouldn't we return null instead and let caller to deal with the error?
            return currentProfile;
        }

        return baseProfile;
    }

    @JsonIgnore
    private static boolean merge(final String path, Profile templateProfile, final Profile profile, final boolean ignoreNullEmpty)
    {
        //For each template profile item, try to locate its equivalent entry in task profile

        String currentPath = path + "/" + templateProfile.getName();
        Profile taskProfile = find(profile, currentPath);
        if ( taskProfile != null ) {
            //Updates

            if ( templateProfile.size() == 0 ) {
                if ( !templateProfile.getObjectClass().equalsIgnoreCase(taskProfile.getObjectClass())) {
                    //Failure, due different class types
                    return false;
                }

                /*if ( taskProfile.getValue() != null || !ignoreNullEmpty ) {
                    //Check if we are working with resources (resources are not simple objects)
                    //but a complex structure we need to handle in a different way.
                    if (templateProfile.getObjectClass().equalsIgnoreCase(ProfileResource.class.getTypeName())) {
                        if (!templateProfile.getResourceValue().merge(taskProfile.getResourceValue())) {
                            return false;
                        }

                    } else if (templateProfile.getObjectClass().equalsIgnoreCase(ProfileTemplate.class.getTypeName())) {
                        //We are working with template field. We need to update the Template Id field only and ignore others.
                        templateProfile.getTemplateValue().setTemplateId(taskProfile.getTemplateValue().getTemplateId());

                    } else if (templateProfile.getObjectClass().equalsIgnoreCase(ProfileStorage.class.getTypeName())) {
                        //We are working with Storage field. We need to update the Storage Id field only and ignore others.
                        templateProfile.getStorageValue().setStorageId(taskProfile.getStorageValue().getStorageId());

                    } else if (templateProfile.getObjectClass().equalsIgnoreCase(ProfileWatchFolder.class.getTypeName())) {
                        //We are working with template field. We need to update the Template Id field only and ignore others.
                        templateProfile.getWatchFolderValue().setWatchFolderId(taskProfile.getWatchFolderValue().getWatchFolderId());

                    } else {
                        templateProfile.setValue(taskProfile.getValue());
                    }
                }*/
            }
        }

        for (Profile subProfile : templateProfile.getSub()) {
            if ( !merge(currentPath, subProfile, profile, ignoreNullEmpty ) ) {
                return false;
            }
        }

        return true;
    }

    @JsonIgnore
    public static String toJSON(Profile profile)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(profile);
        }
        catch( Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    private static Profile getMissingFields(Profile profile) {

        String name = profile.getName();
        Object value = profile.getValue();
        String classPath = profile.getObjectClass();

        Profile newProfile = null;

        if ( profile.size() > 0 ) {

            //it is a directory
            for (Profile subProfile : profile.getSub()) {
                Profile newSub = getMissingFields(subProfile);

                if ( newSub != null ) {

                    if ( newProfile == null ) {
                        newProfile = Profile.copy(profile, false);
                    }

                    newProfile.add(newSub);
                }
            }

            if ( newProfile == null ) {
                //We do not want to include empty folders
                return null;
            }

            return newProfile;
        }

        if (profile.isFolder() || profile.isNullable() || profile.getValue() != null ) {

            if ( !profile.getObjectClass().equals("java.lang.String") ) {
                return null;
            }

            if ( !String.valueOf(profile.getValue()).isEmpty() ) {
                return null;
            }

        }

        return Profile.copy(profile, false);
    }

    //----------------- Conversions ---------------------

    @JsonIgnore
    public String getScriptValue() {
        try {
            return String.valueOf(getValue());
        }
        catch(Exception ex) {
            return null;
        }
    }

    /*@JsonIgnore
    public ProfileResource getResourceValue() {
        try {
            return (ProfileResource) getValue();
        }
        catch(Exception ex) {
            return null;
        }
    }*/

    /*@JsonIgnore
    public ProfileTemplate getTemplateValue() {
        try {
            return (ProfileTemplate) getValue();
        }
        catch(Exception ex) {
            return null;
        }
    }*/

    /*@JsonIgnore
    public ProfileStorage getStorageValue() {
        try {
            return (ProfileStorage) getValue();
        }
        catch(Exception ex) {
            return null;
        }
    }*/

    /*@JsonIgnore
    public ProfileWatchFolder getWatchFolderValue() {
        try {
            return (ProfileWatchFolder) getValue();
        }
        catch(Exception ex) {
            return null;
        }
    }*/

    @JsonIgnore
    public String getStringValue(String defaultValue)
    {
        try {
            try {
                return getValue().toString();
            } catch (Exception ex) {
            }
            return getDefaultValue().toString();
        }
        catch(Exception ex) {
        }
        return defaultValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getEnumValue(T defaultValue) {
        try {
            try {
                return (T) this.getValue();
            } catch (Exception ex) {

            }
            return defaultValue;
        }
        catch(Exception ex) {
        }
        return defaultValue;
    }

    @JsonIgnore
    public long getLongValue(long defaultValue)
    {
        try {
            try {
                return Long.parseLong(getValue().toString());
            } catch (Exception ex) {
            }
            return Long.parseLong(getDefaultValue().toString());
        }
        catch(Exception ex) {
        }
        return defaultValue;
    }

    @JsonIgnore
    public int getIntValue(int defaultValue)
    {
        try {
            try {
                return Integer.parseInt(getValue().toString());
            } catch (Exception ex) {
            }
            return Integer.parseInt(getDefaultValue().toString());
        }
        catch(Exception ex) {
        }
        return defaultValue;
    }

    @JsonIgnore
    public boolean getBoolValue(boolean defaultValue)
    {
        try {
            try {
                return Boolean.parseBoolean(getValue().toString());
            } catch (Exception ex) {
            }
            return Boolean.parseBoolean(getDefaultValue().toString());
        }
        catch(Exception ex) {
        }
        return defaultValue;
    }

    @JsonIgnore
    public float getFloatValue(float defaultValue)
    {
        try {
            try {
                return Float.parseFloat(getValue().toString());
            } catch (Exception ex) {
            }
            return Float.parseFloat(getDefaultValue().toString());
        }
        catch(Exception ex) {
        }
        return defaultValue;
    }

    @JsonIgnore
    public double getDoubleValue(double defaultValue)
    {
        try {
            try {
                return Double.parseDouble(getValue().toString());
            } catch (Exception ex) {
            }
            return Double.parseDouble(getDefaultValue().toString());
        }
        catch(Exception ex) {
        }
        return defaultValue;
    }

    @JsonIgnore
    public UUID getUUIDValue(UUID defaultValue)
    {
        try {
            try {
                return UUID.fromString(getValue().toString());
            } catch (Exception ex) {
            }
            return UUID.fromString(getDefaultValue().toString());
        }
        catch(Exception ex) {
        }
        return defaultValue;
    }

    /*@JsonIgnore
    public CybertronList getListValue() {
        try {
            return (CybertronList) getValue();
        }
        catch(Exception ex) {
            return null;
        }
    }*/

    // --------------
    public static Profile create(String name, String baseClass) {

        Profile newProfile = new Profile();
        newProfile.setName(name);
        newProfile.setAncestor(baseClass);
        newProfile.setFolder(true);

        return newProfile;
    }

    public static Profile addChild(IProfile node, String name) {

        if ( node == null ) {
            return null;
        }

        Profile newNode = new Profile();
        newNode.setName(name);
        newNode.setFolder(true);

        node.getSub().add(newNode);

        return newNode;
    }

    // ------------- String Group ----------------

    public static Profile addString(IProfile node, String name) {
        return addString(node, name, false);
    }

    public static Profile addString(IProfile node, String name, boolean required) {
        return addString(node, name, required, false);
    }

    public static Profile addString(IProfile node, String name, boolean required, boolean secure) {
        return addString(node, name, required, secure, null);
    }

    public static Profile addString(IProfile node, String name, boolean required, boolean secure, String value) {
        return addField(node, "java.lang.String", name, required, value, null, null, null, null, secure, false);
    }

    public static Profile addString(IProfile node, String name, boolean required, boolean secure, String value, String defaultValue) {
        return addField(node, "java.lang.String", name, required, value, defaultValue, null, null, null, secure, false);
    }

    // --------------- Text Area Input String ----------
    public static Profile addMultiline(IProfile node, String name, boolean required, boolean secure, int lines) {
        return addField(node, "java.lang.String", name, required, null, null, null, null, lines, secure, false );
    }
    public static Profile addMultiline(IProfile node, String name, boolean required, boolean secure, int lines, String value) {
        return addField(node, "java.lang.String", name, required, value, null, null, null, lines, secure, false);
    }

    public static Profile addMultiline(IProfile node, String name, boolean required, boolean secure, int lines, String value, String defaultValue) {
        return addField(node, "java.lang.String", name, required, value, defaultValue, null, null, lines, secure, false);
    }

    // ------------- Boolean Group ----------------
    public static Profile addBoolean(IProfile node, String name) {
        return addBoolean(node, name, false, false);
    }

    public static Profile addBoolean(IProfile node, String name, Boolean value) {
        return addBoolean(node, name, value, false);
    }

    public static Profile addBoolean(IProfile node, String name, Boolean value, Boolean defaultValue) {
        return addField(node, "java.lang.Boolean", name, true, value, defaultValue, null, null);
    }

    // ------------- Integer Group ----------------

    public static Profile addInteger(IProfile node, String name) {
        return addField(node, "java.lang.Integer", name, false, null, null, null, null);
    }

    public static Profile addInteger(IProfile node, String name, boolean required) {
        return addField(node, "java.lang.Integer", name, required, null, null, null, null);
    }

    public static Profile addInteger(IProfile node, String name, boolean required, Integer value) {
        return addField(node, "java.lang.Integer", name, required, value, null, null, null);
    }

    public static Profile addInteger(IProfile node, String name, boolean required, Integer value, Integer defaultValue) {
        return addField(node,"java.lang.Integer", name, required, value, defaultValue, null, null);
    }

    public static Profile addInteger(IProfile node, String name, boolean required, Integer value, Integer defaultValue, Integer minValue, Integer maxValue) {
        return addField(node, "java.lang.Integer", name, required, value, defaultValue, minValue, maxValue);
    }

    // ------------- Long Group ----------------
    public static Profile addLong(IProfile node, String name) {
        return addField(node, "java.lang.Long", name, false, null, null, null, null);
    }

    public static Profile addLong(IProfile node, String name, boolean required) {
        return addField(node, "java.lang.Long", name, required, null, null, null, null);
    }

    public static Profile addLong(IProfile node, String name, boolean required, Long value) {
        return addField(node, "java.lang.Long", name, required, value, null, null, null);
    }

    public static Profile addLong(IProfile node, String name, boolean required, Long value, Long defaultValue) {
        return addField(node,"java.lang.Long", name, required, value, defaultValue, null, null);
    }

    public static Profile addLong(IProfile node, String name, boolean required, Long value, Long defaultValue, Long minValue, Long maxValue) {
        return addField(node, "java.lang.Long", name, required, value, defaultValue, minValue, maxValue);
    }

    // ------------- Float Group ----------------
    public static Profile addFloat(IProfile node, String name) {
        return addField(node, "java.lang.Float", name, false, null, null, null, null);
    }

    public static Profile addFloat(IProfile node, String name, boolean required) {
        return addField(node, "java.lang.Float", name, required, null, null, null, null);
    }

    public static Profile addFloat(IProfile node, String name, boolean required, Float value) {
        return addField(node, "java.lang.Float", name, required, value, null, null, null);
    }

    public static Profile addFloat(IProfile node, String name, boolean required, Float value, Float defaultValue) {
        return addField(node,"java.lang.Float", name, required, value, defaultValue, null, null);
    }

    public static Profile addFloat(IProfile node, String name, boolean required, Float value, Float defaultValue, Float minValue, Float maxValue) {
        return addField(node, "java.lang.Float", name, required, value, defaultValue, minValue, maxValue);
    }

    // ------------- Double Group ----------------
    public static Profile addDouble(IProfile node, String name) {
        return addField(node, "java.lang.Double", name, false, null, null, null, null);
    }

    public static Profile addDouble(IProfile node, String name, boolean required) {
        return addField(node, "java.lang.Double", name, required, null, null, null, null);
    }

    public static Profile addDouble(IProfile node, String name, boolean required, Double value) {
        return addField(node, "java.lang.Double", name, required, value, null, null, null);
    }

    public static Profile addDouble(IProfile node, String name, boolean required, Double value, Double defaultValue) {
        return addField(node,"java.lang.Double", name, required, value, defaultValue, null, null);
    }

    public static Profile addDouble(IProfile node, String name, boolean required, Double value, Double defaultValue, Double minValue, Double maxValue) {
        return addField(node, "java.lang.Double", name, required, value, defaultValue, minValue, maxValue);
    }

    // ------------- Enum ----------------
    public static Profile addEnum(IProfile node, String name, boolean required, Object value) {
        return addEnum(node, name, required, value, null);
    }

    public static Profile addEnum(IProfile node, String name, boolean required, Object value, Object defaultValue) {

        if ( !value.getClass().isEnum() ) {
            return null;
        }

        Object[] enums = null;

        if ( value != null && value.getClass().isEnum() ) {
            enums = enumToEnumSet(value.getClass()).toArray();
        }
        else if ( enums == null && defaultValue != null && defaultValue.getClass().isEnum() ) {
            enums = enumToEnumSet(defaultValue.getClass()).toArray();
        }
        else {
            //Nothing here is an enum
            return null;
        }

        if ( enums == null ) {
            return null;
        }

        Profile newNode = new Profile();
        newNode.setName(name);
        newNode.setObjectClass(value.getClass().getTypeName());
        newNode.setValue(value);
        newNode.setDefaultValue(defaultValue);
        newNode.setEnumOptions(enums);

        //Adds the new node into parent node.
        node.getSub().add(newNode);

        return newNode;
    }

    // --------------- Script ------------------
    public static Profile addScript(IProfile node, String name, String value ) {
        return addField(node, "cybertron.lang.Script", name, true, value, null, null, null, 10, false, true );
    }

    // ------------- Resources -------------------
    /*public static Profile addResources(IProfile node, String name, ProfileResource value) {
        return addField(node, ProfileResource.class.getName(), name, true, value, null, null, null); //Always required
    }*/

    // ------------- Templates ----------------
    /*public static Profile addTemplate(IProfile node, String name) {
        return addField(node, ProfileTemplate.class.getName(), name, true, new ProfileTemplate(), null, null, null);
    }*/

    // ------------- Storage ----------------
    /*public static Profile addStorage(IProfile node, String name) {
        ProfileStorage storage = new ProfileStorage();
        return addField(node, ProfileStorage.class.getName(), name, true, storage, null, null, null);
    }*/

    // ------------- Watch folder ----------------
    /*public static Profile addWatchFolder(IProfile node, String name) {
        ProfileWatchFolder watchFolders = new ProfileWatchFolder();
        return addField(node, ProfileWatchFolder.class.getName(), name, true,  watchFolders, null, null, null);
    }*/

    // ------------- Complex Type (HTML auto generate injections) ----------
    public static Profile addComplexType(IProfile node, String name, String className, boolean required, Object value, String html) {
        return addComplexType(node, name, className, required, value, html, null);
    }

    public static Profile addComplexType(IProfile node, String name, String className, boolean required, Object value, String html, String script) {

        ComplexTypes c = ComplexTypes.create(html, script, className, value);

        return addField(node, c.getClass().getName(), name, required, c, null, null, null, null);
    }

    // -------------- Cybertron List ----------------
    /*public static Profile addList(IProfile node, String name, CybertronList list) {
        return addField(node, CybertronList.class.getName(), name, true, CybertronList.copy(list), null, null, null);
    }*/

    /*public static Profile addList(IProfile node, String name, List<CybertronListItem> items) {
        return addField(node, CybertronList.class.getName(), name, true, CybertronList.create(items), null, null, null);
    }*/

    // ------------- Unknown Objects --------------

    public static Profile addField(IProfile node, String className, String name, boolean required, Object value, Object defaultValue, Object minValue, Object maxValue) {
        return addField(node, className, name, required, value, defaultValue, minValue, maxValue, null);
    }

    public static Profile addField(IProfile node, String className, String name, boolean required, Object value, Object defaultValue, Object minValue, Object maxValue, Object configData) {
        return addField(node, className, name, required, value, defaultValue, minValue, maxValue, null, false, false);
    }

    public static Profile addField(IProfile node, String className, String name, boolean required, Object value, Object defaultValue, Object minValue, Object maxValue, Object configData, boolean secure, boolean ignoreCompatibilityChecking) {

        if ( node == null || (!ignoreCompatibilityChecking && !checkCompatibility(className, value, defaultValue)) || !node.getObjectClass().isEmpty()) {
            return null; //They don't match each other or missing node or adding a field into another node field (node fields needs node of type folder).
        }

        Profile newNode = new Profile();
        newNode.setName(name);
        newNode.setObjectClass(className);
        newNode.setNullable(!required);
        newNode.setValue(createNewInstance(value));
        newNode.setDefaultValue(createNewInstance(defaultValue));
        newNode.setMinValue(minValue);
        newNode.setMaxValue(maxValue);
        newNode.setConfig(configData);
        newNode.setSecure(secure);

        //Adds the new node into parent node.
        node.getSub().add(newNode);

        //Returns the node created.
        return newNode;
    }

    private static Object[] enumOptions(Object value)
    {
        if ( value != null && value.getClass().isEnum() ) {
            return enumToEnumSet(value.getClass()).toArray();
        }

        return null;
    }

    private static Object[] enumOptions(Object value1, Object value2)
    {
        if ( value1 != null && value1.getClass().isEnum() ) {
            return enumToEnumSet(value1.getClass()).toArray();
        }
        else if ( value2 != null && value2.getClass().isEnum() ) {
            return enumToEnumSet(value2.getClass()).toArray();
        }

        return null;

    }

    @SuppressWarnings("unchecked")
    private static EnumSet enumToEnumSet(Class<?> c)
    {
        try {

            return EnumSet.allOf((Class<? extends Enum>) c);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    //Checks if class name and valor or default value match each other
    private static boolean checkCompatibility(String className, Object value, Object defaultValue) {

        if ( value == null && defaultValue == null ) {
            return true; //We don't have ways to check the class name is real or not. Need to rely on it.
        }

        if ( value != null && !value.getClass().getName().equals(className)) {
            return false;
        }

        if ( defaultValue != null && !defaultValue.getClass().getName().equals(className)) {
            return false;
        }

        //Check each other
        if ( value != null && defaultValue != null && !value.getClass().getName().equals(defaultValue.getClass().getName()) ) {
            return false; //They are not the same thing
        }

        return true; //it is compatible
    }

    // Safe gets

    /*public static ProfileResource getResourceValueSafe(Profile profile) {
        IProfile result = profile.find("/configuration/resources/pool");
        if ( result == null ) {
            return null;
        }

        return result.getResourceValue();
    }*/

    public static <T> T getEnumValueSafe (IProfile profile, String field, T defaultValue) {
        IProfile result = profile.find(field);
        if ( result == null ) {
            return defaultValue;
        }
        return result.getEnumValue(defaultValue);
    }

    public static String getStringValueSafe(IProfile profile, String field, String defaultValue) {
        IProfile result = profile.find(field);
        if ( result == null ) {
            return defaultValue;
        }
        return result.getStringValue(defaultValue);
    }

    public static long getLongValueSafe(IProfile profile, String field, long defaultValue) {
        IProfile result = profile.find(field);
        if ( result == null ) {
            return defaultValue;
        }
        return result.getLongValue(defaultValue);
    }

    public static int getIntValueSafe(IProfile profile, String field, int defaultValue) {
        IProfile result = profile.find(field);
        if ( result == null ) {
            return defaultValue;
        }
        return result.getIntValue(defaultValue);
    }

    public static boolean getBoolValueSafe(IProfile profile, String field, boolean defaultValue) {
        IProfile result = profile.find(field);
        if ( result == null ) {
            return defaultValue;
        }
        return result.getBoolValue(defaultValue);
    }

    public static float getFloatValueSafe(IProfile profile, String field, float defaultValue) {
        IProfile result = profile.find(field);
        if ( result == null ) {
            return defaultValue;
        }
        return result.getFloatValue(defaultValue);
    }

    public static double getDoubleValueSafe(IProfile profile, String field, double defaultValue) {
        IProfile result = profile.find(field);
        if ( result == null ) {
            return defaultValue;
        }
        return result.getDoubleValue(defaultValue);
    }

    public static UUID getUUIDValueSafe(IProfile profile, String field, UUID defaultValue) {
        IProfile result = profile.find(field);
        if ( result == null ) {
            return defaultValue;
        }
        return result.getUUIDValue(defaultValue);
    }

    public static String getScriptValueSafe(IProfile profile, String field) {
        IProfile result = profile.find(field);
        if ( result == null ) {
            return "";
        }
        return result.getStringValue("");
    }

    public static String getObjectClassSafe(IProfile profile, String field) {
        IProfile result = profile.find(field);
        if ( result == null ) {
            return null;
        }
        return result.getScriptValue();
    }

}
