package org.motechproject.openmrs.omod.advice;

import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseAdvice implements AfterReturningAdvice {
    private Logger log = LoggerFactory.getLogger(BaseAdvice.class);

    public static final String ADVICE_EVENT_RETURNED_VALUE = "returnedValue";
    public static final String ADVICE_EVENT_METHOD_INVOKED = "methodInvoked";

    private EventRelay eventRelay;

    public BaseAdvice() {
        this.eventRelay = EventContext.getInstance().getEventRelay();
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        String methodName = method.getName();
        Map params = new HashMap();
        params.put(ADVICE_EVENT_METHOD_INVOKED, methodName);
        params.put(ADVICE_EVENT_RETURNED_VALUE, returnValue);

        eventRelay.sendEventMessage(new MotechEvent(this.getClass().getName(), params));
        log.info("intercepting service: " + methodName + "|" + params);
    }
}
