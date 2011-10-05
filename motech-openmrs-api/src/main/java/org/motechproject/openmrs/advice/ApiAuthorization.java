package org.motechproject.openmrs.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;

@Aspect
public class ApiAuthorization {
    private String openmrsUser;
    private String openmrsPassword;

    public ApiAuthorization(String openmrsUser, String openmrsPassword) {
        this.openmrsUser = openmrsUser;
        this.openmrsPassword = openmrsPassword;

    }

    @Pointcut("execution(@org.motechproject.openmrs.advice.ApiAuthorize * org.motechproject.openmrs.*.*(..))")
    private void openmrsSession() {
    }

    @Around("openmrsSession()")
    public Object authorize(ProceedingJoinPoint pjp) throws Throwable {
        Context.openSession();
        try {
            Context.authenticate(openmrsUser, openmrsPassword);
            Object proceed = pjp.proceed();
            return proceed;
        } catch (ContextAuthenticationException authenticationException) {
            return null;
        } finally {
            Context.closeSession();
        }
    }
}
