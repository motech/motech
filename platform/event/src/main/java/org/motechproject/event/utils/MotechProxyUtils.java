package org.motechproject.event.utils;

import org.motechproject.event.listener.annotations.MotechListenerAbstractProxy;

/**
 * Provides utils for Motech Event proxies.
 */
public final class MotechProxyUtils {

    private MotechProxyUtils() {
    }

    /**
     * Returns <code>true</code> if the object is an instance of
     * <code>MotechListenerAbstractProxy</code>.
     *
     * @param object the object to be checked
     * @return <code>true</code> if the object is an instance of
     * <code>MotechListenerAbstractProxy</code>;
     * <code>false</code> otherwise
     */
    public static boolean isMotechListenerProxy(Object object) {
        return object instanceof MotechListenerAbstractProxy;
    }

    /**
     * Returns the bean from the <code>MotechListenerAbstractProxy</code>
     * if the proxy {@link #isMotechListenerProxy(Object)}, otherwise returns
     * the proxy.
     *
     * @param proxy the object to be checked
     * @return the proxy
     */
    public static Object getTargetIfProxied(Object proxy) {
        if (isMotechListenerProxy(proxy)) {
            MotechListenerAbstractProxy listenerProxy = (MotechListenerAbstractProxy) proxy;
            return listenerProxy.getBean();
        }
        return proxy;
    }
}
