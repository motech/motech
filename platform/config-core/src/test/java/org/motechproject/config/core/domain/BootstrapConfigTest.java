package org.motechproject.config.core.domain;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.motechproject.config.core.MotechConfigurationException;

import static org.junit.Assert.assertThat;

public class BootstrapConfigTest {
    private static final String sqlUrl = "jdbc:mysql://www.mydb.com:3306/";
    private static final String sqlDriver = "com.mysql.jdbc.Driver";
    private final String queueUrl = "tcp://localhost:61616";

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfDbConfigIsNull() {
        new BootstrapConfig(null, "tenantId", ConfigSource.FILE, null, null);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfQueueUrlIsInvalid() {
        new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), null, ConfigSource.FILE, null, "invalid.url");
    }

    @Test
    public void shouldUseDefaultIfTenantIdIsNull() {
        BootstrapConfig config = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), null, ConfigSource.FILE, null, queueUrl);
        assertThat(config.getTenantId(), IsEqual.equalTo("DEFAULT"));
    }

    @Test
    public void shouldUseDefaultIfTenantIdIsBlank() {
        BootstrapConfig config = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), " ", ConfigSource.FILE, null, queueUrl);
        assertThat(config.getTenantId(), IsEqual.equalTo("DEFAULT"));
    }

    @Test
    public void shouldUseDefaultIfConfigSourceIsNull() {
        BootstrapConfig config = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), "tenantId", null, null, queueUrl);
        assertThat(config.getConfigSource(), IsEqual.equalTo(ConfigSource.UI));
    }
}
