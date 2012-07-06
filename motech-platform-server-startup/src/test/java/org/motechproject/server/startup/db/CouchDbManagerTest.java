package org.motechproject.server.startup.db;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CouchDbManagerTest {

    @Mock
    CouchDbInstance couchDbInstance;

    @Mock
    CouchDbConnector couchDbConnector;

    @Mock
    HttpClientFactoryBean httpClientFactoryBean;

    @InjectMocks
    CouchDbManager couchDbManager = new CouchDbManager();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testConnectorRetrieval() throws DbConnectionException {
        when(couchDbInstance.createConnector("test", true)).thenReturn(couchDbConnector);
        couchDbManager.setCouchDbInstance(couchDbInstance);

        CouchDbConnector connector1 = couchDbManager.getConnector("test", true);
        assertNotNull(connector1);

        CouchDbConnector connector2 = couchDbManager.getConnector("test", true);
        assertEquals(connector1, connector2);
    }

    @Test(expected = DbConnectionException.class)
    public void testDbException() throws Exception {
        doThrow(new Exception()).when(httpClientFactoryBean).afterPropertiesSet();

        couchDbManager.configureDb(new Properties()); // assert Db exception thrown
    }
}
