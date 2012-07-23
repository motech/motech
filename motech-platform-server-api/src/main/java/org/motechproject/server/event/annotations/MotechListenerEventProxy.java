package org.motechproject.server.event.annotations;

import org.motechproject.scheduler.domain.MotechEvent;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Responsible for dispatching to {@code void doSomething(MotechEvent event) {} } type of handlers
 *
 * @author yyonkov
 */
public class MotechListenerEventProxy extends MotechListenerAbstractProxy {

    /**
     * @param name
     * @param bean
     * @param method
     */
    public MotechListenerEventProxy(String name, Object bean, Method method) {
        super(name, bean, method);
    }

    /* (non-Javadoc)
      * @see org.motechproject.server.event.annotations.MotechListenerAbstractProxy#callHandler(org.motechproject.scheduler.model.MotechEvent)
      */
    @Override
    public void callHandler(MotechEvent event) {
        ReflectionUtils.invokeMethod(getMethod(), getBean(), event);
    }

}
