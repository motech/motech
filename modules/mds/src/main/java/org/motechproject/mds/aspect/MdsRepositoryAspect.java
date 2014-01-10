package org.motechproject.mds.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.motechproject.mds.repository.BaseMdsRepository;
import org.springframework.stereotype.Component;

/**
 * The main purpose of the <code>MdsRepositoryAspect</code> class is change class loader for
 * current thread while methods inside repository classes are executed. After performing
 * a repository method, the old class loader is restored to current thread.
 *
 * @see org.aspectj.lang.annotation.Aspect
 * @see org.motechproject.mds.repository.BaseMdsRepository
 */
@Aspect
@Component
public class MdsRepositoryAspect {

    @Around("within(org.motechproject.mds.repository.All*)")
    public Object changeClassLoader(ProceedingJoinPoint joinPoint) throws Throwable { // NO CHECKSTYLE IllegalThrowsCheck
        Object target = joinPoint.getTarget();

        if (!(target instanceof BaseMdsRepository)) {
            throw new IllegalStateException(
                    "The target class should extend " + BaseMdsRepository.class.getName()
            );
        }

        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(BaseMdsRepository.class.getClassLoader());

            return joinPoint.proceed();
        } finally {
            Thread.currentThread().setContextClassLoader(webAppClassLoader);
        }
    }
}
