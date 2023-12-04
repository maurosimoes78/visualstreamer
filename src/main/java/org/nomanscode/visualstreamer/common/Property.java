package org.nomanscode.visualstreamer.common;

//import com.glookast.commons.xml.XmlAdapterUUID;

/*import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;*/
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/*@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property", namespace = "http://commons.cybertron.glookast.com", propOrder = {
    "id",
    "value"
})*/
public class Property implements Serializable
{
    //@XmlElement(required = true)
    protected Id id;
    //@XmlElement(required = true)
    protected String value;

    public Property()
    {
        super();
    }

    public Property(Id id, String value)
    {
        this.id = id;
        this.value = value;
    }

    public Property(Id id, int value)
    {
        this(id, String.valueOf(value));
    }

    public Property(Id id, long value)
    {
        this(id, String.valueOf(value));
    }

    public Property(Id id, boolean value)
    {
        this(id, String.valueOf(value));
    }

    public Property(Id id, double value)
    {
        this(id, String.valueOf(value));
    }

    public Property(Id id, UUID value)
    {
        this(id, String.valueOf(value));
    }

    public Property(String key, UUID uuid, String value)
    {
        this(new Id(key, uuid), value);
    }

    public Property(String key, UUID uuid, int value)
    {
        this(key, uuid, String.valueOf(value));
    }

    public Property(String key, UUID uuid, long value)
    {
        this(key, uuid, String.valueOf(value));
    }

    public Property(String key, UUID uuid, boolean value)
    {
        this(key, uuid, String.valueOf(value));
    }

    public Property(String key, UUID uuid, double value)
    {
        this(key, uuid, String.valueOf(value));
    }

    public Property(String key, UUID uuid, UUID value)
    {
        this(key, uuid, String.valueOf(value));
    }

    public Property(String key, String value)
    {
        this(key, null, value);
    }

    public Property(String key, int value)
    {
        this(key, null, String.valueOf(value));
    }

    public Property(String key, long value)
    {
        this(key, null, String.valueOf(value));
    }

    public Property(String key, boolean value)
    {
        this(key, null, String.valueOf(value));
    }

    public Property(String key, double value)
    {
        this(key, null, String.valueOf(value));
    }

    public Property(String key, UUID value)
    {
        this(key, null, String.valueOf(value));
    }

    public Property(Property property)
    {
        this.id = new Id(property.id);
        this.value = property.value;
    }

    public Id getId()
    {
        return id;
    }

    public void setId(Id id)
    {
        this.id = id;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public int getIntValue()
    {
        try {
            return Integer.valueOf(value);
        } catch (Exception ex) {
        }
        return 0;
    }

    public void setIntValue(int value)
    {
        this.value = String.valueOf(value);
    }

    public long getLongValue()
    {
        try {
            return Long.valueOf(value);
        } catch (Exception ex) {
        }
        return 0;
    }

    public void setLongValue(long value)
    {
        this.value = String.valueOf(value);
    }

    public boolean getBoolValue()
    {
        try {
            return Boolean.valueOf(value);
        } catch (Exception ex) {
        }
        return false;
    }

    public void setBoolValue(boolean value)
    {
        this.value = String.valueOf(value);
    }

    public double getDoubleValue()
    {
        try {
            return Double.valueOf(value);
        } catch (Exception ex) {
        }
        return 0.0;
    }

    public void setDoubleValue(double value)
    {
        this.value = String.valueOf(value);
    }

    public UUID getUUIDValue()
    {
        try {
            return UUID.fromString(value);
        } catch (Exception ex) {
        }
        return null;
    }

    public void setUUIDValue(UUID value)
    {
        this.value = String.valueOf(value);
    }

    @Override
    public String toString()
    {
        return "Property{" + "id=" + id + ", value=" + value + '}';
    }

    /*@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "PropertyId", namespace = "http://commons.cybertron.glookast.com", propOrder = {
        "key",
        "uuid"
    })*/
    public static class Id implements Serializable
    {
        //@XmlElement(required = true)
        private String key;
        //@XmlElement(type = String.class)
        //@XmlJavaTypeAdapter(XmlAdapterUUID.class)
        private UUID uuid;

        public Id()
        {
        }

        public Id(Id id)
        {
            this.key = id.key;
            this.uuid = id.uuid;
        }

        public Id(String key, UUID uuid)
        {
            this.key = key;
            this.uuid = uuid;
        }

        public Id(String key)
        {
            this(key, null);
        }

        public String getKey()
        {
            return key;
        }

        public void setKey(String key)
        {
            this.key = key;
        }

        public UUID getUuid()
        {
            return uuid;
        }

        public void setUuid(UUID uuid)
        {
            this.uuid = uuid;
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 37 * hash + Objects.hashCode(this.key);
            hash = 37 * hash + Objects.hashCode(this.uuid);
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Id other = (Id) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            if (!Objects.equals(this.uuid, other.uuid)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString()
        {
            return this.key + ((uuid != null) ? "." + String.valueOf(uuid) : "");
        }
    }
}
