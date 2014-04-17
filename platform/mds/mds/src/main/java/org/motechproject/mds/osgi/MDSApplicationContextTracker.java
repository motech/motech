package org.motechproject.mds.osgi;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.mds.annotations.internal.MDSAnnotationProcessor;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.util.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * The <code>MDSApplicationContextTracker</code> in Motech Data Services listens to the service
 * registrations and passes application contexts to the MDSAnnotationProcess for annotation
 * scanning
 */
@Component
public class MDSApplicationContextTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(MDSApplicationContextTracker.class);

    private MdsBundleListener mdsBundleListener;

    private MDSAnnotationProcessor processor;
    private JarGeneratorService jarGeneratorService;
    private BundleContext bundleContext;

    // called by the initializer after the initial entities bundle was generated
    public void startTracker() {
        processInstalledBundles();

        if (null == mdsBundleListener) {
            mdsBundleListener = new MdsBundleListener();
            bundleContext.addBundleListener(mdsBundleListener);
            LOGGER.info("Scanning for MDS annotations");
        }
    }

    private void processInstalledBundles() {
        for (Bundle bundle : bundleContext.getBundles()) {
            process(bundle);
        }
    }

    private void process(Bundle bundle) {
        // we skip the generated entities bundle and the framework bundle
        if (Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME.equals(bundle.getSymbolicName()) ||
                bundle.getBundleId() == 0) {
            return;
        }
        // we also skip bundles which locations start with "link:", as these are pax exam bundles, which we
        // encounter only during tests. Maybe in some distant future, support for resolving these locations will be
        // added, but there is no need to do it right now.
        if (StringUtils.startsWith(bundle.getLocation(), "link:") ||
                StringUtils.startsWith(bundle.getLocation(), "local")) {
            return;
        }

        LOGGER.info("Processing bundle {}", bundle.getSymbolicName());

        boolean annotationsFound = processor.processAnnotations(bundle);
        // if we found annotations, we will refresh the bundle in order to start weaving the classes it exposes
        if (annotationsFound) {
            LOGGER.info("Refreshing wiring for bundle {}", bundle.getSymbolicName());

            jarGeneratorService.regenerateMdsDataBundle(true);

            FrameworkWiring framework = bundleContext.getBundle(0).adapt(FrameworkWiring.class);
            framework.refreshBundles(Arrays.asList(bundle));
        }
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

    public class MdsBundleListener implements SynchronousBundleListener {

        @Override
        public void bundleChanged(BundleEvent event) {
            Bundle bundle = event.getBundle();

            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Bundle event of type {} received from {}: {} -> {}",
                        new String[]{
                                OsgiStringUtils.nullSafeBundleEventToString(event.getType()),
                                bundle.getSymbolicName(),
                                String.valueOf(event.getType()),
                                String.valueOf(bundle.getState())
                        });
            }

            if (event.getType() == BundleEvent.INSTALLED) {
                process(bundle);
            }
        }
    }
}
