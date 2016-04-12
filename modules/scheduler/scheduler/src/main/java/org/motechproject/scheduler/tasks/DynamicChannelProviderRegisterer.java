package org.motechproject.scheduler.tasks;

import org.motechproject.tasks.service.DynamicChannelProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Responsible for registering {@link SchedulerChannelProvider} service whenever Tasks module becomes available.
 */
@Component
public class DynamicChannelProviderRegisterer implements BundleTrackerCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicChannelProviderRegisterer.class);

    private static final String TASKS_BUNDLE_SYMBOLIC_NAME = "org.motechproject.motech-tasks";

    @Autowired
    private Properties sqlProperties;

    @Autowired
    private BundleContext bundleContext;

    @PostConstruct
    public void init() {

        if (bundleContext.getBundle(TASKS_BUNDLE_SYMBOLIC_NAME) != null) {
            register();
        }

        BundleTracker taskTracker = new BundleTracker(bundleContext, Bundle.ACTIVE, this);
        taskTracker.open();
    }

    @Override
    public Object addingBundle(Bundle bundle, BundleEvent bundleEvent) {
        if (TASKS_BUNDLE_SYMBOLIC_NAME.equals(bundle.getSymbolicName())) {
            LOGGER.info("Tasks module added, registering SchedulerChannelProvider service");
            register();
        }
        return null;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, Object o) {
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent bundleEvent, Object o) {

    }

    private void register() {
        bundleContext.registerService(
                DynamicChannelProvider.class.getName(),
                new SchedulerChannelProvider(sqlProperties),
                new Hashtable<>()
        );
        LOGGER.info("Registered SchedulerChannelProvider service");
    }
}
