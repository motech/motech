package org.motechproject.config.core.filestore;

import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ConfigFileFilterTest {

    @Test
    public void shouldRejectNonPropertiesFilesAtCorrectParentDir() {
        File file = new File(getClass().getClassLoader().getResource("config/org.motechproject.file-filter-test/filefilter.conf").getFile());
        assertFalse(ConfigFileFilter.isFileSupported(file));
    }

    @Test
    public void shouldRejectBootstrapPropertiesAtRootConfigDir() {
        File file = new File(getClass().getClassLoader().getResource("config/bootstrap.properties").getFile());
        assertFalse(ConfigFileFilter.isFileSupported(file));
    }

    @Test
    public void shouldRejectPropertiesFileAtRootConfigDir() {
        File file = new File(getClass().getClassLoader().getResource("config/test.properties").getFile());
        assertFalse(ConfigFileFilter.isFileSupported(file));
    }

    @Test
    public void shouldAcceptPropertiesFileAtCorrectParentDir() {
        File file = new File(getClass().getClassLoader().getResource("config/org.motechproject.file-filter-test/filefilter.properties").getFile());
        assertTrue(ConfigFileFilter.isFileSupported(file));
    }

    @Test
    public void shouldAcceptPlatformCorePropertiesFileAtRootConfigDir() {
        File file = new File(getClass().getClassLoader().getResource("config/motech-settings.properties").getFile());
        assertTrue(ConfigFileFilter.isFileSupported(file));
    }
}
