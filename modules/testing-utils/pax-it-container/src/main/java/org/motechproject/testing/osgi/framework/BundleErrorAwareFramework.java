package org.motechproject.testing.osgi.framework;

import org.motechproject.server.osgi.PlatformConstants;
import org.motechproject.testing.osgi.event.BundleErrorEventListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 *  <code>BundleErrorAwareFramework</code> is a framework decorator that registers event listener
 *  that listen to bundle startup errors related to unresolved bundle requirements.
 */
public class BundleErrorAwareFramework extends FrameworkDecorator {

    private static final Logger LOG = LoggerFactory.getLogger(BundleErrorAwareFramework.class.getName());
    public static final String MOTECH_PLATFORM_BUNDLE = "org.motechproject.motech-osgi-platform";

    private BundleErrorEventListener bundleErrorEventListener;

    public BundleErrorAwareFramework(Framework framework) {
        super(framework);
        bundleErrorEventListener = new BundleErrorEventListener();
    }

    @Override
    public void init() throws BundleException {
        super.init();
        registerBundleListener();
    }

    public boolean isBundleError() {
        return bundleErrorEventListener.isBundleError();
    }

    private void registerBundleListener() {
        getBundleContext().addBundleListener(new BundleListener() {
            @Override
            public void bundleChanged(BundleEvent event) {
                if (event.getBundle() != null && event.getBundle().toString().contains(MOTECH_PLATFORM_BUNDLE)
                            && event.getType() == BundleEvent.RESOLVED) {
                    try {
                        registerBundleErrorEventListener(event.getBundle());
                    } catch (ClassNotFoundException e) {
                        LOG.warn("Unable to register listener.");
                    }
                }
            }
        });
    }

    private void registerBundleErrorEventListener(Bundle bundle) throws ClassNotFoundException {
        String activator = bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);
        Class activatorClass = bundle.loadClass(activator);
        ClassLoader eventListenerClassLoader = activatorClass.getClassLoader();
        Class<?> eventListenerClass = eventListenerClassLoader.loadClass(EventHandler.class.getName());

        Object proxy = Proxy.newProxyInstance(eventListenerClassLoader, new Class[]{eventListenerClass}, bundleErrorEventListener);

        Dictionary<String, String[]> properties = new Hashtable<>();
        properties.put(EventConstants.EVENT_TOPIC, new String[]{PlatformConstants.BUNDLE_ERROR_TOPIC});

        bundle.getBundleContext().registerService(eventListenerClass.getName(), proxy, properties);

        LOG.info("Registered " + EventHandler.class.getName() + " using " + eventListenerClassLoader + "class loader.");
    }
}
