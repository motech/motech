package org.motechproject.security.osgi;

import org.motechproject.osgi.web.ApplicationContextTracker;
import org.motechproject.security.annotations.SecurityAnnotationBeanPostProcessor;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.SecurityRoleLoader;
import org.motechproject.security.service.SecurityRuleLoaderService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * A {@link ServiceTracker} that tracks {@link ApplicationContext} objects published as OSGi services.
 * When a new context becomes available it is processed by this class using {@link SecurityAnnotationBeanPostProcessor}
 * for processing permissions declared by the module in its annotations. It also uses a {@link SecurityRoleLoader} for
 * loading {@code role.json} files contained in the module. Finally, it uses a {@link SecurityRuleLoaderService} to
 * scan the module for any module-defined security rules, that are added to the system and used right after the
 * bundle defining them is started.
 *
 * @see SecurityAnnotationBeanPostProcessor
 * @see SecurityRoleLoader
 */
public class SecurityContextTracker extends ApplicationContextTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityContextTracker.class);

    private SecurityAnnotationBeanPostProcessor securityAnnotationBeanPostProcessor;
    private SecurityRoleLoader securityRoleLoader;
    private SecurityRuleLoaderService securityRuleLoader;

    public SecurityContextTracker(BundleContext bundleContext, MotechRoleService roleService, MotechPermissionService permissionService, SecurityRuleLoaderService securityRuleLoader) {
        super(bundleContext);
        this.securityAnnotationBeanPostProcessor = new SecurityAnnotationBeanPostProcessor(permissionService);
        this.securityRoleLoader = new SecurityRoleLoader(roleService, permissionService);
        this.securityRuleLoader = securityRuleLoader;
    }

    /**
     * Listens for module and then process its context
     * by looking for security annotations.
     * Also loads all roles and rules from
     * loaded context.
     *
     * @param reference to module
     * @return ApplicationContext
     */
    @Override
    public Object addingService(ServiceReference reference) {
        ApplicationContext applicationContext = (ApplicationContext) super.addingService(reference);

        if (applicationContext == null) {
            return null;
        }

        LOGGER.info("Starting to process {}", applicationContext.getDisplayName());

        String contextId = applicationContext.getId();
        LOGGER.debug("Application context id: {}", contextId);

        synchronized (getLock()) {
            if (contextInvalidOrProcessed(reference, applicationContext)) {
                return applicationContext;
            }
            markAsProcessed(applicationContext);

            securityAnnotationBeanPostProcessor.processAnnotations(applicationContext);
            securityRoleLoader.loadRoles(applicationContext);
            securityRuleLoader.loadRules(applicationContext);
        }

        LOGGER.info("End to process {}", applicationContext.getDisplayName());

        return applicationContext;
    }

    /**
     * Removes service from Application context
     *
     * @param reference to the service that is going to be removed
     * @param service ApplicationContext
     */
    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);

        synchronized (getLock()) {
            removeFromProcessed((ApplicationContext) service);
        }
    }
}
