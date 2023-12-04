package org.nomanscode.visualstreamer.common;

public class  Info<K, V>
{
    public final K key;
    public final V value;

    public Info(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    public static <K,V> Info<K,V> create (K key, V value) {
        return new Info<K,V>(key, value);
    }
}
