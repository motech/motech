package org.motechproject.mds.javassist;

import javassist.ClassClassPath;
import javassist.ClassPool;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.impl.DefaultMotechDataService;

/**
 * This class holds the javasisst classpool, enriched by motech classes. All predefined additions to the ClassPool
 * should take place here. The classpool should also be retrieved using this class, in order to be sure that the a
 * initialization took place.
 */
public final class MotechClassPool {

    private static final ClassPool POOL;

    static {
        POOL = ClassPool.getDefault();

        POOL.appendClassPath(new ClassClassPath(MotechDataRepository.class));
        POOL.appendClassPath(new ClassClassPath(MotechDataService.class));
        POOL.appendClassPath(new ClassClassPath(DefaultMotechDataService.class));
    }

    public static ClassPool getDefault() {
        return POOL;
    }

    private MotechClassPool() {
    }
}
