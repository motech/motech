package org.motechproject.couch.mrs.repository;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllCouchMRSPersonsIT extends SpringIntegrationTest {

    @Autowired
    AllCouchMRSPersons allCouchMRSPersons;

    Initializer init;

    @Autowired
    @Qualifier("couchMRSDatabaseConnector")
    CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
    }

    @Test
    public void shouldSaveAPersonAndRetrieveByExternalId() throws MRSCouchException {
        CouchMRSPerson person1 = init.initializePerson1();
        allCouchMRSPersons.addPerson(person1);
        List<CouchMRSPerson> personsRetrieved = allCouchMRSPersons.findByExternalId(person1.getExternalId());
        CouchMRSPerson personCompare = personsRetrieved.get(0);
        assertTrue(person1.equals(personCompare));
    }

    @Test
    public void findByExternalIdShouldReturnNullIfExternalIdIsNull() {
        assertNull(null, allCouchMRSPersons.findByExternalId(null));
    }

    @Test
    public void shouldRetrieveIfOnlyExternalIdAndPhoneEntered() throws MRSCouchException {
        CouchMRSPerson person3 = init.initializePerson3WithOnlyExternalIdAndPhone();
        allCouchMRSPersons.addPerson(person3);
        List<CouchMRSPerson> personsRetrieved = allCouchMRSPersons.findByExternalId(person3.getExternalId());
        assertTrue(person3.equals(personsRetrieved.get(0)));
        assertTrue(person3.attrValue("phone number").equals(personsRetrieved.get(0).attrValue("phone number")));
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchMRSPersonsImpl) allCouchMRSPersons).removeAll();
    }
}
