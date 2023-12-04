package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class MinimalInfo {

    @JsonIgnore
    protected String name = "";

    @JsonIgnore
    protected String description = "";

    protected MinimalInfo() {

    }

    @JsonCreator
    public MinimalInfo(@JsonProperty("name") final String name,
                       @JsonProperty("description") final String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     * {@link String }
     */
    @JsonProperty("name")
    public String getName()
    {
        if( this.name == null ) {
            this.name = "";
        }
        return this.name;
    }


    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {

        if (value == null) {
            this.name = "";
            return;
        }
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link String }
     */
    @JsonProperty("description")
    public String getDescription()
    {
        if( this.description == null ) {
            this.description = "";
        }
        return this.description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value)
    {
        if ( value == null ) {
            this.description = "";
            return;
        }
        this.description = value;
    }
}
