package org.motechproject.testing.osgi.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <code>BundleErrorEventListener</code> is responsible to listen to MOTECH osgi-platform
 * bundle startup errors.
 *
 * @see org.motechproject.server.osgi.PlatformActivator
 */
public class BundleErrorEventListener implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleErrorEventListener.class);
    private static final String HANDLE_EVENT_METHOD = "handleEvent";
    private boolean bundleError;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (method.getName().equals(HANDLE_EVENT_METHOD)) {
            handleEvent();
            return null;
        } else {
            return method.invoke(proxy, args);
        }
    }

    public void handleEvent() {
        LOGGER.error("Bundle startup error occurred.");
        bundleError = true;
    }

    public boolean isBundleError() {
        return bundleError;
    }
}
