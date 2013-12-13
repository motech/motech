package org.motechproject.config.core.domain;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.motechproject.config.core.domain.FileListMatcher.doesNotHave;
import static org.motechproject.config.core.domain.FileListMatcher.has;

public class ConfigLocationIT {

    @Test
    public void shouldReturnConfigurationFiles() throws IOException {
        ConfigLocation configLocation = new ConfigLocation(getConfigDirectoryLocation("config/"));
        List<File> existingConfigFiles = configLocation.getExistingConfigFiles();
        assertThat(existingConfigFiles.isEmpty(), Is.is(false));
        assertThat(existingConfigFiles, has("motech-settings.properties"));
        assertThat(existingConfigFiles, doesNotHave("bootstrap.properties"));
    }

    @Test
    public void shouldReturnTrueWhenConfigLocationHasPlatformConfigurationFile() throws IOException {
        ConfigLocation configLocation = new ConfigLocation(getConfigDirectoryLocation("config/"));
        assertTrue(configLocation.hasPlatformConfigurationFile());
    }

    @Test
    public void shouldReturnFalseWhenConfigLocationHasPlatformConfigurationFile() throws IOException {
        ConfigLocation configLocation = new ConfigLocation(getConfigDirectoryLocation("some_random_dir/"));
        assertFalse(configLocation.hasPlatformConfigurationFile());
    }

    private String getConfigDirectoryLocation(String path) throws IOException {
        return new ClassPathResource(path).getFile().getPath();
    }

}
