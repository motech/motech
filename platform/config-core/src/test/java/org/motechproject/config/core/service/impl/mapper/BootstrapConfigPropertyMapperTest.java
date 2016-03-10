package org.motechproject.config.core.service.impl.mapper;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.SQLDBConfig;

import java.io.File;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.motechproject.config.core.domain.BootstrapConfig.*;

public class BootstrapConfigPropertyMapperTest {

    private String sqlUrl = "jdbc:mysql://localhost:3306/";
    private String sqlUsername = "root";
    private String sqlPassword = "password";
    private String felixPath = "./felix";
    private String motechDir = new File(System.getProperty("user.home"), ".motech").getAbsolutePath();
    private static final String sqlDriver = "com.mysql.jdbc.Driver";
    private String queueUrl = "tcp://localhost:61616";
    private ConfigSource configSource = ConfigSource.UI;

    @Test
    public void shouldMapToPropertiesFromBootstrapConfig() {
        Properties bootstrapProperties = BootstrapConfigPropertyMapper.toProperties(new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, sqlUsername, sqlPassword), configSource, felixPath, motechDir, queueUrl));

        Assert.assertThat(bootstrapProperties.getProperty(CONFIG_SOURCE), is(configSource.getName()));
    }

    @Test
    public void shouldMapToPropertiesFromBootstrapConfig_WhenUsernameAndPasswordAreBlank() {
        Properties bootstrapProperties = BootstrapConfigPropertyMapper.toProperties(new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, sqlUsername, sqlPassword), configSource, felixPath, motechDir, queueUrl));

        Assert.assertThat(bootstrapProperties.getProperty(sqlUsername), nullValue());
        Assert.assertThat(bootstrapProperties.getProperty(sqlPassword), nullValue());
        Assert.assertThat(bootstrapProperties.getProperty(CONFIG_SOURCE), is(configSource.getName()));
    }

    @Test
    public void shouldMapToBootstrapConfigFromProperties() {
        Properties bootstrapProperties = new Properties();
        bootstrapProperties.setProperty(SQL_URL, sqlUrl);
        bootstrapProperties.setProperty(SQL_DRIVER, sqlDriver);
        bootstrapProperties.setProperty(SQL_USER, sqlUsername);
        bootstrapProperties.setProperty(SQL_PASSWORD, sqlPassword);
        bootstrapProperties.setProperty(CONFIG_SOURCE, configSource.getName());
        bootstrapProperties.setProperty(QUEUE_URL, queueUrl);

        BootstrapConfig bootstrapConfig = BootstrapConfigPropertyMapper.fromProperties(bootstrapProperties);

        Assert.assertThat(bootstrapConfig.getSqlConfig().getUrl(), Matchers.is(sqlUrl));
        Assert.assertThat(bootstrapConfig.getSqlConfig().getUsername(), Matchers.is(sqlUsername));
        Assert.assertThat(bootstrapConfig.getSqlConfig().getPassword(), Matchers.is(sqlPassword));
        Assert.assertThat(bootstrapConfig.getConfigSource(), is(configSource));
        Assert.assertThat(bootstrapConfig.getQueueUrl(), is(queueUrl));
    }
}
