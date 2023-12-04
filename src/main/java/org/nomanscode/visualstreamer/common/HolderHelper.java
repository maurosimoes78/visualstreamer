package org.nomanscode.visualstreamer.common;

public class HolderHelper {

    public static MyHolder<String> setSystemError(MyHolder<String> hld, String methodName, Exception e) {
        return HolderHelper.setHolderValue(hld, "SYSTEM ERROR: " + e.getMessage() + " Method: " + methodName);
    }

    public static <T> MyHolder<T> setHolderValue(MyHolder<T> hld, T value)
    {
        try {
            if (hld == null) {
                return null;
            }

            hld.value = value;

            return hld;
        }
        catch(Exception e)
        {
            e.getStackTrace();
            return null;
        }
    }

    public static <T> MyHolder<T> create() {
        return new MyHolder<T>();
    }

    public static <T> MyHolder<T> create(T value) {
        MyHolder<T> hldr = create();
        return HolderHelper.setHolderValue(hldr, value);
    }
}
