package org.motechproject.mobileforms.api.utils;

import java.util.List;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static <T> void addIfNotNull(List<T> list, T value) {
        if (value != null) {
            list.add(value);
        }
    }
}
