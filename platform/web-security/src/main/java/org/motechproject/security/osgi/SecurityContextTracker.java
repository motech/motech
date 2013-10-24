package org.motechproject.security.osgi;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.security.annotations.SecurityAnnotationBeanPostProcessor;
import org.motechproject.security.service.SecurityRoleLoader;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.SecurityRuleLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ServiceTracker} that tracks {@link ApplicationContext} objects published as OSGi services.
 * When a new context becomes available it processed by this class using {@link SecurityAnnotationBeanPostProcessor}
 * for processing permissions declared by the module in its annotations. It also uses a {@link SecurityRoleLoader} for
 * loading {@code role.json} files contained in the module.
 *
 * @see SecurityAnnotationBeanPostProcessor
 * @see SecurityRoleLoader
 */
public class SecurityContextTracker extends ServiceTracker {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityContextTracker.class);

    private final Object lock = new Object();

    private List<String> contextProcessed = new ArrayList<>();

    private SecurityAnnotationBeanPostProcessor securityAnnotationBeanPostProcessor;
    private SecurityRoleLoader securityRoleLoader;
    private SecurityRuleLoader securityRuleLoader;

    public SecurityContextTracker(BundleContext bundleContext, MotechRoleService roleService, MotechPermissionService permissionService, SecurityRuleLoader securityRuleLoader) {
        super(bundleContext, ApplicationContext.class.getName(), null);
        this.securityAnnotationBeanPostProcessor = new SecurityAnnotationBeanPostProcessor(permissionService);
        this.securityRoleLoader = new SecurityRoleLoader(roleService);
        this.securityRuleLoader = securityRuleLoader;
    }

    @Override
    public Object addingService(ServiceReference reference) {
        ApplicationContext applicationContext = (ApplicationContext) super.addingService(reference);

        LOG.debug("Starting to process " + applicationContext.getDisplayName());

        if (ApplicationContextServiceReferenceUtils.isNotValid(reference)) {
            return applicationContext;
        }

        String contextId = applicationContext.getId();

        synchronized (lock) {
            if (!contextProcessed.contains(contextId)) {
                securityAnnotationBeanPostProcessor.processAnnotations(applicationContext);
                securityRoleLoader.loadRoles(applicationContext);
                securityRuleLoader.loadRules(applicationContext);
                contextProcessed.add(contextId);
            }
        }

        return applicationContext;
    }
}
