package org.motechproject.server.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <code>BundleErrorEventListener</code> is responsible to listen to bundles startup errors.
 *
 * @see org.motechproject.server.osgi.PlatformActivator
 */
public class BundleErrorEventListener implements InvocationHandler {

    private static final String HANDLE_EVENT_METHOD = "handleEvent";
    private static boolean bundleError;

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
        bundleError = true;
    }

    public static boolean isBundleError() {
        return bundleError;
    }
}
