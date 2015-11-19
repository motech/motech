package org.motechproject.mds.service;

import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * Contains utility methods for dealing with history and trash clasess
 */
public final class HistoryTrashClassHelper {

    public static Class<?> getClass(Object src, EntityType type, BundleContext bundleContext) {
        return getClass(getInstanceClassName(src), type, bundleContext);
    }

    public static Class<?> getClass(String srcClassName, EntityType type, BundleContext bundleContext) {
        String className;

        switch (type) {
            case HISTORY:
                className = ClassName.getHistoryClassName(srcClassName);
                break;
            case TRASH:
                className = ClassName.getTrashClassName(srcClassName);
                break;
            default:
                className = null;
        }

        try {
            ClassLoader entitiesClassLoader = bundleContext.getBundle().adapt(BundleWiring.class).getClassLoader();
            return null == className ? null : entitiesClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getInstanceClassName(Object instance) {
        return null == instance ? "" : instance.getClass().getName();
    }

    public static String currentVersion(Class<?> historyClass) {
        return uncapitalize(historyClass.getSimpleName() + Constants.HistoryTrash.CURRENT_VERSION);
    }

    public static String schemaVersion(Class<?> historyClass) {
        return uncapitalize(historyClass.getSimpleName() + Constants.HistoryTrash.SCHEMA_VERSION);
    }

    private HistoryTrashClassHelper() {
    }
}
