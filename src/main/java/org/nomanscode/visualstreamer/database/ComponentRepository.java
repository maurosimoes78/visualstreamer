package org.nomanscode.visualstreamer.database;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.type.TypeFactory;
import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.common.interfaces.*;

import lombok.extern.slf4j.Slf4j;
import org.nomanscode.visualstreamer.exceptions.CybertronException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

@Repository
@Slf4j
public class ComponentRepository extends CacheRepository<UUID, ComponentInfo>
{

    @Autowired
    PluginRepository  pluginRepository;

    @Autowired
    ComponentPropertyRepository componentPropertyRepository;

    @Override
    protected void loadCache(Connection conn, Map<UUID, ComponentInfo> cache) throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM components.component")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    UUID id = UUID.fromString(rs.getString("id"));
                    UUID pluginId = UUID.fromString(rs.getString("pluginId"));

                    PluginInfo pluginInfo = null;
                    try {
                        pluginInfo = pluginRepository.get(pluginId);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    long xCoord = 0;
                    long yCoord = 0;

                    if (pluginInfo != null) {

                        //Reads from property table
                        String jsonString;
                        try {
                            jsonString = componentPropertyRepository.getValue("data", id, "");
                            if( jsonString == null || jsonString.length() == 0 ) {
                                continue;
                            }
                        }
                        catch(Exception e) {
                            e.getStackTrace();
                            continue;
                        }

                        ComponentInfo component = ComponentInfo.fromJSON(jsonString);


                        /*Profile profile = null;
                        try {
                            String jsonString = componentPropertyRepository.getValue("profile", id, "");
                            profile = Profile.fromJSON(jsonString);

                            xCoord = componentPropertyRepository.getLongValue("xCoord", id, 0);
                            yCoord = componentPropertyRepository.getLongValue("yCoord", id, 0);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        if ( profile != null ) {
                            List<IParameter> params = getParameters(id);

                            ComponentInfo component = new ComponentInfo(id,
                                    rs.getString("name"),
                                    rs.getString("description"),
                                    pluginId,
                                    rs.getBoolean("enabled"),
                                    true,
                                    params,
                                    profile,
                                    "fas fa-puzzle-piece",
                                    pluginInfo.getColor());

                        }*/

                        cache.put(id, component);

                    }
                }
            }
        }
    }

    /*private List<IParameter> getParameters(UUID componentId)
    {
        String jsonString = "";

        try {

            jsonString = componentPropertyRepository.getValue("parameters", componentId, "");

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        if ( jsonString.isEmpty() ) {
            return null;
        }

        try {

            ObjectMapper mapper = new ObjectMapper();

            TypeFactory typeFactory = mapper.getTypeFactory();
            JavaType outer = typeFactory.constructParametricType( ArrayList.class, Parameter.class);
            List<IParameter>  params = mapper.readValue(jsonString, outer);

            return params;
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }*/

    /*private boolean setParameters(UUID componentId, List<ParameterX> params)
    {

        try {

            componentPropertyRepository.setValue("parameters", componentId, ParameterX.toJSON(params));

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

    }*/

    /*public boolean setProfile(UUID componentId, Profile profile, Holder<String> errorMessage)
    {
        try {
            String jsonString = Profile.toJSON(profile);

            componentPropertyRepository.setValue("profile", componentId, jsonString);
            return true;
        }
        catch(Exception e)
        {
            HolderHelper.setHolderValue(errorMessage, e.getMessage());
            return false;
        }
    }*/

    @Override
    protected ComponentInfo set(Connection conn, ComponentInfo t) throws SQLException, InterruptedException, JsonProcessingException {

        if (t.getId() == null) {
            throw new CybertronException(ErrorCode.COMPONENT_ERROR, "Invalid id!", "Component id must be set prior to recording the component into database");
        }

        String sql = "INSERT INTO components.component (id, name, description, pluginid, enabled) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = ?, description = ?, pluginid = ?, enabled = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, t.getId());
            stmt.setString(2, t.getName());
            stmt.setString(3, t.getDescription());
            stmt.setObject(4, t.getPluginId());
            stmt.setBoolean(5, t.getEnabled());
            stmt.setString(6, t.getName());
            stmt.setString(7, t.getDescription());
            stmt.setObject(8, t.getPluginId());
            stmt.setBoolean(9, t.getEnabled());
            stmt.executeUpdate();
        }

        return t;
    }

    @Override
    protected boolean del(Connection conn, UUID id) throws SQLException
    {
        boolean result;

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM components.component WHERE id = ?")) {
            stmt.setObject(1, id);
            result = (stmt.executeUpdate() > 0);
        }

        return result;
    }


    public boolean setComponent( ComponentInfo component, MyHolder<String> errorMessage) {

        try {

            this.set(component);

            this.componentPropertyRepository.setValue("data", component.getId(), ComponentInfo.copy(component).toJSON());


            /*if (!this.setProfile(component.getId(), component.getProfile(), errorMessage)) {
                return false;
            }*/

            return this.setCoordinates(component.getId(), component.getXCoord(), component.getYCoord(), errorMessage);

        }
        catch (InterruptedException e) {
            if (errorMessage != null) {
                HolderHelper.setHolderValue(errorMessage, e.getMessage());
            }
            return false;
        }
        catch(Exception e) {
            if (errorMessage != null) {
                HolderHelper.setHolderValue(errorMessage, e.getMessage());
            }
            return false;
        }
    }

    private boolean setCoordinates(UUID id, long xCoord, long yCoord, MyHolder<String> errorMessage) {
        try {

            componentPropertyRepository.setValue("xCoord", id, xCoord);
            componentPropertyRepository.setValue("yCoord", id, yCoord);

            return true;
        }
        catch(InterruptedException e) {
            if (errorMessage != null) {
                HolderHelper.setHolderValue(errorMessage, e.getMessage());
            }
            return false;
        }
        catch(Exception e) {
            if (errorMessage != null) {
                HolderHelper.setHolderValue(errorMessage, e.getMessage());
            }
            return false;
        }
    }
}
