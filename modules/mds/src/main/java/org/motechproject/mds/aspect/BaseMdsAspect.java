package org.motechproject.mds.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.motechproject.mds.PersistanceClassLoader;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The <code>BaseMdsAspect</code> class is a base class for all aspects in mds module.
 */
public abstract class BaseMdsAspect {
    private PersistanceClassLoader persistanceClassLoader;

    protected abstract void isChangeClassLoader();

    protected abstract void checkTarget(Object target);

    @Around("isChangeClassLoader()")
    public Object makeChange(ProceedingJoinPoint joinPoint) throws Throwable { // NO CHECKSTYLE IllegalThrowsCheck
        checkTarget(joinPoint.getTarget());
        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(getPersistanceClassLoader());

            return joinPoint.proceed();
        } finally {
            Thread.currentThread().setContextClassLoader(webAppClassLoader);
        }
    }

    protected PersistanceClassLoader getPersistanceClassLoader() {
        return persistanceClassLoader;
    }

    @Autowired
    public void setPersistanceClassLoader(PersistanceClassLoader persistanceClassLoader) {
        this.persistanceClassLoader = persistanceClassLoader;
    }
}
