package org.motechproject.config.core.filestore;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.domain.ConfigLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * <p>Used to read default platform config location(s) from <code>config-location.properties</code> and also to save in the file in the default location.</p>
 * <p><code>config-location.properties</code> file will be loaded according to the behaviour of {@link org.apache.commons.configuration.PropertiesConfiguration}
 * as specified <a href="http://commons.apache.org/proper/commons-configuration/userguide/howto_filebased.html#Specifying_the_file">here</a>.</p>
 */
@Component
public class ConfigLocationFileStore {

    private PropertiesConfiguration propertiesConfiguration;
    public static final String CONFIG_LOCATION_PROPERTY_KEY = "config.location";
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLocationFileStore.class);

    private List<ConfigLocation> configLocations = new ArrayList<>();

    @Autowired
    public ConfigLocationFileStore(PropertiesConfiguration propertiesConfiguration) {
        this.propertiesConfiguration = propertiesConfiguration;
    }

    ConfigLocationFileStore(PropertiesConfiguration propertiesConfiguration, List<ConfigLocation> configLocations) {
        this.propertiesConfiguration = propertiesConfiguration;
        this.configLocations = configLocations;
    }

    public Iterable<ConfigLocation> getAll() {
        List<String> configPaths = loadAll();
        for (String configLocation : configPaths) {
            if (!exists(configLocation)) {
                configLocations.add(new ConfigLocation(configLocation));
            }
        }

        return configLocations;
    }

    private boolean exists(String newLocation) {
        for (ConfigLocation existingLocation : configLocations) {
            if (existingLocation.getLocation().equals(newLocation)) {
                return true;
            }
        }
        return false;
    }

    private List<String> loadAll() {
        return asList(propertiesConfiguration.getStringArray(CONFIG_LOCATION_PROPERTY_KEY));
    }

    public void add(String location) {
        List<String> configLocations = new ArrayList<>(loadAll());
        configLocations.add(location);
        save(configLocations);
    }

    private void save(List<String> configLocations) {
        try {
            propertiesConfiguration.setProperty(CONFIG_LOCATION_PROPERTY_KEY, StringUtils.join(configLocations, ","));
            propertiesConfiguration.save();
        } catch (ConfigurationException e) {
            String errorMessage = String.format("Could not save %s in this location %s.", propertiesConfiguration.getFileName(), propertiesConfiguration.getBasePath());
            LOGGER.error(errorMessage);
            throw new MotechConfigurationException(errorMessage, e);
        }
    }
}
