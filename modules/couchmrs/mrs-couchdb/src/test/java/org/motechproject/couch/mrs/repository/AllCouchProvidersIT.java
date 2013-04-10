package org.motechproject.couch.mrs.repository;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.CouchProviderImpl;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.impl.AllCouchProvidersImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        CouchProviderImpl provider = new CouchProviderImpl("Provider1", "PersonId");
        allCouchProviders.addProvider(provider);
        List<CouchProviderImpl> providersRetrieved = allCouchProviders.findByProviderId("Provider1");
        CouchProviderImpl providerCompare = providersRetrieved.get(0);
        assertTrue(providerCompare.getProviderId().equals(provider.getProviderId()));
        assertTrue(providerCompare.getPersonId().equals(provider.getPersonId()));
    }

    @Test
    public void shouldUpdateProviderRecord() throws MRSCouchException {
        CouchProviderImpl provider = new CouchProviderImpl("Provider1", "PersonId");
        allCouchProviders.addProvider(provider);

        CouchProviderImpl provider2  = new CouchProviderImpl("Provider1", "NewPersonId");
        allCouchProviders.addProvider(provider2);

        List<CouchProviderImpl> providersRetrieved = allCouchProviders.findByProviderId("Provider1");

        assertEquals(providersRetrieved.get(0).getPersonId(), "NewPersonId");
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
