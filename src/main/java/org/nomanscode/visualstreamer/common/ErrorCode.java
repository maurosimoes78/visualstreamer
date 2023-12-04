
package org.nomanscode.visualstreamer.common;

//import javax.xml.bind.annotation.XmlEnum;
//import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ErrorCode.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ErrorCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="UNKNOWN"/&gt;
 *     &lt;enumeration value="DATABASE_ERROR"/&gt;
 *     &lt;enumeration value="ENGINE_ERROR"/&gt;
 *     &lt;enumeration value="PERMISSION_ERROR"/&gt;
 *     &lt;enumeration value="SCHEDULE_UNAVAILABLE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
//@XmlType(name = "ErrorCode", namespace = "http://commons.cybertron.glookast.com")
//@XmlEnum
public enum ErrorCode
{

    UNKNOWN,
    DATABASE_ERROR,
    ENGINE_ERROR,
    PERMISSION_ERROR,
    SCHEDULE_UNAVAILABLE,
    PLUGIN_ERROR,
    COMPONENT_ERROR,
    RESOURCE_ERROR;

    public String value()
    {
        return name();
    }

    public static ErrorCode fromValue(String v)
    {
        return valueOf(v);
    }

}
