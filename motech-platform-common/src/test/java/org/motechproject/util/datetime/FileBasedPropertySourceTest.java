package org.motechproject.util.datetime;

import org.junit.Assert;
import org.junit.Test;

public class FileBasedPropertySourceTest {
    @Test
    public void whenPropertiesFileIsNotFound() {
        FileBasePropertySource fileBasePropertySource = new FileBasePropertySource("nonexistentfile.properties");
        Assert.assertNull(fileBasePropertySource.getProperty("foo"));
    }
}
