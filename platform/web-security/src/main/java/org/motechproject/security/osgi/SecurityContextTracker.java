package org.motechproject.security.osgi;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.security.annotations.SecurityAnnotationBeanPostProcessor;
import org.motechproject.security.service.SecurityRoleLoader;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class SecurityContextTracker extends ServiceTracker {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityContextTracker.class);

    private final Object lock = new Object();

    private List<String> contextProcessed = new ArrayList<>();

    private SecurityAnnotationBeanPostProcessor securityAnnotationBeanPostProcessor;
    private SecurityRoleLoader securityRoleLoader;

    public SecurityContextTracker(BundleContext bundleContext, MotechRoleService roleService, MotechPermissionService permissionService) {
        super(bundleContext, ApplicationContext.class.getName(), null);
        this.securityAnnotationBeanPostProcessor = new SecurityAnnotationBeanPostProcessor(permissionService);
        this.securityRoleLoader = new SecurityRoleLoader(roleService);
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
                contextProcessed.add(contextId);
            }
        }

        return applicationContext;
    }
}
