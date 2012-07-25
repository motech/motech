package org.motechproject.openmrs.atomfeed.osgi;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.osgi.context.BundleContextAware;

public class PropertiesFactoryBean implements FactoryBean<Properties>, BundleContextAware {
    private static final Logger LOGGER = Logger.getLogger(PropertiesFactoryBean.class);

    private static final String MOTECH_ATOMFEED_PROPERTIES = "motech-atomfeed";
    private final PlatformSettingsService platformSettingsService;
    private BundleContext bundleContext;
    private Properties props;

    @Autowired
    public PropertiesFactoryBean(PlatformSettingsService platformSettingsService) {
        this.platformSettingsService = platformSettingsService;
    }

    @Override
    public Properties getObject() throws Exception {
        if (props == null) {
            props = platformSettingsService.getBundleProperties(bundleContext.getBundle().getSymbolicName(),
                    MOTECH_ATOMFEED_PROPERTIES);

            if (props == null) {
                // used built in properties file defaults
                LOGGER.warn("Did not configuration file for Motech OpenMRS Atom Feed module.");
                LOGGER.warn("Polling will be disabled by default");
                LOGGER.warn("Using http://localhost:8080/openmrs as default OpenMRS Url");

                props = new Properties();
                ClassPathResource resource = new ClassPathResource("polling-config.properties");
                props.load(resource.getInputStream());
                resource.getInputStream().close();
            }
        }

        return props;
    }

    @Override
    public Class<?> getObjectType() {
        return Properties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
