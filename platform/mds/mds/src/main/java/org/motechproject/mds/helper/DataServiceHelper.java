package org.motechproject.mds.helper;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.exception.entity.ServiceNotFoundException;
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

    /**
     * Retrieves {@link org.motechproject.mds.service.MotechDataService} implementation for the
     * given entity class. It will throw {@link org.motechproject.mds.exception.entity.ServiceNotFoundException}, in
     * case a service for the given entity class cannot be found.
     *
     * @param bundleContext context of a bundle
     * @param entityClass fully qualified class name of an entity
     * @return generated {@link org.motechproject.mds.service.MotechDataService} implementation
     */
    public static MotechDataService getDataService(BundleContext bundleContext, String entityClass) {
        String interfaceName = MotechClassPool.getInterfaceName(entityClass);
        MotechDataService dataService = OSGiServiceUtils.findService(bundleContext, interfaceName);
        if (dataService == null) {
            throw new ServiceNotFoundException(interfaceName);
        }
        return dataService;
    }

    /**
     * Retrieves {@link org.motechproject.mds.service.MotechDataService} implementation for the
     * given entity. It will throw {@link org.motechproject.mds.exception.entity.ServiceNotFoundException}, in
     * case a service for the given entity class cannot be found.
     *
     * @param bundleContext context of a bundle
     * @param entity entity representation, to retrieve its service for
     * @return generated {@link org.motechproject.mds.service.MotechDataService} implementation
     */
    public static MotechDataService getDataService(BundleContext bundleContext, Entity entity) {
        return getDataService(bundleContext, entity.getClassName());
    }
}
