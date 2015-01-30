package org.motechproject.security.osgi;

import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.SecurityRuleLoader;
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
    private SecurityRuleLoader securityRuleLoader;

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
        if (service instanceof MotechRoleService) {
            roleService = (MotechRoleService) service;
            LOGGER.debug("Found motech role service");
        } else if (service instanceof MotechPermissionService) {
            permissionService = (MotechPermissionService) service;
            LOGGER.debug("Found motech permission service");
        }

        openTracker();
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
        if (securityContextTracker != null) {
            securityContextTracker.close();
            trackerOpened = false;
        }
    }

    private void openTracker() {
        synchronized (lock) {
            if (roleService != null && permissionService != null && securityContextTracker == null) {
                securityContextTracker = new SecurityContextTracker(bundleContext, roleService, permissionService, securityRuleLoader);
            }
        }

        if  (!trackerOpened && securityContextTracker != null) {
            securityContextTracker.open(true);
            trackerOpened = true;
        }
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setSecurityRuleLoader(SecurityRuleLoader securityRuleLoader) {
        this.securityRuleLoader = securityRuleLoader;
    }
}
