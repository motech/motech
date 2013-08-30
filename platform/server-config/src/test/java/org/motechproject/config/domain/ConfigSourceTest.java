package org.motechproject.config.domain;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.motechproject.config.MotechConfigurationException;

import static org.junit.Assert.assertThat;

public class ConfigSourceTest {
    @Test
    public void shouldReturnConfigSourceAsUIByDefault() throws Exception {
        assertThat(ConfigSource.valueOf(null), IsEqual.equalTo(ConfigSource.UI));
    }

    @Test
    public void shouldReturnConfigSourceUI() throws Exception {
        assertThat(ConfigSource.valueOf("UI"), IsEqual.equalTo(ConfigSource.UI));
    }

    @Test
    public void shouldReturnConfigSourceFILE() throws Exception {
        assertThat(ConfigSource.valueOf("FILE"), IsEqual.equalTo(ConfigSource.FILE));
    }

    @Test
    public void shouldIgnoreCaseForConfigSourceFILE() throws Exception {
        assertThat(ConfigSource.valueOf("fIlE"), IsEqual.equalTo(ConfigSource.FILE));
    }

    @Test
    public void shouldIgnoreCaseForConfigSourceUI() throws Exception {
        assertThat(ConfigSource.valueOf("uI"), IsEqual.equalTo(ConfigSource.UI));
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionForInvalidConfigSource() throws Exception {
        ConfigSource.valueOf("junk");
    }
}
