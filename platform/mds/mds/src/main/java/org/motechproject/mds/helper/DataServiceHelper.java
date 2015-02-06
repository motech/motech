package org.motechproject.mds.helper;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.ex.entity.ServiceNotFoundException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.osgi.web.util.OSGiServiceUtils;
import org.osgi.framework.BundleContext;

/**
 * The <code>DataServiceHelper</code> is a helper class that simplifies retrieving Data Service
 * for a given entity.
 *
 * @see org.motechproject.mds.service.MotechDataService
 * @see org.motechproject.mds.domain.Entity
 */
public final class DataServiceHelper {

    private DataServiceHelper() {
    }

    public static MotechDataService getDataService(BundleContext bundleContext, String entityClass) {
        String interfaceName = MotechClassPool.getInterfaceName(entityClass);
        MotechDataService dataService = OSGiServiceUtils.findService(bundleContext, interfaceName);
        if (dataService == null) {
            throw new ServiceNotFoundException();
        }
        return dataService;
    }

    public static MotechDataService getDataService(BundleContext bundleContext, Entity entity) {
        return getDataService(bundleContext, entity.getClassName());
    }
}
