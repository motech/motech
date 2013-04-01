package org.motechproject.bundle.extender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.eclipse.gemini.blueprint.extender.support.scanning.ConfigurationScanner;
import org.eclipse.gemini.blueprint.extender.support.scanning.DefaultConfigurationScanner;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class MotechOsgiApplicationContextCreator implements OsgiApplicationContextCreator {

    private static final Log LOG = LogFactory.getLog(MotechOsgiApplicationContextCreator.class);

    private ConfigurationScanner configurationScanner = new DefaultConfigurationScanner();


    public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext(BundleContext bundleContext) {
        Bundle bundle = bundleContext.getBundle();
        ApplicationContextConfiguration config = new ApplicationContextConfiguration(bundle, configurationScanner);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Created configuration " + config + " for bundle "
                    + OsgiStringUtils.nullSafeNameAndSymName(bundle));
        }

        if (!config.isSpringPoweredBundle()) {
            return null;
        }

        LOG.info("Discovered configurations " + ObjectUtils.nullSafeToString(config.getConfigurationLocations())
                + " in bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "]");

        DelegatedExecutionOsgiBundleApplicationContext motechOsgiConfigurableApplicationContext = new MotechOsgiConfigurableApplicationContext(
                config.getConfigurationLocations());
        motechOsgiConfigurableApplicationContext.setBundleContext(bundleContext);
        motechOsgiConfigurableApplicationContext.setPublishContextAsService(config.isPublishContextAsService());
        LOG.debug("Hurray! created application context for " + bundle.getSymbolicName());
        return motechOsgiConfigurableApplicationContext;
    }

    public void setConfigurationScanner(ConfigurationScanner configurationScanner) {
        Assert.notNull(configurationScanner);
        this.configurationScanner = configurationScanner;
    }

}
