package org.motechproject.mds.listener;

import org.motechproject.mds.helper.MdsBundleHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
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
            applicationContext = (ApplicationContext) bundleContext.getService(event.getServiceReference());
            afterContextRegistered();
        }
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected abstract void afterContextRegistered();
}
