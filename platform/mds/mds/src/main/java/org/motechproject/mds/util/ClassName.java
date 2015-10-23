package org.motechproject.mds.util;

import org.apache.commons.lang.StringUtils;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.lowerCase;

/**
 * The <code>ClassName</code> util provides several methods which should help for example with
 * getting class name or package from string representation of class. There is also methods related
 * with creating names for repository, service interface and implementation of this service
 * interface.
 */
public final class ClassName {

    private static final String MOTECH_PREFIX = "motech";
    private static final String MOTECHPLATFORM_PREFIX = "motechplatform";

    private ClassName() {
    }

    /**
     * Returns simple name of the class (without the package prefix), from the given String.
     * If the name is already simple, it will return that name.
     *
     * @param className class name
     * @return simple class name
     */
    public static String getSimpleName(String className) {
        String str = defaultIfBlank(className, EMPTY);
        int idx = str.lastIndexOf('.');

        return idx < 0 ? str : str.substring(idx + 1);
    }

    /**
     * Returns package name from the fully qualified class name. If package cannot
     * be resolved, an empty String will be returned.
     *
     * @param className fully qualified class name
     * @return package name
     */
    public static String getPackage(String className) {
        String str = defaultIfBlank(className, EMPTY);
        int idx = str.lastIndexOf('.');

        return idx < 0 ? EMPTY : str.substring(0, idx);
    }

    /**
     * Retrieves fully qualified history class name, for the given class.
     *
     * @param className class name
     * @return fully qualified history class name
     */
    public static String getHistoryClassName(String className) {
        return String.format("%s.history.%s__History", getPackage(className), getSimpleName(className));
    }

    /**
     * Retrieves fully qualified trash class name, for the given class.
     *
     * @param className class name
     * @return fully qualified trash class name
     */
    public static String getTrashClassName(String className) {
        return String.format("%s.history.%s__Trash", getPackage(className), getSimpleName(className));
    }

    /**
     * Retrieves fully qualified entity class name, for the End User Defined Entity.
     *
     * @param className class name
     * @return fully qualified class name
     */
    public static String getEntityName(String className) {
        return String.format("%s.%s", Constants.PackagesGenerated.ENTITY, getSimpleName(className));
    }

    /**
     * Retrieves repository class name for the given entity class name.
     *
     * @param className entity class name
     * @return fully qualified repository class name
     */
    public static String getRepositoryName(String className) {
        String packageName = getPackage(className);
        if (StringUtils.isBlank(packageName) || Constants.PackagesGenerated.ENTITY.equals(packageName)) {
            packageName = Constants.PackagesGenerated.REPOSITORY;
        } else {
            packageName += ".mdsrepositoryimpl";
        }
        return String.format("%s.All%ss", packageName, getSimpleName(className));
    }

    /**
     * Retrieves interface name for the End User Defined Entity or for Developer Defined Entity
     * that do not define their own interface.
     *
     * @param className entity class name
     * @return fully qualified interface name
     */
    public static String getInterfaceName(String className) {
        String packageName = getPackage(className);
        if (StringUtils.isEmpty(packageName) || Constants.PackagesGenerated.ENTITY.equals(packageName)) {
            return String.format("%s.%sService", Constants.PackagesGenerated.SERVICE, getSimpleName(className));
        } else {
            return String.format("%s.mdsservice.%sService", packageName, getSimpleName(className));
        }

    }

    /**
     * Retrieves fully qualified class name of the
     * {@link org.motechproject.mds.service.MotechDataService} service implementation.
     *
     * @param className entity class name
     * @return fully qualified MDS service implementation name
     */
    public static String getServiceClassName(String className) {
        String packageName = getPackage(className);
        if (StringUtils.isBlank(packageName) || Constants.PackagesGenerated.ENTITY.equals(packageName)) {
            packageName = Constants.PackagesGenerated.SERVICE_IMPL;
        } else {
            packageName += ".mdsserviceimpl";
        }
        return String.format("%s.%sServiceImpl", packageName, getSimpleName(className));
    }

    /**
     * Returns the Spring bean name for the service class.
     * @param className the name of the service class
     * @return the bean name for the service
     */
    public static String getServiceName(String className) {
        return className + "DataService";
    }

    /**
     * Removes entity type suffix from the class name. If the name does not contain
     * entity type suffix, the passed name will be returned.
     *
     * @param name class name
     * @return class name, trimmed from type suffix
     */
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

    /**
     * Retrieves entity type suffix from the class name. If the class is neither
     * a history type class, nor a trash type class, it returns empty String.
     *
     * @param name class name
     * @return suffix of an entity type or empty String, if not applicable
     */
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

    /**
     * Verifies whether the given class name matches trash class naming pattern.
     *
     * @param className class name to verify
     * @return true, if given class matches trash class naming pattern; false otherwise
     */
    public static boolean isTrashClassName(String className) {
        return StringUtils.endsWith(className, "__Trash");
    }

    /**
     * Verifies whether the given class name matches history class naming pattern.
     *
     * @param className class name to verify
     * @return true, if given class matches history class naming pattern; false otherwise
     */
    public static boolean isHistoryClassName(String className) {
        return StringUtils.endsWith(className, "__History");
    }

    /**
     * Builds REST id, based on the entity name, module name and namespace.
     *
     * @param entityName name of the entity
     * @param module name of the module
     * @param namespace namespace
     * @return REST id
     */
    public static String restId(String entityName, String module, String namespace) {
        if (StringUtils.isBlank(module)) {
            return String.format("rest-%s", StringUtils.lowerCase(entityName));
        } else if (StringUtils.isBlank(namespace)) {
            return String.format("rest-%s-%s", simplifiedModuleName(module), StringUtils.lowerCase(entityName));
        } else {
            return String.format("rest-%s-%s-%s", simplifiedModuleName(module), StringUtils.lowerCase(namespace),
                    StringUtils.lowerCase(entityName));
        }
    }

    /**
     * Builds URL endpoint to access REST operations, based on the entity name, module name and namespace.
     *
     * @param entityName name of the entity
     * @param entityModule name of the module
     * @param entityNamespace namespace
     * @return URL endpoint for REST
     */
    public static String restUrl(String entityName, String entityModule, String entityNamespace) {
        String module = simplifiedModuleName(entityModule);

        if (StringUtils.isNotBlank(entityNamespace)) {
            return String.format("/%s/%s/%s", module, lowerCase(entityNamespace),
                    lowerCase(entityName));
        } else if (StringUtils.isNotBlank(module)) {
            return String.format("/%s/%s", module, lowerCase(entityName));
        } else {
            return String.format("/%s", lowerCase(entityName));
        }
    }

    /**
     * Builds URL endpoint, to access lookups via REST, based on the entity name, module, namespace
     * and lookup method name.
     *
     * @param entityName name of the entity
     * @param entityModule name of the module
     * @param entityNamespace namespace
     * @param lookupMethodName name of the lookup method
     * @return URL endpoint for REST lookup
     */
    public static String restLookupUrl(String entityName, String entityModule, String entityNamespace,
                                       String lookupMethodName) {
        String restUrl = restUrl(entityName, entityModule, entityNamespace);
        return String.format("/lookup%s/%s", restUrl, lookupMethodName);
    }

    /**
     * Returns simplified name of the module. It will drop the "motech" and "motechplatform" prefix from the
     * module names. Also any blank spaces will be removed.
     *
     * @param moduleName module name to simplify
     * @return simplified module name
     */
    public static String simplifiedModuleName(String moduleName) {
        if (StringUtils.isBlank(moduleName)) {
            return moduleName;
        }

        String parsedName = moduleName.toLowerCase();
        parsedName = parsedName.replace(" ", "");
        if (parsedName.startsWith(MOTECH_PREFIX)) {
            // drop Motech or Motech platform from the name
            int index = (parsedName.startsWith(MOTECHPLATFORM_PREFIX)) ? MOTECHPLATFORM_PREFIX.length() : MOTECH_PREFIX.length();
            parsedName = parsedName.substring(index);
        }
        return parsedName;
    }

    /**
     * Returns the package name that contain enum added as a field to a class with a given name.
     *
     * @param className name of a class that contain enum field
     * @return package name of the enum generated for the class
     */
    public static String getEnumPackage(String className) {
        return getPackage(className) + ".mdsenum";
    }
}
