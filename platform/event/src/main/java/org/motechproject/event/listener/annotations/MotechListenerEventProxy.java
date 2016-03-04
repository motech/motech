package org.motechproject.event.listener.annotations;

import org.motechproject.event.MotechEvent;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Represents the type of <code>MotechListener</code> proxy where handler is a method with the
 * <code>MotechEvent</code> parameter.
 */
public class MotechListenerEventProxy extends MotechListenerAbstractProxy {

    /**
      * @see org.motechproject.event.listener.annotations.MotechListenerAbstractProxy#MotechListenerAbstractProxy(String, Object, java.lang.reflect.Method)
      */
    public MotechListenerEventProxy(String name, Object bean, Method method) {
        super(name, bean, method);
    }

    @Override
    public void callHandler(MotechEvent event) {
        ReflectionUtils.invokeMethod(getMethod(), getBean(), event);
    }

}
