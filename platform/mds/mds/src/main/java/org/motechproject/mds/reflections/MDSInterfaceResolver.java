package org.motechproject.mds.reflections;

import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.MotechDataService;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * The <code>MDSInterfaceResolver</code> class is responsible for finding
 * MotechDataService interfaces and binding them with proper entities. This is
 * required to be able to determine class location during weaving.
 */
public final class MDSInterfaceResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MDSInterfaceResolver.class);

    private MDSInterfaceResolver() {

    }

    /**
     * This method scans the given bundle and registers all interfaces extending
     * {@link MotechDataService} interface in the {@link MotechClassPool}.
     *
     * @param bundle A bundle to scan in.
     */
    public static void processMDSInterfaces(Bundle bundle) {
        LOGGER.debug("Starting to look for MotechDataService implementations in bundle {}", bundle.getSymbolicName());
        List<Class<? extends MotechDataService>> allBundleInterfaces = ReflectionsUtil.getMdsInterfaces(bundle);

        for (Class clazz : allBundleInterfaces) {
            LOGGER.debug("Processing {} class", clazz.getName());
            Type[] interfaces = clazz.getGenericInterfaces();
            ParameterizedType parameterizedType = findMDSInterface(interfaces);

            LOGGER.info("Registering {} service in MotechClassPool", clazz.getName());
            MotechClassPool.registerServiceInterface(discoverEntityClassName(parameterizedType), clazz.getName());
        }
    }

    private static ParameterizedType findMDSInterface(Type[] types) {
        LOGGER.trace("Iterating through all subtypes");
        for (Type type : types) {
            LOGGER.trace("Verifying {} subtype", type.toString());
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType().equals(MotechDataService.class)) {
                    return parameterizedType;
                }
            }
        }

        return null;
    }

    private static String discoverEntityClassName(ParameterizedType parameterizedType) {
        String entityClass = ((Class) parameterizedType.getActualTypeArguments()[0]).getName();
        LOGGER.debug("Resolved the following entity class: {} for the service {}", entityClass, parameterizedType);

        return entityClass;
    }

}
