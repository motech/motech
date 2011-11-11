package org.motechproject.openmrs.omod.advice;

import org.openmrs.Obs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

public class ObsAdvice implements AfterReturningAdvice {

    private Logger log = LoggerFactory.getLogger(ObsAdvice.class);

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        Obs obs = (Obs) returnValue;
        log.info("intercepting method invocation: " + obs.getId());
    }
}
