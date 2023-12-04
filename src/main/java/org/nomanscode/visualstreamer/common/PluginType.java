package org.nomanscode.visualstreamer.common;

import org.nomanscode.visualstreamer.common.interfaces.IEnum;

public enum PluginType implements IEnum {
    PLUGIN_TYPE_UNKNOWN("Unknown Task",""),
    PLUGIN_TYPE_ENTRY("Entry Task","Used as an enry point"),
    PLUGIN_TYPE_END("End Task","Last task performed by a workflow in case of success"),
    PLUGIN_TYPE_ERROR("Error Task","Last task performed by a workflow in case of error"),
    PLUGIN_TYPE_ABORT("Abort Task","Last task performed by a workflow in case of user abortion"),
    PLUGIN_TYPE_TASK("Generic Task","Generic use customizable plugin"),
    PLUGIN_TYPE_WORKFLOW_TASK("Sub Process","Used for starting a sub-process"),
    PLUGIN_TYPE_CONDITIONAL_BEGIN("Decision Start",""),
    PLUGIN_TYPE_CONDITIONAL_END("Decision End",""),
    PLUGIN_TYPE_CONDITIONAL_CASE("Decision Case",""),
    PLUGIN_TYPE_SPLIT("Split Connector",""),
    PLUGIN_TYPE_JOIN_ALL("Join All Connector",""),
    PLUGIN_TYPE_FIRST_IN("First-In Connector",""),
    PLUGIN_TYPE_ITERATION_WHILE("Iteration (While)",""),
    PLUGIN_TYPE_ITERATION_FOR("Iteration (For)",""),
    PLUGIN_TYPE_LOGIC_AND("And","Logic AND"),
    PLUGIN_TYPE_LOGIC_OR("And","Logic OR"),
    PLUGIN_TYPE_WATCH_FOLDER("Watch Folder", "Detects file changes within a folder triggering output events"),
    PLUGIN_TYPE_ITERATION_END_WHILE("Iteration (End While)",""),
    PLUGIN_TYPE_ITERATION_END_FOR("Iteration (End For)","");

    public String description = "";
    public String friendlyName = "";


    PluginType()
    {

    }

    PluginType(final String friendlyName, final String description)
    {
        this.friendlyName = friendlyName;
        this.description = description;
    }

    public String value()
    {
        return name();
    }

    public static PluginType fromValue(String v)
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
    public PluginType getValue() {
        return this;
    }
}
