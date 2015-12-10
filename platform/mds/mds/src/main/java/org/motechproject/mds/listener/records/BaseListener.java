package org.motechproject.mds.listener.records;

import org.motechproject.mds.helper.MdsBundleHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * A base class for listeners in MDS. Since listeners get constructed by JDO,
 * we don't normally have access to the spring context of MDS entities. This class
 * retrieves this context by registering as a service listener that for the
 * {@link org.springframework.context.ApplicationContext}. After the context gets
 * retrieved the {@link #afterContextRegistered()} gets called, which allows implementing
 * listeners to initialize.
 */
public abstract class BaseListener implements ServiceListener {

    private static final int CTX_WAIT_TIME_MS = 30000;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Object ctxWaitLock = new Object();
    private final BundleContext bundleContext;

    private ApplicationContext applicationContext;

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
        if (event.getType() == ServiceEvent.REGISTERED &&
                MdsBundleHelper.isMdsEntitiesBundle(event.getServiceReference().getBundle())) {
            synchronized (ctxWaitLock) {
                applicationContext = (ApplicationContext) bundleContext.getService(event.getServiceReference());
                ctxWaitLock.notify();
            }
            afterContextRegistered();
        }
    }

    public ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            waitForCtx();
            if (applicationContext == null) {
                throw new IllegalStateException("Entities application context unavailable in: " + getClass());
            }
        }
        return applicationContext;
    }


    protected Logger getLogger() {
        return logger;
    }

    protected abstract void afterContextRegistered();

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
