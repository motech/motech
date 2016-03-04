package org.motechproject.event.listener.annotations;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;

import java.lang.reflect.Method;

/**
 * Represents a <code>MotechListener</code> proxy, providing access to the listener's
 * name, bean, method. Constructed for listeners defined using annotations.
 *
 * @author yyonkov
 */
public abstract class MotechListenerAbstractProxy implements EventListener {

    private final String name;
    private final Object bean;
    private final Method method;

    /**
     *
     * @param name the unique listener identifier/key
     * @param bean the bean where handler exists
     * @param method the method which will be invoked when the particular event will be fired
     */
    public MotechListenerAbstractProxy(String name, Object bean, Method method) {
        this.name = name;
        this.bean = bean;
        this.method = method;
    }

    /**
     * Calls handler for the concrete proxy.
     *
     * @param event the event which will be handled
     */
    public abstract void callHandler(MotechEvent event);

    @Override
    public void handle(MotechEvent event) {
        callHandler(event);
    }

    @Override
    public String getIdentifier() {
        return this.name;
    }

    /**
     * Returns the bean where handler exists.
     *
     * @return the bean where handler exists
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Returns the handler of a event.
     *
     * @return the method which handles a event.
     */
    public Method getMethod() {
        return method;
    }
}
