package org.motechproject.mds.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.service.BaseMdsService;
import org.springframework.stereotype.Component;

/**
 * The main purpose of the <code>MdsServiceAspect</code> class is change class loader for
 * current thread while methods inside service classes are executed. After performing
 * a service method, the old class loader is restored to current thread.
 *
 * @see org.aspectj.lang.annotation.Aspect
 * @see org.motechproject.mds.service.BaseMdsService
 */
@Aspect
@Component
public class ServiceAspect {

    @Around("within(org.motechproject.mds.service.impl..*)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable { // NO CHECKSTYLE IllegalThrowsCheck
        checkTarget(joinPoint.getTarget());
        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(MDSClassLoader.getInstance());

            return joinPoint.proceed();
        } finally {
            Thread.currentThread().setContextClassLoader(webAppClassLoader);
        }
    }

    private void checkTarget(Object target) {
        if (!(target instanceof BaseMdsService)) {
            throw new IllegalStateException(
                    "The target class should extend " + BaseMdsService.class.getName()
            );
        }
    }

}
