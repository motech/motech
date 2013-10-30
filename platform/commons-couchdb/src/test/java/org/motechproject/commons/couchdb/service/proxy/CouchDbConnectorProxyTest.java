package org.motechproject.commons.couchdb.service.proxy;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.commons.couchdb.service.impl.CouchDbManagerImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class CouchDbConnectorProxyTest {

    @Test
    public void shouldUseCouchdbConnectorProxy() {
        final CouchDbManagerImpl couchDbManagerMock = Mockito.mock(CouchDbManagerImpl.class);
        final String dbName = "foo";
        final CouchDbConnector couchDbTargetConnectorMock = Mockito.mock(CouchDbConnector.class);
        when(couchDbManagerMock.getTargetConnector(dbName)).thenReturn(couchDbTargetConnectorMock);
        when(couchDbTargetConnectorMock.toString()).thenReturn("Hello");

        final CouchDbConnector couchDbConnector = CouchDbConnectorProxy.newInstance(couchDbManagerMock, dbName);

        assertNotNull(couchDbConnector);
        assertEquals("Hello", couchDbConnector.toString());
    }
}
