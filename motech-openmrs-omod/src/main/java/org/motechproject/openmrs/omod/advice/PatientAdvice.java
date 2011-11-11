package org.motechproject.openmrs.omod.advice;

import org.openmrs.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

public class PatientAdvice implements AfterReturningAdvice{

    private Logger log = LoggerFactory.getLogger(PatientAdvice.class);

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        Patient patient = (Patient) returnValue;
        log.info("intercepting method invocation: " + patient.getId());
    }
}
