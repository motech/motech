package org.motechproject.mds.util;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;

/**
 * The <code>ClassName</code> util provides several methods which should help for example with
 * getting class name or package from string representation of class. There is also methods related
 * with creating names for repository, service interface and implementation of this service
 * interface.
 */
public final class ClassName {

    private ClassName() {
    }

    public static String getSimpleName(String className) {
        String str = defaultIfBlank(className, EMPTY);
        int idx = str.lastIndexOf('.');

        return idx < 0 ? str : str.substring(idx + 1);
    }

    public static String getPackage(String className) {
        String str = defaultIfBlank(className, EMPTY);
        int idx = str.lastIndexOf('.');

        return idx < 0 ? EMPTY : str.substring(0, idx);
    }

    public static String getEntityName(String className) {
        return String.format("%s.%s", Constants.PackagesGenerated.ENTITY, getSimpleName(className));
    }

    public static String getRepositoryName(String className) {
        return String.format("%s.All%ss", Constants.PackagesGenerated.REPOSITORY, getSimpleName(className));
    }

    public static String getInterfaceName(String className) {
        return String.format("%s.%sService", Constants.PackagesGenerated.SERVICE, getSimpleName(className));
    }

    public static String getServiceName(String className) {
        return String.format("%s.%sServiceImpl", Constants.PackagesGenerated.SERVICE_IMPL, getSimpleName(className));
    }

}
