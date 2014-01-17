package org.motechproject.mds.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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
public class MdsRepositoryAspect extends BaseMdsAspect {

    @Override
    @Pointcut("within(org.motechproject.mds.repository.All*)")
    protected void isExecutable() {
        // Left blank.
        // Annotation does all the work.
    }

    @Override
    protected void checkTarget(Object target) {
        if (!(target instanceof BaseMdsRepository)) {
            throw new IllegalStateException(
                    "The target class should extend " + BaseMdsRepository.class.getName()
            );
        }
    }
}
