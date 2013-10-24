package org.motechproject.security.osgi;

import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.SecurityRuleLoader;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * This class is responsible for creating
 * the SecurityContextTracker once the
 * security bundle has been started and processed
 */
public class RolePermissionRegistrationListener implements OsgiServiceRegistrationListener {

    private final Object lock = new Object();

    @Autowired
    private BundleContext bundleContext;

    @Autowired
    private SecurityRuleLoader securityRuleLoader;
    private MotechRoleService roleService;
    private MotechPermissionService permissionService;

    private SecurityContextTracker securityContextTracker;

    @Override
    public void registered(Object service, Map serviceProperties) {
        if (service instanceof MotechRoleService) {
            roleService = (MotechRoleService) service;
        } else if (service instanceof MotechPermissionService) {
            permissionService = (MotechPermissionService) service;
        }

        openTracker();
    }

    @Override
    public void unregistered(Object service, Map serviceProperties) {
        if (securityContextTracker != null) {
            securityContextTracker.close();
        }
    }

    private void openTracker() {
        synchronized (lock) {
            if (roleService != null && permissionService != null && securityContextTracker == null) {
                securityContextTracker = new SecurityContextTracker(bundleContext, roleService, permissionService, securityRuleLoader);
            } else {
                return;
            }
        }

        securityContextTracker.open(true);
    }
}
