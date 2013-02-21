package org.motechproject.event.osgi;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;

import static java.util.Arrays.asList;
import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeSymbolicName;

public class ApplicationContextServiceReference {


    public static final String SERVICE_NAME = "org.springframework.context.service.name";
    private ServiceReference serviceReference;

    public ApplicationContextServiceReference(ServiceReference serviceReference) {
        this.serviceReference = serviceReference;
    }

    public Boolean isValid() {
        String[] objectClasses = (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);

        if (objectClasses.length == 0) {
            return false;
        }

        if (!asList(objectClasses).contains(ApplicationContext.class.getName())) {
            return false;
        }

        String serviceName = (String) serviceReference.getProperty(SERVICE_NAME);


        if (!nullSafeSymbolicName(serviceReference.getBundle()).equals(serviceName)) {
            return false;
        }
        return true;
    }
}
