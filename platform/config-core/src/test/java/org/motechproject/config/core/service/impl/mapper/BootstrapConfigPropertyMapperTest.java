package org.motechproject.config.core.service.impl.mapper;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.domain.SQLDBConfig;

import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.motechproject.config.core.domain.BootstrapConfig.CONFIG_SOURCE;
import static org.motechproject.config.core.domain.BootstrapConfig.COUCHDB_URL;
import static org.motechproject.config.core.domain.BootstrapConfig.COUCHDB_USERNAME;
import static org.motechproject.config.core.domain.BootstrapConfig.COUCHDB_PASSWORD;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_URL;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_USER;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_PASSWORD;
import static org.motechproject.config.core.domain.BootstrapConfig.TENANT_ID;

public class BootstrapConfigPropertyMapperTest {

    private String couchUrl = "http://url";
    private String couchUsername = "username";
    private String couchPassword = "password";
    private String tenantId = "tenantId";
    private String sqlUrl = "jdbc:mysql://localhost:3306/";
    private String sqlUsername = "root";
    private String sqlPassword = "password";
    private ConfigSource configSource = ConfigSource.UI;

    @Test
    public void shouldMapToPropertiesFromBootstrapConfig() {
        Properties bootstrapProperties = BootstrapConfigPropertyMapper.toProperties(new BootstrapConfig(new DBConfig(couchUrl, couchUsername, couchPassword), new SQLDBConfig(sqlUrl, sqlUsername, sqlPassword), tenantId, configSource));

        Assert.assertThat(bootstrapProperties.getProperty(COUCHDB_URL), Matchers.is(couchUrl));
        Assert.assertThat(bootstrapProperties.getProperty(COUCHDB_USERNAME), Matchers.is(couchUsername));
        Assert.assertThat(bootstrapProperties.getProperty(COUCHDB_PASSWORD), Matchers.is(couchPassword));
        Assert.assertThat(bootstrapProperties.getProperty(TENANT_ID), Matchers.is(tenantId));
        Assert.assertThat(bootstrapProperties.getProperty(CONFIG_SOURCE), is(configSource.getName()));
    }

    @Test
    public void shouldMapToPropertiesFromBootstrapConfig_WhenUsernameAndPasswordAreBlank() {
        Properties bootstrapProperties = BootstrapConfigPropertyMapper.toProperties(new BootstrapConfig(new DBConfig(couchUrl, null, "  "), new SQLDBConfig(sqlUrl, sqlUsername, sqlPassword), tenantId, configSource));

        Assert.assertThat(bootstrapProperties.getProperty(COUCHDB_URL), is(couchUrl));
        Assert.assertThat(bootstrapProperties.getProperty(couchUsername), nullValue());
        Assert.assertThat(bootstrapProperties.getProperty(couchPassword), nullValue());
        Assert.assertThat(bootstrapProperties.getProperty(TENANT_ID), is(tenantId));
        Assert.assertThat(bootstrapProperties.getProperty(CONFIG_SOURCE), is(configSource.getName()));
    }

    @Test
    public void shouldMapToBootstrapConfigFromProperties() {
        Properties bootstrapProperties = new Properties();
        bootstrapProperties.setProperty(COUCHDB_URL, couchUrl);
        bootstrapProperties.setProperty(COUCHDB_USERNAME, couchUsername);
        bootstrapProperties.setProperty(COUCHDB_PASSWORD, couchPassword);
        bootstrapProperties.setProperty(SQL_URL, sqlUrl);
        bootstrapProperties.setProperty(SQL_USER, sqlUsername);
        bootstrapProperties.setProperty(SQL_PASSWORD, sqlPassword);
        bootstrapProperties.setProperty(TENANT_ID, tenantId);
        bootstrapProperties.setProperty(CONFIG_SOURCE, configSource.getName());

        BootstrapConfig bootstrapConfig = BootstrapConfigPropertyMapper.fromProperties(bootstrapProperties);

        Assert.assertThat(bootstrapConfig.getCouchDbConfig().getUrl(), Matchers.is(couchUrl));
        Assert.assertThat(bootstrapConfig.getCouchDbConfig().getUsername(), Matchers.is(couchUsername));
        Assert.assertThat(bootstrapConfig.getCouchDbConfig().getPassword(), Matchers.is(couchPassword));
        Assert.assertThat(bootstrapConfig.getSqlConfig().getUrl(), Matchers.is(sqlUrl));
        Assert.assertThat(bootstrapConfig.getSqlConfig().getUsername(), Matchers.is(sqlUsername));
        Assert.assertThat(bootstrapConfig.getSqlConfig().getPassword(), Matchers.is(sqlPassword));
        Assert.assertThat(bootstrapConfig.getTenantId(), Matchers.is(tenantId));
        Assert.assertThat(bootstrapConfig.getConfigSource(), is(configSource));
    }
}
