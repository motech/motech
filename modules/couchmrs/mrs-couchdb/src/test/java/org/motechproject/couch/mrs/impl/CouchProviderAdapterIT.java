package org.motechproject.couch.mrs.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.motechproject.couch.mrs.repository.impl.AllCouchProvidersImpl;
import org.motechproject.mrs.domain.Provider;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchProviderAdapterIT extends SpringIntegrationTest {

    @Autowired
    private CouchProviderAdapter providerAdapter;

    @Autowired
    private AllCouchProviders allProviders;

    private Initializer init;

    @Autowired
    @Qualifier("couchProviderDatabaseConnector")
    CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
    }

    @Test
    public void shouldSaveAProviderAndRetrieveByProviderId() throws MRSCouchException {
        CouchPerson person = init.initializePerson1();

        CouchProvider provider = new CouchProvider("providerId", person);

        providerAdapter.saveProvider(provider);

        Provider retrievedProvider = providerAdapter.getProviderByProviderId("providerId");

        assertEquals(retrievedProvider.getProviderId(), "providerId");
        assertNotNull(retrievedProvider.getPerson());
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchProvidersImpl) allProviders).removeAll();
    }
}
