package org.motechproject.mds.util;

import org.apache.commons.lang.StringUtils;

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

    public static String getHistoryClassName(String className) {
        return String.format("%s.history.%s__History", getPackage(className), getSimpleName(className));
    }

    public static String getTrashClassName(String className) {
        return String.format("%s.history.%s__Trash", getPackage(className), getSimpleName(className));
    }

    public static String getEntityName(String className) {
        return String.format("%s.%s", Constants.PackagesGenerated.ENTITY, getSimpleName(className));
    }

    public static String getRepositoryName(String className) {
        String packageName = getPackage(className);
        if (StringUtils.isBlank(packageName) || Constants.PackagesGenerated.ENTITY.equals(packageName)) {
            packageName = Constants.PackagesGenerated.REPOSITORY;
        } else {
            packageName += ".mdsrepositoryimpl";
        }
        return String.format("%s.All%ss", packageName, getSimpleName(className));
    }

    public static String getInterfaceName(String className) {
        return String.format("%s.%sService", Constants.PackagesGenerated.SERVICE, getSimpleName(className));
    }

    public static String getServiceName(String className) {
        String packageName = getPackage(className);
        if (StringUtils.isBlank(packageName) || Constants.PackagesGenerated.ENTITY.equals(packageName)) {
            packageName = Constants.PackagesGenerated.SERVICE_IMPL;
        } else {
            packageName += ".mdsserviceimpl";
        }
        return String.format("%s.%sServiceImpl", packageName, getSimpleName(className));
    }

    public static String trimTrashHistorySuffix(String name) {
        String trimmedName = name;
        if (StringUtils.isNotBlank(trimmedName)) {
            trimmedName = trimmedName.replaceAll("\\.history\\.(.+)__(History|Trash)$", ".$1");
            if (trimmedName.startsWith(".")) {
                trimmedName = trimmedName.substring(1);
            }
        }
        return trimmedName;
    }

    public static String getEntityTypeSuffix(String name) {
        String suffix = EMPTY;
        if (StringUtils.isNotBlank(name)) {
            if (name.matches("\\.history\\.(.+)__History$")) {
                suffix = "__History";
            } else if (name.matches("\\.history\\.(.+)__Trash$")) {
                suffix = "__Trash";
            }
        }
        return suffix;
    }

    public static boolean isTrashClassName(String className) {
        return StringUtils.endsWith(className, "__Trash");
    }

    public static boolean isHistoryClassName(String className) {
        return StringUtils.endsWith(className, "__History");
    }

    public static String restId(String entityName, String module, String namespace) {
        if (StringUtils.isBlank(module)) {
            return String.format("rest-%s", StringUtils.lowerCase(entityName));
        } else if (StringUtils.isBlank(namespace)) {
            return String.format("rest-%s-%s", moduleNameForRest(module), StringUtils.lowerCase(entityName));
        } else {
            return String.format("rest-%s-%s-%s", moduleNameForRest(module), StringUtils.lowerCase(namespace),
                    StringUtils.lowerCase(entityName));
        }
    }

    public static String moduleNameForRest(String moduleName) {
        if (StringUtils.isBlank(moduleName)) {
            return moduleName;
        }

        String parsedName = moduleName.toLowerCase();
        parsedName = parsedName.replace(" ", "");
        if (parsedName.startsWith("motech")) {
            // drop Motech or Motech platform from the name
            int index = (parsedName.startsWith("motechplatform")) ? 14 : 6;
            parsedName = parsedName.substring(index);
        }
        return parsedName;
    }
}
