package org.motechproject.config.core.filestore;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.exception.MotechConfigurationException;
import org.motechproject.config.core.domain.ConfigLocation;
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

    @Autowired
    public ConfigLocationFileStore(PropertiesConfiguration propertiesConfiguration) {
        this.propertiesConfiguration = propertiesConfiguration;
    }

    /**
     * Returns all the configuration locations stored by this object.
     *
     * @return the list of configuration locations
     */
    public Iterable<ConfigLocation> getAll() {
        List<String> configLocations = loadAll();

        return map(configLocations);
    }

    private Iterable<ConfigLocation> map(List<String> configPaths) {
        List<ConfigLocation> configLocations = new ArrayList<>();

        for (String configLocation : configPaths) {
            configLocations.add(new ConfigLocation(configLocation));
        }

        return configLocations;
    }

    private List<String> loadAll() {
        return asList(propertiesConfiguration.getStringArray(CONFIG_LOCATION_PROPERTY_KEY));
    }

    /**
     * Adds the given location to the store.
     *
     * @param location  the location to be stored
     */
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
            throw new MotechConfigurationException(errorMessage, e);
        }
    }
}
