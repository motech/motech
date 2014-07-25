package org.motechproject.config.core.domain;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.motechproject.config.core.MotechConfigurationException;

import static org.junit.Assert.assertThat;

public class BootstrapConfigTest {
    private static final String sqlUrl = "jdbc:mysql://www.mydb.com:3306/";
    private static final String sqlDriver = "com.mysql.jdbc.Driver";

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfDbConfigIsNull() {
        new BootstrapConfig(null, "tenantId", ConfigSource.FILE);
    }

    @Test
    public void shouldUseDefaultIfTenantIdIsNull() {
        BootstrapConfig config = new BootstrapConfig( new SQLDBConfig(sqlUrl, sqlDriver, null, null), null, ConfigSource.FILE);
        assertThat(config.getTenantId(), IsEqual.equalTo("DEFAULT"));
    }

    @Test
    public void shouldUseDefaultIfTenantIdIsBlank() {
        BootstrapConfig config = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), " ", ConfigSource.FILE);
        assertThat(config.getTenantId(), IsEqual.equalTo("DEFAULT"));
    }

    @Test
    public void shouldUseDefaultIfConfigSourceIsNull() {
        BootstrapConfig config = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), "tenantId", null);
        assertThat(config.getConfigSource(), IsEqual.equalTo(ConfigSource.UI));
    }
}
