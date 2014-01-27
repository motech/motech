package org.motechproject.mds.osgi;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.mds.annotations.SeussAnnotationProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The <code>SeussApplicationContextTracker</code> in Motech Data Services listens to the service registrations
 * and passes application contexts to the SeussAnnotationProcess for annotation scanning
 */
@Component
public class SeussApplicationContextTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeussApplicationContextTracker.class);

    private SeussServiceTracker applicationContextTracker;

    @PostConstruct
    public void startTracker() {
        if (applicationContextTracker == null && FrameworkUtil.getBundle(this.getClass()) != null) {
            applicationContextTracker = new SeussServiceTracker(
                    FrameworkUtil.getBundle(this.getClass()).getBundleContext());
            applicationContextTracker.open();
        }
    }


    private class SeussServiceTracker extends ServiceTracker {

        private List<String> contextsProcessed = Collections.synchronizedList(new ArrayList<String>());

        private SeussAnnotationProcessor seussAnnotationProcessor = new SeussAnnotationProcessor();

        public SeussServiceTracker(BundleContext bundleContext) {
            super(bundleContext, ApplicationContext.class.getName(), null);
        }

        @Override
        public Object addingService(ServiceReference serviceReference) {
            ApplicationContext applicationContext = (ApplicationContext) super.addingService(serviceReference);
            LOGGER.debug("Staring to process " + applicationContext.getDisplayName());

            if (ApplicationContextServiceReferenceUtils.isNotValid(serviceReference)) {
                return applicationContext;
            }

            if (contextsProcessed.contains(applicationContext.getId())) {
                return applicationContext;
            }

            contextsProcessed.add(applicationContext.getId());
            seussAnnotationProcessor.findAnnotations(serviceReference.getBundle().getBundleContext());

            LOGGER.debug("Processed " + applicationContext.getDisplayName());

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
