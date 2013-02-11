package org.motechproject.commons.api;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class CastUtils {

    private CastUtils() {
    }

    public static <T> List<T> cast(Class<T> clazz, Enumeration enumeration) {
        List<T> list = new ArrayList<>();

        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Object obj = enumeration.nextElement();

                if (clazz.isInstance(obj)) {
                    list.add(clazz.cast(obj));
                }
            }
        }

        return list;
    }
}
