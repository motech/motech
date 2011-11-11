package org.motechproject.openmrs.omod.advice;

import org.openmrs.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;
import java.util.List;

public class PatientAdvice implements AfterReturningAdvice{

    private Logger log = LoggerFactory.getLogger(PatientAdvice.class);

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        List<Patient> patients = (List<Patient>) returnValue;
        log.info("intercepting method invocation: " + patients.size());
    }
}
