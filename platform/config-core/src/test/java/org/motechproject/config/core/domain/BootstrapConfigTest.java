package org.motechproject.config.core.domain;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.motechproject.config.core.MotechConfigurationException;


import java.io.File;

import static org.junit.Assert.assertThat;

public class BootstrapConfigTest {
    private static final String sqlUrl = "jdbc:mysql://www.mydb.com:3306/";
    private static final String sqlDriver = "com.mysql.jdbc.Driver";
    private final String queueUrl = "tcp://localhost:61616";

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfDbConfigIsNull() {
        new BootstrapConfig(null, ConfigSource.FILE, null, null, null);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfQueueUrlIsInvalid() {
        new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), ConfigSource.FILE, null,null, "invalid.url");
    }

    @Test
    public void shouldUseDefaultIfConfigSourceIsNull() {
        BootstrapConfig config = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), null, null, null, queueUrl);
        assertThat(config.getConfigSource(), IsEqual.equalTo(ConfigSource.UI));
    }

    @Test
    public void shouldUseDefaultIfOsgiFrameworkStorageIsNull() {
        BootstrapConfig config = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), null, null, null, queueUrl);
        assertThat(config.getOsgiFrameworkStorage(), IsEqual.equalTo(new File(System.getProperty("user.home"), ".motech"+File.separator+"felix-cache").getAbsolutePath()));
    }

}
