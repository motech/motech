package org.motechproject.config.core.filestore;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.motechproject.config.core.domain.ConfigLocation;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigLocationFileStoreTest {
    @Test
    public void shouldAddConfigLocation() {
        PropertiesConfiguration propertiesConfiguration = mock(PropertiesConfiguration.class);
        ConfigLocationFileStore configLocationFileStore = new ConfigLocationFileStore(propertiesConfiguration);
        when(propertiesConfiguration.getStringArray("config.location")).thenReturn(new String[]{"value1", "value2"});

        configLocationFileStore.add("file:newlocation");

        verify(propertiesConfiguration).setProperty("config.location", "value1,value2,file:newlocation");
    }

    @Test
    public void shouldReturnOldConfigLocationIfThePathIsSame() {
        final ConfigLocation existingLocation = new ConfigLocation("existingLocation");
        existingLocation.markAsCurrentLocation();
        ArrayList<ConfigLocation> existingLocations = new ArrayList<ConfigLocation>() {{
            add(existingLocation);
        }};
        PropertiesConfiguration propertiesConfiguration = mock(PropertiesConfiguration.class);
        ConfigLocationFileStore configLocationFileStore = new ConfigLocationFileStore(propertiesConfiguration, existingLocations);

        when(propertiesConfiguration.getStringArray("config.location")).thenReturn(new String[]{"existingLocation", "value2"});

        Iterable<ConfigLocation> actualConfigLocations = configLocationFileStore.getAll();

        assertEquals(existingLocation, actualConfigLocations.iterator().next());
    }
}
