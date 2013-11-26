package org.motechproject.config.core.service.impl.mapper;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;

import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.motechproject.config.core.domain.BootstrapConfig.CONFIG_SOURCE;
import static org.motechproject.config.core.domain.BootstrapConfig.DB_PASSWORD;
import static org.motechproject.config.core.domain.BootstrapConfig.DB_URL;
import static org.motechproject.config.core.domain.BootstrapConfig.DB_USERNAME;
import static org.motechproject.config.core.domain.BootstrapConfig.TENANT_ID;

public class BootstrapConfigPropertyMapperTest {

    private String url = "http://url";
    private String username = "username";
    private String password = "password";
    private String tenantId = "tenantId";
    private ConfigSource configSource = ConfigSource.UI;

    @Test
    public void shouldMapToPropertiesFromBootstrapConfig() {
        Properties bootstrapProperties = BootstrapConfigPropertyMapper.toProperties(new BootstrapConfig(new DBConfig(url, username, password), tenantId, configSource));

        Assert.assertThat(bootstrapProperties.getProperty(DB_URL), Matchers.is(url));
        Assert.assertThat(bootstrapProperties.getProperty(DB_USERNAME), Matchers.is(username));
        Assert.assertThat(bootstrapProperties.getProperty(DB_PASSWORD), Matchers.is(password));
        Assert.assertThat(bootstrapProperties.getProperty(TENANT_ID), Matchers.is(tenantId));
        Assert.assertThat(bootstrapProperties.getProperty(CONFIG_SOURCE), is(configSource.getName()));
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

        Assert.assertThat(bootstrapConfig.getDbConfig().getUrl(), Matchers.is(url));
        Assert.assertThat(bootstrapConfig.getDbConfig().getUsername(), Matchers.is(username));
        Assert.assertThat(bootstrapConfig.getDbConfig().getPassword(), Matchers.is(password));
        Assert.assertThat(bootstrapConfig.getTenantId(), Matchers.is(tenantId));
        Assert.assertThat(bootstrapConfig.getConfigSource(), is(configSource));
    }
}
