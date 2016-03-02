package org.motechproject.mds.web.rest;

import org.motechproject.mds.exception.rest.RestNotSupportedException;
import org.motechproject.mds.rest.MdsRestFacade;
import org.motechproject.mds.util.ClassName;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Helper class for retrieving {@link org.motechproject.mds.rest.MdsRestFacade} objects
 * published as OSGi services. Used by the {@link org.motechproject.mds.web.rest.MdsRestController}.
 */
@Component
public class MdsRestFacadeRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsRestFacadeRetriever.class);

    @Autowired
    private BundleContext bundleContext;

    public MdsRestFacade getRestFacade(String entityName, String moduleName, String namespace) {
        String restId = ClassName.restId(entityName, moduleName, namespace);

        MdsRestFacade restFacade = null;
        try {
            String filter = String.format("(org.eclipse.gemini.blueprint.bean.name=%s)", restId);
            Collection<ServiceReference<MdsRestFacade>> refs = bundleContext.getServiceReferences(
                    MdsRestFacade.class, filter);

            if (refs != null && refs.size() > 1 && LOGGER.isWarnEnabled()) {
                LOGGER.warn("More then one Rest Facade matching for entityName={}, module={}, namespace={}. " +
                        "Using first one available.", entityName, moduleName, namespace);
            }

            if (refs != null && refs.size() > 0) {
                ServiceReference<MdsRestFacade> ref = refs.iterator().next();
                restFacade = bundleContext.getService(ref);
            }
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid Syntax for Rest Facade retrieval", e);
        }

        if (restFacade == null) {
            throw new RestNotSupportedException(entityName, moduleName, namespace);
        }

        return restFacade;
    }
}
