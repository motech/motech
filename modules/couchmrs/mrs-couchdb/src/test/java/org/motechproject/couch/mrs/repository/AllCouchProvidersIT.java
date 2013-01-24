package org.motechproject.couch.mrs.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Collections;
import java.util.List;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.impl.AllCouchProvidersImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class AllCouchProvidersIT extends SpringIntegrationTest {

    @Autowired
    private AllCouchProviders allCouchProviders;

    private Initializer init;

    @Autowired
    @Qualifier("couchProviderDatabaseConnector")
    private CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
    }

    @Test
    public void shouldSaveAProviderAndRetrieveByExternalId() throws MRSCouchException {
        CouchPerson person1 = init.initializePerson1();
        CouchProvider provider = new CouchProvider("Provider1", person1);
        allCouchProviders.addProvider(provider);
        List<CouchProvider> providersRetrieved = allCouchProviders.findByProviderId("Provider1");
        CouchProvider providerCompare = providersRetrieved.get(0);
        assertTrue(providerCompare.getProviderId().equals(provider.getProviderId()));
        assertNotNull(providerCompare.getPerson());
    }

    @Test
    public void shouldUpdateProviderRecord() throws MRSCouchException {
        CouchPerson person1 = init.initializePerson1();
        CouchProvider provider = new CouchProvider("Provider1", person1);
        allCouchProviders.addProvider(provider);
        CouchPerson person2 = init.initializeSecondPerson();
        CouchProvider provider2  = new CouchProvider("Provider1", person2);
        allCouchProviders.addProvider(provider2);

        List<CouchProvider> providersRetrieved = allCouchProviders.findByProviderId("Provider1");
        assertTrue(providersRetrieved.size() == 1);
        assertEquals(providersRetrieved.get(0).getPerson().getFirstName(), "AName");
    }

    @Test
    public void findByProviderIdShouldReturnEmptyListIfExternalIdIsNull() {
        assertEquals(Collections.emptyList(), allCouchProviders.findByProviderId(null));
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchProvidersImpl) allCouchProviders).removeAll();
    }
}
