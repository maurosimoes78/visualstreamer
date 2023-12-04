package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class Update<K, V>
{
    @JsonProperty("type")
    public final Type type;

    @JsonProperty("key")
    public final K key;

    @JsonProperty("value")
    public final V value;

    @JsonIgnore
    public Update(final Update<K,V> u)
    {
        this.key = u.key;
        this.type = u.type;
        this.value = u.value;
    }

    @JsonCreator
    public Update(@JsonProperty("type") Type type,
                  @JsonProperty("key") K key,
                  @JsonProperty("value") V value)
    {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public enum Type
    {
        Set,
        Delete
    }

    @JsonIgnore
    public static <K,V>Update set(K key, V value)
    {
        return new Update<K,V>(Type.Set, key, value);
    }

    @JsonIgnore
    public static <K,V>Update delete(K key)
    {
        return new Update<K,V>(Type.Delete, key, null);
    }

    @JsonIgnore
    public static <K,V>Update delete(K key, V value)
    {
        return new Update<K,V>(Type.Delete, key, value);
    }
}
