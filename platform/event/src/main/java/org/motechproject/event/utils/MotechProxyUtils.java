package org.motechproject.event.utils;

import org.motechproject.event.listener.annotations.MotechListenerAbstractProxy;

public final class MotechProxyUtils {

    private MotechProxyUtils() {
    }

    public static boolean isMotechListenerProxy(Object object) {
        return object instanceof MotechListenerAbstractProxy;
    }

    public static Object getTargetIfProxied(Object proxy) {
        if (isMotechListenerProxy(proxy)) {
            MotechListenerAbstractProxy listnerProxy = (MotechListenerAbstractProxy) proxy;
            return listnerProxy.getBean();
        }
        return proxy;
    }
}
