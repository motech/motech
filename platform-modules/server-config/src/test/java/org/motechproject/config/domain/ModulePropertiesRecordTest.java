package org.motechproject.config.domain;

import org.junit.Test;
import org.motechproject.testing.utils.FileHelper;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class ModulePropertiesRecordTest {

    @Test
    public void shouldBuildModulePropertiesRecordForPropertyFile() throws URISyntaxException {
        Properties expected = new Properties();
        expected.setProperty("apikey", "qwerty");
        expected.setProperty("host", "1.2.3.4");
        expected.setProperty("port", "9001");

        final File file = FileHelper.getResourceFile("config/org.motechproject.motech-module1/somemodule.properties");

        ModulePropertiesRecord actual = ModulePropertiesRecord.buildFrom(file);

        assertEquals(expected, actual.getProperties());
        assertEquals("org.motechproject.motech-module1", actual.getBundle());
        assertEquals("somemodule.properties", actual.getFilename());
        assertEquals(false, actual.isRaw());
    }

    @Test
    public void shouldBuildModulePropertiesRecordForRawFile() throws URISyntaxException {
        Properties expected = new Properties();
        expected.setProperty("rawData", "{\n" +
                "    apikey:\"qwerty\",\n" +
                "    host:\"1.2.3.4\",\n" +
                "    port:\"9001\"\n" +
                "}");

        final File file = FileHelper.getResourceFile("config/org.motechproject.motech-module2/raw/somemodule.json");

        ModulePropertiesRecord actual = ModulePropertiesRecord.buildFrom(file);

        for (Map.Entry<String, Object> entry : actual.getProperties().entrySet()) {
            // compare like this so that it passes on Windows
            assertEquals(expected.getProperty(entry.getKey()), entry.getValue().toString().replace("\r\n", "\n"));
        }

        assertEquals("org.motechproject.motech-module2", actual.getBundle());
        assertEquals("somemodule.json", actual.getFilename());
        assertEquals(true, actual.isRaw());
    }

    @Test
    public void shouldReturnNullIfErrorInReadingFile() {
        assertNull(ModulePropertiesRecord.buildFrom(new File("someRandomFile")));
    }

    @Test
    public void shouldCheckIfGivenRecordIsSameBasedOnKeyFields() {
        Properties existingProperties = new Properties();
        existingProperties.setProperty("existingKey", "existingValue");
        Properties updatedProperties = new Properties();
        updatedProperties.setProperty("updatedKey", "updatedValue");
        ModulePropertiesRecord existing = new ModulePropertiesRecord(existingProperties, "module", "1.0.0", "file1.properties", false);

        assertTrue(existing.sameAs(new ModulePropertiesRecord(updatedProperties, "module", "1.0.0", "file1.properties", false)));

        assertFalse(existing.sameAs(new ModulePropertiesRecord(existingProperties, "differentModule", "module", "file1.properties", false)));

        assertFalse(existing.sameAs(new ModulePropertiesRecord(existingProperties, "module", "1.0.0", "differentFile.properties", false)));

        assertFalse(existing.sameAs(new ModulePropertiesRecord(existingProperties, "module", "1.0.0", "file1.properties", true)));
    }
}
