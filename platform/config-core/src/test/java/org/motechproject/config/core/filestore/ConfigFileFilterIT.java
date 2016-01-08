package org.motechproject.config.core.filestore;

import org.apache.commons.io.FileUtils;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.config.core.filters.ConfigFileFilter;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;

public class ConfigFileFilterIT {

    @Test
    public void shouldRejectNonPropertiesFilesAtCorrectParentDir() {
        File file = getFile("config/org.motechproject.file-filter-test/filefilter.conf");
        assertFalse(ConfigFileFilter.isFileSupported(file));
    }

    @Test
    public void shouldRejectBootstrapPropertiesAtRootConfigDir() {
        File file = getFile("config/bootstrap.properties");
        assertFalse(ConfigFileFilter.isFileSupported(file));
    }

    @Test
    public void shouldRejectPropertiesFileAtRootConfigDir() {
        File file = getFile("config/test.properties");
        assertFalse(ConfigFileFilter.isFileSupported(file));
    }

    @Test
    public void shouldAcceptPropertiesFileAtCorrectParentDir() {
        File file = getFile("config/org.motechproject.file-filter-test/filefilter.properties");
        assertTrue(ConfigFileFilter.isFileSupported(file));
    }

    @Test
    public void shouldAcceptPlatformCorePropertiesFileAtRootConfigDir() {
        File file = getFile("config/motech-settings.properties");
        assertTrue(ConfigFileFilter.isFileSupported(file));
    }

    @Test
    public void shouldRetainOnlyPlatformConfigFile() throws IOException {
        List<File> files = (List<File>) FileUtils.listFiles(getConfigDirectory(), ConfigFileFilter.PLATFORM_CORE_CONFIG_FILTER, null);
        assertThat(files.size(), Is.is(1));
        assertThat(files.get(0).getName(), Is.is("motech-settings.properties"));
    }

    private File getFile(String fileName) {
        return new File(getClass().getClassLoader().getResource(fileName).getFile());
    }

    private File getConfigDirectory() throws IOException {
        return new ClassPathResource("config/").getFile();
    }
}
