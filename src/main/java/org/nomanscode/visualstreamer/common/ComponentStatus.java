package org.nomanscode.visualstreamer.common;

//import javax.xml.bind.annotation.XmlEnum;
//import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for ComponentStatus.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PinDirection"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="TASK_IDLE"/&gt;
 *     &lt;enumeration value="TASK_RUNNING"/&gt;
 *     &lt;enumeration value="TASK_COMPLETE"/&gt;
 *     &lt;enumeration value="TASK_ABORTING"/&gt;
 *     &lt;enumeration value="TASK_ABORTED"/&gt;
 *     &lt;enumeration value="TASK_FAILED"/&gt;
 *     &lt;enumeration value="TASK_PAUSED"/&gt;
 *     &lt;enumeration value="TASK_UNKNOW"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
//@XmlType(name = "ComponentStatus", namespace = "http://commons.cybertron.glookast.com")
//@XmlEnum
public enum ComponentStatus
{
    TASK_IDLE,
    TASK_RUNNING,
    TASK_COMPLETE,
    TASK_ABORTING,
    TASK_ABORTED,
    TASK_FAILED,
    TASK_PAUSED,
    TASK_UNKNOW;

    public String value()
    {
        return name();
    }

    public static ComponentStatus fromValue(String v)
    {
        return valueOf(v);
    }
}