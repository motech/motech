package org.motechproject.config.filestore;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;

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
}
