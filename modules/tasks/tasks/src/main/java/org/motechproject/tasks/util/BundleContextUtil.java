package org.motechproject.tasks.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.List;

public final class BundleContextUtil {

    private BundleContextUtil() {
    }

    public static List<String> getSymbolicNames(BundleContext context) {
        Bundle[] bundles = context.getBundles();
        List<String> list = new ArrayList<>();

        if (ArrayUtils.isNotEmpty(bundles)) {
            for (Bundle bundle : bundles) {
                CollectionUtils.addIgnoreNull(list, bundle.getSymbolicName());
            }
        }

        return list;
    }
}
