package org.motechproject.config.domain;

import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class ModulePropertiesRecordTest {

    @Test
    public void shouldBuildModulePropertiesRecordForPropertyFile() {
        Properties expected = new Properties();
        expected.setProperty("apikey", "qwerty");
        expected.setProperty("host", "1.2.3.4");
        expected.setProperty("port", "9001");
        String filePath = this.getClass().getClassLoader().getResource("config/org.motechproject.motech-module1/somemodule.properties").getFile();

        ModulePropertiesRecord actual = ModulePropertiesRecord.build(new File(filePath));

        assertEquals(expected, actual.getProperties());
        assertEquals("org.motechproject.motech-module1", actual.getModule());
        assertEquals("somemodule.properties", actual.getFilename());
        assertEquals(false, actual.isRaw());
    }

    @Test
    public void shouldBuildModulePropertiesRecordForRawFile() {
        Properties expected = new Properties();
        expected.setProperty("rawData", "{\n" +
                "    apikey:\"qwerty\",\n" +
                "    host:\"1.2.3.4\",\n" +
                "    port:\"9001\"\n" +
                "}");
        String filePath = this.getClass().getClassLoader().getResource("config/org.motechproject.motech-module2/somemodule.json").getFile();

        ModulePropertiesRecord actual = ModulePropertiesRecord.build(new File(filePath));

        assertEquals(expected, actual.getProperties());
        assertEquals("org.motechproject.motech-module2", actual.getModule());
        assertEquals("somemodule.json", actual.getFilename());
        assertEquals(true, actual.isRaw());
    }

    @Test
    public void shouldReturnNullIfErrorInReadingFile() {
        assertNull(ModulePropertiesRecord.build(new File("someRandomFile")));
    }

    @Test
    public void shouldCheckIfGivenRecordIsSameBasedOnKeyFields() {
        Properties existingProperties = new Properties();
        existingProperties.setProperty("existingKey", "existingValue");
        Properties updatedProperties = new Properties();
        updatedProperties.setProperty("updatedKey", "updatedValue");
        ModulePropertiesRecord existing = new ModulePropertiesRecord(existingProperties, "module", "file1.properties", false);

        assertTrue(existing.sameAs(new ModulePropertiesRecord(updatedProperties, "module", "file1.properties", false)));

        assertFalse(existing.sameAs(new ModulePropertiesRecord(existingProperties, "differentModule", "file1.properties", false)));

        assertFalse(existing.sameAs(new ModulePropertiesRecord(existingProperties, "module", "differentFile.properties", false)));

        assertFalse(existing.sameAs(new ModulePropertiesRecord(existingProperties, "module", "file1.properties", true)));
    }
}
