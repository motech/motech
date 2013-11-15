package org.motechproject.config.domain;

import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ModulePropertiesRecordTest {

    @Test
    public void shouldBuildModulePropertiesRecordForPropertyFile() {
        Properties expected = new Properties();
        expected.setProperty("apikey", "qwerty");
        expected.setProperty("host", "1.2.3.4");
        expected.setProperty("port", "9001");
        String filePath = this.getClass().getClassLoader().getResource("config/somemodule.properties").getFile();

        ModulePropertiesRecord actual = ModulePropertiesRecord.build(new File(filePath));

        assertEquals(expected, actual.getProperties());
        assertEquals("config", actual.getModule());
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
        String filePath = this.getClass().getClassLoader().getResource("config/somemodule.json").getFile();

        ModulePropertiesRecord actual = ModulePropertiesRecord.build(new File(filePath));

        assertEquals(expected, actual.getProperties());
        assertEquals("config", actual.getModule());
        assertEquals("somemodule.json", actual.getFilename());
        assertEquals(true, actual.isRaw());
    }

    @Test
    public void shouldReturnNullIfErrorInReadingFile() {
        assertNull(ModulePropertiesRecord.build(new File("someRandomFile")));
    }
}
