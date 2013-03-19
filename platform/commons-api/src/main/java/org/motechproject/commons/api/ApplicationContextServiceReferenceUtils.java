package org.motechproject.commons.api;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;

import static java.util.Arrays.asList;
import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeSymbolicName;

public final class ApplicationContextServiceReferenceUtils {
    public static final String SERVICE_NAME = "org.springframework.context.service.name";

    private ApplicationContextServiceReferenceUtils() {
    }

    public static boolean isValid(ServiceReference serviceReference) {
        String[] objectClasses = (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);

        if (objectClasses.length == 0) {
            return false;
        }

        if (!asList(objectClasses).contains(ApplicationContext.class.getName())) {
            return false;
        }

        String serviceName = (String) serviceReference.getProperty(SERVICE_NAME);


        return nullSafeSymbolicName(serviceReference.getBundle()).equals(serviceName);
    }

    public static boolean isNotValid(ServiceReference serviceReference) {
        return !isValid(serviceReference);
    }
}
