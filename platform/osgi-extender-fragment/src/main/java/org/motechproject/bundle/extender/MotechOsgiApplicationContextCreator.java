package org.motechproject.bundle.extender;

import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.eclipse.gemini.blueprint.extender.support.scanning.ConfigurationScanner;
import org.eclipse.gemini.blueprint.extender.support.scanning.DefaultConfigurationScanner;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * This is the class responsible for creating Spring application contexts for Spring enabled bundles.
 * Scans bundles and creates a {@link org.motechproject.bundle.extender.MotechOsgiConfigurableApplicationContext}
 * for Spring enabled bundles. In most cases such bundles have their Spring configuration in their META-INF/spring directory.
 * The context created is then managed by the Blueprint extender.
 */
public class MotechOsgiApplicationContextCreator implements OsgiApplicationContextCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechOsgiApplicationContextCreator.class);

    private ConfigurationScanner configurationScanner = new DefaultConfigurationScanner();

    public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext(BundleContext bundleContext) {
        if (null == bundleContext) {
            return null;
        }

        Bundle bundle = bundleContext.getBundle();
        ApplicationContextConfiguration config = new ApplicationContextConfiguration(bundle, configurationScanner);

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Created configuration {} for bundle {}", config,
                    OsgiStringUtils.nullSafeNameAndSymName(bundle));
        }

        if (!config.isSpringPoweredBundle()) {
            return null;
        }

        LOGGER.info("Discovered configurations {} in bundle [{}]",
                ObjectUtils.nullSafeToString(config.getConfigurationLocations()),
                OsgiStringUtils.nullSafeNameAndSymName(bundle));


        DelegatedExecutionOsgiBundleApplicationContext motechOsgiConfigurableApplicationContext =
                new MotechOsgiConfigurableApplicationContext(config.getConfigurationLocations());

        motechOsgiConfigurableApplicationContext.setBundleContext(bundleContext);
        motechOsgiConfigurableApplicationContext.setPublishContextAsService(config.isPublishContextAsService());

        LOGGER.info("Created application context for " + bundle.getSymbolicName());

        return motechOsgiConfigurableApplicationContext;
    }

    /**
     * @param configurationScanner the configuration scanner used for scanning bundles
     */
    public void setConfigurationScanner(ConfigurationScanner configurationScanner) {
        Assert.notNull(configurationScanner);
        this.configurationScanner = configurationScanner;
    }
}
