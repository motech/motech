package org.motechproject.config.bootstrap.mapper;

import org.junit.Test;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;

import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.config.domain.BootstrapConfig.CONFIG_SOURCE;
import static org.motechproject.config.domain.BootstrapConfig.DB_PASSWORD;
import static org.motechproject.config.domain.BootstrapConfig.DB_URL;
import static org.motechproject.config.domain.BootstrapConfig.DB_USERNAME;
import static org.motechproject.config.domain.BootstrapConfig.TENANT_ID;

public class BootstrapConfigPropertyMapperTest {

    private String url = "http://url";
    private String username = "username";
    private String password = "password";
    private String tenantId = "tenantId";
    private ConfigSource configSource = ConfigSource.UI;

    @Test
    public void shouldMapToPropertiesFromBootstrapConfig() {
        Properties bootstrapProperties = BootstrapConfigPropertyMapper.toProperties(new BootstrapConfig(new DBConfig(url, username, password), tenantId, configSource));

        assertThat(bootstrapProperties.getProperty(DB_URL), is(url));
        assertThat(bootstrapProperties.getProperty(DB_USERNAME), is(username));
        assertThat(bootstrapProperties.getProperty(DB_PASSWORD), is(password));
        assertThat(bootstrapProperties.getProperty(TENANT_ID), is(tenantId));
        assertThat(bootstrapProperties.getProperty(CONFIG_SOURCE), is(configSource.getName()));
    }

    @Test
    public void shouldMapToBootstrapConfigFromProperties() {
        Properties bootstrapProperties = new Properties();
        bootstrapProperties.setProperty(DB_URL, url);
        bootstrapProperties.setProperty(DB_USERNAME, username);
        bootstrapProperties.setProperty(DB_PASSWORD, password);
        bootstrapProperties.setProperty(TENANT_ID, tenantId);
        bootstrapProperties.setProperty(CONFIG_SOURCE, configSource.getName());

        BootstrapConfig bootstrapConfig = BootstrapConfigPropertyMapper.fromProperties(bootstrapProperties);

        assertThat(bootstrapConfig.getDbConfig().getUrl(), is(url));
        assertThat(bootstrapConfig.getDbConfig().getUsername(), is(username));
        assertThat(bootstrapConfig.getDbConfig().getPassword(), is(password));
        assertThat(bootstrapConfig.getTenantId(), is(tenantId));
        assertThat(bootstrapConfig.getConfigSource(), is(configSource));
    }
}
