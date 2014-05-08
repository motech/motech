package org.motechproject.mds.osgi;

import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.mds.annotations.internal.MDSAnnotationProcessor;
import org.motechproject.mds.service.JarGeneratorService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.startsWith;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;

/**
 * The <code>MdsBundleWatcher</code> in Motech Data Services listens for bundle installation and
 * processes the annotations in the given bundle. It also processes all installed bundles after startup.
 * After annotations are found in a bundle, the entities jar is regenerated and the target bundle is refreshed.
 */
@Component
public class MdsBundleWatcher implements BundleListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdsBundleWatcher.class);

    private MDSAnnotationProcessor processor;
    private JarGeneratorService jarGeneratorService;
    private BundleContext bundleContext;
    private EntitiesBundleMonitor monitor;

    private final Object lock = new Object();

    // called by the initializer after the initial entities bundle was generated
    public void start() {
        LOGGER.info("Scanning for MDS annotations");
        processInstalledBundles();
        bundleContext.addBundleListener(this);
    }

    private void processInstalledBundles() {
        List<Bundle> bundles = new ArrayList<>();
        boolean needRefresh = false;

        for (Bundle bundle : bundleContext.getBundles()) {
            boolean annotationsFound = process(bundle);

            if (annotationsFound) {
                bundles.add(bundle);
                needRefresh = true;
            }
        }

        // if we found annotations, we will refresh the bundle in order to start weaving the
        // classes it exposes
        if (needRefresh) {
            refreshBundles(bundles);
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        Bundle bundle = event.getBundle();

        int eventType = event.getType();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Bundle event of type {} received from {}: {} -> {}",
                    new String[]{
                            OsgiStringUtils.nullSafeBundleEventToString(event.getType()),
                            bundle.getSymbolicName(),
                            String.valueOf(eventType),
                            String.valueOf(bundle.getState())
                    }
            );
        }

        if (eventType == BundleEvent.INSTALLED || eventType == BundleEvent.UPDATED) {
            boolean needRefresh = process(bundle);

            // if we found annotations, we will refresh the bundle in order to start weaving the
            // classes it exposes
            if (needRefresh) {
                refreshBundle(bundle);
            }
        }
    }

    private boolean process(Bundle bundle) {
        synchronized (lock) {
            // we skip the generated entities bundle and the framework bundle
            if (MDS_ENTITIES_SYMBOLIC_NAME.equals(bundle.getSymbolicName()) || bundle.getBundleId() == 0) {
                return false;
            }

            // we also skip bundles which locations start with "link:", as these are pax exam bundles, which we
            // encounter only during tests. Maybe in some distant future, support for resolving these locations will be
            // added, but there is no need to do it right now.
            if (startsWith(bundle.getLocation(), "link:") || startsWith(bundle.getLocation(), "local")) {
                return false;
            }

            LOGGER.debug("Processing bundle {}", bundle.getSymbolicName());

            return processor.processAnnotations(bundle);
        }
    }

    private void refreshBundle(Bundle bundle) {
        refreshBundles(Arrays.asList(bundle));
    }

    private void refreshBundles(List<Bundle> bundles) {
        for (Bundle bundle : bundles) {
            LOGGER.info("Refreshing wiring for bundle {}", bundle.getSymbolicName());
        }

        // we generate the entities bundle but not start it to avoid exceptions when the framework
        // will refresh bundles
        jarGeneratorService.regenerateMdsDataBundle(true, false);

        FrameworkWiring framework = bundleContext.getBundle(0).adapt(FrameworkWiring.class);
        framework.refreshBundles(bundles);

        // after refreshing all bundles we can start the entities bundle
        monitor.start();
    }

    @Autowired
    public void setProcessor(MDSAnnotationProcessor processor) {
        this.processor = processor;
    }

    @Autowired
    public void setJarGeneratorService(JarGeneratorService jarGeneratorService) {
        this.jarGeneratorService = jarGeneratorService;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setMonitor(EntitiesBundleMonitor monitor) {
        this.monitor = monitor;
    }
}
