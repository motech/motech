package org.motechproject.security.osgi;

import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.SecurityRuleLoaderService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * This class is responsible for creating
 * the SecurityContextTracker once the
 * security bundle has been started and processed.
 * Also allows manual binding of role and
 * permission services.
 */
public class RolePermissionRegistrationListener implements OsgiServiceRegistrationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RolePermissionRegistrationListener.class);

    private final Object lock = new Object();

    private BundleContext bundleContext;

    private SecurityRuleLoaderService securityRuleLoader;
    private MotechRoleService roleService;
    private MotechPermissionService permissionService;

    private SecurityContextTracker securityContextTracker;
    private boolean trackerOpened = false;

    /**
     * Listens to OSGi registration. If service
     * is instance of {@link org.motechproject.security.service.MotechRoleService}
     * or {@link org.motechproject.security.service.MotechPermissionService}
     * then assigns it to variable
     *
     * @param service to be assigned
     * @param serviceProperties map that contains service properties
     */
    @Override
    public void registered(Object service, Map serviceProperties) {
        synchronized (lock) {
            if (service instanceof MotechRoleService) {
                roleService = (MotechRoleService) service;
                LOGGER.debug("Found Motech role service");
            } else if (service instanceof MotechPermissionService) {
                permissionService = (MotechPermissionService) service;
                LOGGER.debug("Found Motech permission service");
            } else if (service instanceof SecurityRuleLoaderService) {
                securityRuleLoader = (SecurityRuleLoaderService) service;
                LOGGER.debug("Found SecurityRuleLoader service");
            }

            openTracker();
        }
    }

    /**
     * If SecurityContextTracker is set then close it
     * and set tracker as closed
     *
     * @param service
     * @param serviceProperties
     */
    @Override
    public void unregistered(Object service, Map serviceProperties) {
        synchronized (lock) {
            if (securityContextTracker != null) {
                LOGGER.debug("Closing security context tracker");
                securityContextTracker.close();
                securityContextTracker = null;
                trackerOpened = false;
            }
            if (service == roleService) {
                roleService = null;
                LOGGER.debug("Unregistering Motech role service");
            }
            if (service == permissionService) {
                LOGGER.debug("Unregistering Motech permission service");
                permissionService = null;
            }
            if (service == securityRuleLoader) {
                LOGGER.debug("Unregistering Security Rule Loader service");
                securityRuleLoader = null;
            }
        }
    }

    private void openTracker() {
        if (roleService != null && permissionService != null && securityRuleLoader != null && securityContextTracker == null) {
            LOGGER.debug("Creating SecurityContextTracker");
            securityContextTracker = new SecurityContextTracker(bundleContext, roleService, permissionService, securityRuleLoader);
        }

        if  (!trackerOpened && securityContextTracker != null) {
            LOGGER.debug("Opening SecurityContextTracker");
            securityContextTracker.open(true);
            trackerOpened = true;
        }
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
