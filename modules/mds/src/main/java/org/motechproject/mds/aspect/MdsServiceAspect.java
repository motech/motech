package org.motechproject.mds.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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
public class MdsServiceAspect extends BaseMdsAspect {

    @Override
    @Pointcut("within(org.motechproject.mds.service.impl.*)")
    protected void isChangeClassLoader() {
        // Left blank.
        // Annotation does all the work.
    }

    @Override
    protected void checkTarget(Object target) {
        if (!(target instanceof BaseMdsService)) {
            throw new IllegalStateException(
                    "The target class should extend " + BaseMdsService.class.getName()
            );
        }
    }

}
