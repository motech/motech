package org.motechproject.mds.osgi;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.mds.annotations.internal.MDSAnnotationProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeSymbolicName;

/**
 * The <code>MDSApplicationContextTracker</code> in Motech Data Services listens to the service
 * registrations and passes application contexts to the MDSAnnotationProcess for annotation
 * scanning
 */
@Component
public class MDSApplicationContextTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(MDSApplicationContextTracker.class);

    private MDSServiceTracker serviceTracker;

    private MDSAnnotationProcessor processor;

    @PostConstruct
    public void startTracker() {
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());

        if (null == serviceTracker && bundle != null) {
            serviceTracker = new MDSServiceTracker(bundle.getBundleContext());
            serviceTracker.open();
        }
    }

    @PreDestroy
    public void stopTracker() {
        if (null != serviceTracker) {
            serviceTracker.close();
        }
    }

    @Autowired
    public void setProcessor(MDSAnnotationProcessor processor) {
        this.processor = processor;
    }

    private class MDSServiceTracker extends ServiceTracker {
        private final List<String> contextsProcessed;

        public MDSServiceTracker(BundleContext bundleContext) {
            super(bundleContext, ApplicationContext.class.getName(), null);

            this.contextsProcessed = Collections.synchronizedList(new ArrayList<String>());
        }

        @Override
        public Object addingService(ServiceReference serviceReference) {
            ApplicationContext applicationContext = (ApplicationContext) super.addingService(serviceReference);
            LOGGER.debug("Starting to process {}", applicationContext.getDisplayName());

            if (ApplicationContextServiceReferenceUtils.isValid(serviceReference)) {
                synchronized (contextsProcessed) {
                    String bundleSymbolicName = nullSafeSymbolicName(serviceReference.getBundle());

                    if (!contextsProcessed.contains(bundleSymbolicName)) {
                        contextsProcessed.add(bundleSymbolicName);

                        processor.processAnnotations(serviceReference.getBundle());
                    }
                }
            }

            LOGGER.debug("Processed {}", applicationContext.getDisplayName());
            return applicationContext;
        }

        @Override
        public void removedService(ServiceReference reference, Object service) {
            super.removedService(reference, service);
            ApplicationContext applicationContext = (ApplicationContext) service;

            if (ApplicationContextServiceReferenceUtils.isValid(reference)) {
                contextsProcessed.remove(applicationContext.getId());
            }
        }

    }


}
