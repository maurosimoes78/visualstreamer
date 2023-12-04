package org.nomanscode.visualstreamer.common;

//import javax.xml.bind.annotation.XmlEnum;
//import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for PinDirection.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PinDirection"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PINDIR_UNKNOWN"/&gt;
 *     &lt;enumeration value="PINDIR_INPUT"/&gt;
 *     &lt;enumeration value="PINDIR_OUTPUT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
//@XmlType(name = "PinDirection", namespace = "http://commons.cybertron.glookast.com")
//@XmlEnum
public enum PinDirection
{

    PINDIR_UNKNOWN,
    PINDIR_INPUT,
    PINDIR_OUTPUT;

    public String value()
    {
        return name();
    }

    public static PinDirection fromValue(String v)
    {
        return valueOf(v);
    }

}
