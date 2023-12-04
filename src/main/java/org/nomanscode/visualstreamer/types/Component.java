package org.nomanscode.visualstreamer.types;

import com.fasterxml.jackson.annotation.*;
import org.nomanscode.visualstreamer.common.ComponentRequest;
import org.nomanscode.visualstreamer.common.ComponentInfo;
import org.nomanscode.visualstreamer.common.CybertronRGBColor;
import org.nomanscode.visualstreamer.common.Profile;

import java.awt.*;
import java.util.Set;
import java.util.UUID;

public class Component extends ComponentInfo {

    @JsonIgnore
    public Component()
    {
        super();
    }

    @JsonIgnore
    public Component(ComponentInfo component)
    {
        super(component);
    }

    @JsonIgnore
    public Component(UUID newComponentId, ComponentInfo component)
    {
        super(newComponentId, component);
    }

    @JsonIgnore
    public Component(UUID newComponentId, ComponentInfo component, long newXCoord, long newYCoord)
    {
        super(newComponentId, component, newXCoord, newYCoord);
    }

    @JsonIgnore
    public Component(   final UUID id,
                        final String name,
                        final String description,
                        final boolean isPublic,
                        final UUID pluginId,
                        final boolean enabled,
                        final boolean deletable,
                        final boolean builtIn,
                        final String icon,
                        final CybertronRGBColor color)
    {
        super( id, name, description, isPublic, pluginId, enabled, deletable, builtIn, icon, color );

    }

    @JsonIgnore
    public ComponentInfo getComponentInfo()
    {
        return this;
    }

    //-----------------------------------------------------------------

    @JsonIgnore
    public static Component create(ComponentRequest request)
    {
        try {

            return new Component(   UUID.randomUUID(),
                                    request.getName(),
                                    request.getDescription(),
                                    request.isPublic(),
                                    request.getPluginId(),
                                    request.getEnabled(),
                                    true,
                                    false,
                                    "fas fa-puzzle-piece",
                                    CybertronRGBColor.GRAY());
        }
        catch(Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public static Component create(ComponentRequest request, UUID id, boolean deletable, boolean builtIn, String icon, CybertronRGBColor color)
    {
        try {

            return new Component(id,
                                 request.getName(),
                                 request.getDescription(),
                                 request.isPublic(),
                                 request.getPluginId(),
                                 request.getEnabled(),
                                 deletable,
                                 builtIn,
                                 icon,
                                 color);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public static Component create(final String name, final String description, final boolean isPublic, final UUID pluginId, final boolean enabled, final boolean deletable, final boolean builtIn, final String icon, final CybertronRGBColor color)
    {
        try {

            return new Component(   UUID.randomUUID(),
                                    name,
                                    description,
                                    isPublic,
                                    pluginId,
                                    enabled,
                                    deletable,
                                    builtIn,
                                    icon,
                                    color);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public static Component create(ComponentInfo componentInfo)
    {
        try {

            return new Component( UUID.randomUUID(), componentInfo );
        }
        catch(Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public static Component create(ComponentInfo componentInfo, long newXCoord, long newYCoord)
    {
        try {

            return new Component( UUID.randomUUID(), componentInfo, newXCoord, newYCoord );
        }
        catch(Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public static Component fromInfo(ComponentInfo componentInfo)
    {
        try {

            return new Component(componentInfo);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    @JsonIgnore
    public static Component copyComponent(Component component)
    {
        if ( component == null ) {
            return null;
        }

        return new Component(component);
    }

    @JsonIgnore
    public static Component mergeProfile(final Component component)
    {
        //Merges component profile along side template profile in the way that component profile settings
        //prevails over template profile configuration.
        //to do so, gets all the template profiles that don't occurs in component profile and inserts them.

        if ( component == null ) {
            return null; //Component is null, so returns null.
        }

        if ( component.getPluginInfo() == null ) {
            return component; //does not do anything.
        }

        Profile currentProfile = Profile.copy(component.getPluginInfo().getProfile());

        component.setProfile(Profile.merge( currentProfile, component.getProfile() ));

        return Component.copyComponent(component);
    }

    //Does profile checking and returns a copy of Profile class
    @JsonIgnore
    public Profile getProfile()
    {
        //Gets the profile entries...
        return super.getProfile();
    }
}
