package org.motechproject.server.event.annotations;

import java.lang.reflect.Method;

import org.motechproject.scheduler.domain.MotechEvent;
import org.springframework.util.ReflectionUtils;

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
        ReflectionUtils.invokeMethod(method, bean, event);
    }

}
