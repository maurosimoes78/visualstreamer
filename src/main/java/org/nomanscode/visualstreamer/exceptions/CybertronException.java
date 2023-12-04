package org.nomanscode.visualstreamer.exceptions;

//import javax.xml.bind.annotation.*;
import org.nomanscode.visualstreamer.common.ErrorCode;

import java.io.Serializable;


/**
 * <p>Java class for KoderExceptionInfo complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CybertronException">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="errorCode" type="{http://api.cybertron.glookast.com}ErrorCodeType"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="detail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
/*@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CybertronException", propOrder = {
    "errorCode",
    "description",
    "detail"
})*/
public class CybertronException extends RuntimeException implements Serializable
{

    //@XmlElement(required = true)
    //@XmlSchemaType(name = "string")
    protected ErrorCode errorCode;
    protected String description;
    protected String detail;

    /**
     * Default no-arg constructor
     */
    public CybertronException()
    {
        super();
    }

    public CybertronException(Throwable t)
    {
        super(t);
    }

    public CybertronException(final ErrorCode errorCode, final String description)
    {
        super(description);
        this.errorCode = errorCode;
        this.description = description;
        this.detail = "";
    }

    public CybertronException(final ErrorCode errorCode, final String description, final String detail)
    {
        super(description);
        this.errorCode = errorCode;
        this.description = description;
        this.detail = detail;
    }

    /**
     * Fully-initialising value constructor
     */
    public CybertronException(final ErrorCode errorCode, final String description, final String detail, Throwable cause)
    {
        super(description, cause);
        this.errorCode = errorCode;
        this.description = description;
        this.detail = detail;
    }

    /**
     * Gets the value of the errorCode property.
     *
     * @return possible object is
     * {@link ErrorCode }
     */
    public ErrorCode getErrorCode()
    {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     *
     * @param value allowed object is
     *              {@link ErrorCode }
     */
    public void setErrorCode(ErrorCode value)
    {
        this.errorCode = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value)
    {
        this.description = value;
    }

    /**
     * Gets the value of the detail property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDetail()
    {
        return detail;
    }

    /**
     * Sets the value of the detail property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDetail(String value)
    {
        this.detail = value;
    }

}
