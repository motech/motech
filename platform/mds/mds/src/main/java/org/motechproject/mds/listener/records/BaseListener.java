package org.motechproject.mds.listener.records;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.mds.helper.bundle.MdsBundleHelper.findMdsEntitiesBundle;
import static org.motechproject.mds.helper.bundle.MdsBundleHelper.isMdsEntitiesBundle;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;

/**
 * A base class for listeners in MDS. Since listeners get constructed by JDO,
 * we don't normally have access to the spring context of MDS entities. This class
 * retrieves this context by registering as a service listener that for the
 * {@link org.springframework.context.ApplicationContext}. After the context gets
 * retrieved it can be used for retrieving the service specified by the implementing listener.
 * If service retrieval is initiated before the context is available, a wait time of max 5 minutes
 * will begin.
 *
 * @param <T> the type of the service which is used by the implementing listener
 */
public abstract class BaseListener<T> implements ServiceListener {

    private static final String BUNDLE_SYMBOLIC_NAME_FILTER = "(Bundle-SymbolicName=%s)";

    private static final int CTX_WAIT_TIME_MS = 5 * 60 * 1000; // 5 min

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Object ctxWaitLock = new Object();
    private final BundleContext bundleContext;

    private ApplicationContext applicationContext;
    private T service;

    public BaseListener() {
        // Listeners get constructed by JDO. Because of this, we must obtain required references
        // by hand.
        bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
        try {
            bundleContext.addServiceListener(this, String.format("(%s=%s)", Constants.OBJECTCLASS,
                    ApplicationContext.class.getName()));
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException(
                    "Invalid syntax. Should not happen, can indicate framework version issues", e);
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        if (isMdsEntitiesBundle(event.getServiceReference().getBundle())) {
            if (event.getType() == ServiceEvent.REGISTERED) {
                synchronized (ctxWaitLock) {
                    applicationContext = (ApplicationContext) bundleContext.getService(event.getServiceReference());
                    ctxWaitLock.notify();
                }
            } else if (event.getType() == ServiceEvent.UNREGISTERING) {
                // new listeners will be created with the new context, this one should get unregistered
                bundleContext.removeServiceListener(this);
            }
        }
    }

    public ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = getApplicationContextFromBundleContext();
            if (applicationContext == null) {
                waitForCtx();
                if (applicationContext == null) {
                    throw new IllegalStateException("Entities application context unavailable in: " + getClass());
                }
            }
        }
        return applicationContext;
    }

    private ApplicationContext getApplicationContextFromBundleContext() {
        ApplicationContext entitiesBundleApplicationContext = null;

        try {
            entitiesBundleApplicationContext = getEntitiesBundleApplicationContext(findMdsEntitiesBundle(bundleContext));
        } catch (InvalidSyntaxException e) {
            logger.error("Invalid syntax of the filter passed to the bundle context");
        }

        return entitiesBundleApplicationContext;
    }

    private ApplicationContext getEntitiesBundleApplicationContext(Bundle entitiesBundle)
            throws InvalidSyntaxException {

        ApplicationContext context = null;

        if (entitiesBundle != null) {

            BundleContext entitiesBundleContext = entitiesBundle.getBundleContext();
            List<ServiceReference<ApplicationContext>> references = getApplicationContextReferences(entitiesBundleContext);

            if (references.size() == 1) {
                context = entitiesBundleContext.getService(references.get(0));
            } else if (references.size() > 1) {
                logger.warn("Multiple entities bundle application contexts found");
            } else {
                logger.debug("No entities bundle application context found");
            }
        } else {
            logger.debug("Entities bundle not found");
        }

        return context;
    }

    private List<ServiceReference<ApplicationContext>> getApplicationContextReferences(BundleContext bundleContext)
            throws InvalidSyntaxException {

        List<ServiceReference<ApplicationContext>> references = new ArrayList<>();

        if (bundleContext != null) {
            references.addAll(bundleContext.getServiceReferences(
                    ApplicationContext.class,
                    String.format(BUNDLE_SYMBOLIC_NAME_FILTER, MDS_ENTITIES_SYMBOLIC_NAME)
            ));
        }

        return references;
    }


    protected Logger getLogger() {
        return logger;
    }

    protected T getService() {
        if (service == null) {
            service = getApplicationContext().getBean(getServiceClass());
        }
        return service;
    }

    protected abstract Class<T> getServiceClass();

    private void waitForCtx() {
        synchronized (ctxWaitLock) {
            if (applicationContext == null) {
                try {
                    logger.debug("Waiting {} ms for the entities context", CTX_WAIT_TIME_MS);
                    ctxWaitLock.wait(CTX_WAIT_TIME_MS);
                } catch (InterruptedException e) {
                    logger.debug("Interrupted while waiting for the application context");
                }
            }
        }
    }
}
