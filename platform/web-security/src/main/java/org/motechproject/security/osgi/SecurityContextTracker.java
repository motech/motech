package org.motechproject.security.osgi;

import org.motechproject.osgi.web.ApplicationContextTracker;
import org.motechproject.security.annotations.SecurityAnnotationBeanPostProcessor;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.SecurityRoleLoader;
import org.motechproject.security.service.SecurityRuleLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * A {@link ServiceTracker} that tracks {@link ApplicationContext} objects published as OSGi services.
 * When a new context becomes available it processed by this class using {@link SecurityAnnotationBeanPostProcessor}
 * for processing permissions declared by the module in its annotations. It also uses a {@link SecurityRoleLoader} for
 * loading {@code role.json} files contained in the module.
 *
 * @see SecurityAnnotationBeanPostProcessor
 * @see SecurityRoleLoader
 */
public class SecurityContextTracker extends ApplicationContextTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityContextTracker.class);

    private SecurityAnnotationBeanPostProcessor securityAnnotationBeanPostProcessor;
    private SecurityRoleLoader securityRoleLoader;
    private SecurityRuleLoader securityRuleLoader;

    public SecurityContextTracker(BundleContext bundleContext, MotechRoleService roleService, MotechPermissionService permissionService, SecurityRuleLoader securityRuleLoader) {
        super(bundleContext);
        this.securityAnnotationBeanPostProcessor = new SecurityAnnotationBeanPostProcessor(permissionService);
        this.securityRoleLoader = new SecurityRoleLoader(roleService);
        this.securityRuleLoader = securityRuleLoader;
    }

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

    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);

        synchronized (getLock()) {
            removeFromProcessed((ApplicationContext) service);
        }
    }
}
