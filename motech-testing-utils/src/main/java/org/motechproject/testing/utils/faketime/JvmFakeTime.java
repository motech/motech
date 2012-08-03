package org.motechproject.testing.utils.faketime;

import java.lang.reflect.Method;

public abstract class JvmFakeTime {

    public static void load() {
        try {
            Method m = ClassLoader.class.getDeclaredMethod("loadLibrary", Class.class, String.class, Boolean.TYPE);
            m.setAccessible(true);
            m.invoke(null, System.class, "jvmfaketime", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
