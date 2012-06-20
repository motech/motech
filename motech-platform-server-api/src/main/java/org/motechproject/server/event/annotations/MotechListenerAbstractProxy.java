package org.motechproject.server.event.annotations;

import org.motechproject.MotechException;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.EventListener;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Event Listener Proxy base abstract class
 *
 * @author yyonkov
 */
public abstract class MotechListenerAbstractProxy implements EventListener {

    protected final String name;
    protected final Object bean;
    protected final Method method;

    /**
     * @param name
     * @param bean
     * @param method
     */
    public MotechListenerAbstractProxy(String name, Object bean, Method method) {
        this.name = name;
        this.bean = bean;
        this.method = method;
    }

    /**
     * Needs to be implemented by concrete Proxies
     *
     * @param event
     * @return
     */
    public abstract void callHandler(MotechEvent event);

    /* (non-Javadoc)
      * @see org.motechproject.server.event.EventListener#handle(org.motechproject.scheduler.model.MotechEvent)
      */
    @Override
    public void handle(MotechEvent event) {
        try {
            callHandler(event);
        } catch (Exception e) {
            LoggerFactory.getLogger(bean.getClass()).error("Failed to handle event", e);
            throw new MotechException("Failed to handle event", e);
        }
    }

    /* (non-Javadoc)
     * @see org.motechproject.server.event.EventListener#getIdentifier()
     */
    @Override
    public String getIdentifier() {
        return this.name;
    }

}
