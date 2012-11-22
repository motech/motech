package org.motechproject.testing.utils.faketime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public abstract class JvmFakeTime {

    private static final Logger LOG = LoggerFactory.getLogger(JvmFakeTime.class);

    public static void load() {
        try {
            Method m = ClassLoader.class.getDeclaredMethod("loadLibrary", Class.class, String.class, Boolean.TYPE);
            m.setAccessible(true);
            m.invoke(null, System.class, "jvmfaketime", false);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
