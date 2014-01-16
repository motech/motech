package org.motechproject.mds.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.motechproject.mds.builder.MDSClassLoader;

/**
 * The <code>BaseMdsAspect</code> class is a base class for all aspects in mds module.
 */
public abstract class BaseMdsAspect {

    protected abstract void isExecutable();

    protected abstract void checkTarget(Object target);

    @Around("isExecutable()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable { // NO CHECKSTYLE IllegalThrowsCheck
        checkTarget(joinPoint.getTarget());
        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(MDSClassLoader.PERSISTANCE);

            return joinPoint.proceed();
        } finally {
            Thread.currentThread().setContextClassLoader(webAppClassLoader);
        }
    }

}
