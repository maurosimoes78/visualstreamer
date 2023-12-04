package org.nomanscode.visualstreamer.common;

import lombok.Data;

@Data
public class MyHolder<T> {
    public T value;

    public MyHolder () {

    }
    public MyHolder (T value) {
        this.value = value;
    }
}
