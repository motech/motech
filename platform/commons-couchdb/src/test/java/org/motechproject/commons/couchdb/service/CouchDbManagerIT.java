package org.motechproject.commons.couchdb.service;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.couchdb.service.impl.CouchDbManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class CouchDbManagerIT {

    @Autowired
    CouchDbManagerImpl couchDbManager;

    @Test
    public void testConnectorRetrieval() throws DbConnectionException {
        CouchDbConnector dbConnector = couchDbManager.getConnector("foo");
        assertEquals(System.getProperty("user.name") + "_"+"foo", dbConnector.getDatabaseName());
    }
}
