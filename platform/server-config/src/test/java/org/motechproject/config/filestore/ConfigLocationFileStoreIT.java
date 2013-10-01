package org.motechproject.config.filestore;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.domain.ConfigLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Iterator;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class ConfigLocationFileStoreIT {

    @Autowired
    private ConfigLocationFileStore configLocationFileStore;

    @Test
    public void shouldReadConfigLocations() {
        Iterable<ConfigLocation> configLocationsIterable = configLocationFileStore.getAll();

        assertNotNull(configLocationsIterable);
        assertTrue(configLocationsIterable.iterator().hasNext());
    }

    @Test
    public void shouldSubstituteHomeDirectoryInThePath() throws ConfigurationException, IOException {
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(getClass().getClassLoader().getResource("config-locations.properties"));
        ConfigLocationFileStore configLocationFileStore = new ConfigLocationFileStore(propertiesConfiguration);

        Iterable<ConfigLocation> configLocationsIterable = configLocationFileStore.getAll();

        assertNotNull(configLocationsIterable);
        Iterator<ConfigLocation> configLocationIterator = configLocationsIterable.iterator();
        assertTrue(configLocationIterator.hasNext());
        ConfigLocation expected = new ConfigLocation(String.format("%s/.motech/configtest/", System.getProperty("user.home")));
        assertThat(configLocationIterator.next(), IsEqual.equalTo(expected));
    }
}