package org.motechproject.server.event;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.server.osgi.PlatformConstants;

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
    private static String bundleError;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (method.getName().equals(HANDLE_EVENT_METHOD)) {
            try {
                handleEvent(args);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Invalid event received", e);
            }
            return null;
        } else {
            return method.invoke(proxy, args);
        }
    }

    public void handleEvent(Object[] args) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        if (ArrayUtils.isNotEmpty(args)) {
            Class eventClass = args[0].getClass();
            Method getPropertyMethod = eventClass.getMethod("getProperty", String.class);
            bundleError = (String) getPropertyMethod.invoke(args[0], PlatformConstants.BUNDLE_ERROR_EXCEPTION);
        } else {
            bundleError = "";
        }
    }

    public static boolean isBundleError() {
        return bundleError != null;
    }

    public static String getBundleError() {
        return bundleError;
    }
}
