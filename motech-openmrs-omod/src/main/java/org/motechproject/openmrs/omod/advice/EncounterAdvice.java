package org.motechproject.openmrs.omod.advice;

import org.openmrs.Encounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

public class EncounterAdvice implements AfterReturningAdvice {

    private Logger log = LoggerFactory.getLogger(EncounterAdvice.class);

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        Encounter encounter = (Encounter) returnValue;
        log.info("intercepting method invocation: " + encounter.getId());
    }
}
