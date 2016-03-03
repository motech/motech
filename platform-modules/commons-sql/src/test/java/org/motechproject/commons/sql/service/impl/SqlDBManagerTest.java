package org.motechproject.commons.sql.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.commons.sql.util.Drivers;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;

import java.io.IOException;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SqlDBManagerTest {

    @Mock
    private CoreConfigurationService coreConfigurationService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldProperlySetSqlProperties() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new SQLDBConfig("jdbc:mysql://localhost:3306/", "com.mysql.jdbc.Driver", "root", "pass"), ConfigSource.FILE, "./felix", "tcp://localhost:61616");
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        Properties propertiesToUpdate = new Properties();
        propertiesToUpdate.put("javax.jdo.option.ConnectionURL", "${sql.url}");
        propertiesToUpdate.put("some.username", "${sql.user}");
        propertiesToUpdate.put("some.password", "${sql.password}");
        propertiesToUpdate.put("quartz.delegate", "${sql.quartz.delegateClass}");

        SqlDBManager sqlDBManager = new SqlDBManagerImpl(coreConfigurationService);

        Properties propertiesAfterUpdate = sqlDBManager.getSqlProperties(propertiesToUpdate);
        assertEquals(4, propertiesAfterUpdate.size());
        assertEquals("jdbc:mysql://localhost:3306/", propertiesAfterUpdate.getProperty("javax.jdo.option.ConnectionURL"));
        assertEquals("root", propertiesAfterUpdate.getProperty("some.username"));
        assertEquals("pass", propertiesAfterUpdate.getProperty("some.password"));
        assertEquals(Drivers.QUARTZ_STD_JDBC_DELEGATE, propertiesAfterUpdate.getProperty("quartz.delegate"));
    }

    @Test
    public void shouldARemoveSlash() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new SQLDBConfig("jdbc:mysql://localhost:3306/", "com.mysql.jdbc.Driver", "root", "pass"), ConfigSource.FILE, "./felix", "tcp://localhost:61616");
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        Properties propertiesToUpdate = new Properties();
        propertiesToUpdate.put("javax.jdo.option.ConnectionURL", "${sql.url}dbname?useSSL=true");

        SqlDBManager sqlDBManager = new SqlDBManagerImpl(coreConfigurationService);
        Properties propertiesAfterUpdate = sqlDBManager.getSqlProperties(propertiesToUpdate);
        assertEquals("jdbc:mysql://localhost:3306/dbname?useSSL=true", propertiesAfterUpdate.getProperty("javax.jdo.option.ConnectionURL"));

        propertiesToUpdate = new Properties();
        propertiesToUpdate.put("javax.jdo.option.ConnectionURL", "${sql.url}/dbname?useSSL=true");

        propertiesAfterUpdate = sqlDBManager.getSqlProperties(propertiesToUpdate);
        assertEquals("jdbc:mysql://localhost:3306/dbname?useSSL=true", propertiesAfterUpdate.getProperty("javax.jdo.option.ConnectionURL"));
    }
}
