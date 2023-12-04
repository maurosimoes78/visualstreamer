package org.nomanscode.visualstreamer.common;

import org.nomanscode.visualstreamer.common.interfaces.IEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PluginGroupType implements IEnum {

    PLUGIN_GROUP_TYPE_GENERAL("General","All purpose modules"),
    PLUGIN_GROUP_TYPE_ENTRY_POINT("Entry Components","Used for starting workflows"),
    PLUGIN_GROUP_TYPE_FILE_MANIPULATION("File Manipulation Components","Used for manipulating files"),
    PLUGIN_GROUP_TYPE_AUDIO_VIDEO_PROCESSING("Audio and Video Components","Used for manipulating audio/video"),
    PLUGIN_GROUP_TYPE_LOGIC("Logic Components","Used to control workflow logic"),
    PLUGIN_GROUP_TYPE_GLOOKAST("Glookast Components","Comprises Glookast Modules"),
    PLUGIN_GROUP_TYPE_CLOUD("Cloud Cloud Components","Cloud related Modules"),
    PLUGIN_GROUP_TYPE_STORAGE("Storage Components","Storage related Modules");

    public String description = "";
    public String friendlyName = "";


    PluginGroupType()
    {

    }

    PluginGroupType(final String friendlyName, final String description)
    {
        this.friendlyName = friendlyName;
        this.description = description;
    }

    public String value()
    {
        return name();
    }

    public static PluginGroupType fromValue(String v)
    {
        return valueOf(v);
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getFriendlyName() {
        return this.friendlyName;
    }

    @Override
    public PluginGroupType getValue() {
        return this;
    }

    public static List<PluginGroupType> add(PluginGroupType... types) {
        return Arrays.asList(types);
    }

    public static List<PluginGroupType> add(List<PluginGroupType> list, PluginGroupType... types) {

        List<PluginGroupType> newlist = new ArrayList<>();

        if ( list != null ) {
            newlist.addAll(list);
        }

        if ( types != null ) {
            newlist.addAll(Arrays.asList(types));
        }

        return newlist;
    }
}
