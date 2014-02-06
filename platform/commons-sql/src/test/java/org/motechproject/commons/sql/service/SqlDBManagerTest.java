package org.motechproject.commons.sql.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.sql.service.impl.SqlDBManagerImpl;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;

import java.io.IOException;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SqlDBManagerTest {

    @InjectMocks
    private SqlDBManager sqlDBManager = new SqlDBManagerImpl();

    @Mock
    private CoreConfigurationService coreConfigurationService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldProperlySetSqlProperties() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://localhost:5984/", "", ""), new SQLDBConfig("jdbc:mysql://localhost:3306/", "root", "pass"), "tenant", ConfigSource.FILE);
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        Properties propertiesToUpdate = new Properties();
        propertiesToUpdate.put("some.url", "${sql.url}");
        propertiesToUpdate.put("some.username", "${sql.user}");
        propertiesToUpdate.put("some.password", "${sql.password}");

        sqlDBManager.updateSqlProperties();

        Properties propertiesAfterUpdate = sqlDBManager.getSqlProperties(propertiesToUpdate);
        assertEquals(3, propertiesAfterUpdate.size());
        assertEquals("jdbc:mysql://localhost:3306/", propertiesAfterUpdate.getProperty("some.url"));
        assertEquals("root", propertiesAfterUpdate.getProperty("some.username"));
        assertEquals("pass", propertiesAfterUpdate.getProperty("some.password"));
    }
}
