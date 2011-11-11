package org.motechproject.openmrs.omod.advice;

import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.openmrs.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class PatientAdvice implements AfterReturningAdvice {
    private Logger log = LoggerFactory.getLogger(PatientAdvice.class);
    private EventRelay eventRelay;
    private static final String PATIENT = "patient";
    private static final String METHOD = "method";

    public PatientAdvice() {
        this.eventRelay = EventContext.getInstance().getEventRelay();
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        List<Patient> patients = (List<Patient>) returnValue;
        String methodName = method.getName();

        HashMap eventParams = new HashMap();
        eventParams.put(PATIENT,patients);
        eventParams.put(METHOD, methodName);

        eventRelay.sendEventMessage(new MotechEvent(PatientAdvice.class.getName(), eventParams));
        log.info("intercepting method invocation: " + methodName);
    }
}
