package org.motechproject.couch.mrs.repository;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.impl.AllCouchPersonsImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class AllCouchPersonsIT extends SpringIntegrationTest {

    @Autowired
    AllCouchPersons allCouchMRSPersons;

    Initializer init;

    @Autowired
    @Qualifier("couchPersonDatabaseConnector")
    CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
    }

    @Test
    public void shouldSaveAPersonAndRetrieveByExternalId() throws MRSCouchException {
        CouchPerson person1 = init.initializePerson1();
        allCouchMRSPersons.addPerson(person1);
        List<CouchPerson> personsRetrieved = allCouchMRSPersons.findByPersonId(person1.getPersonId());
        CouchPerson personCompare = personsRetrieved.get(0);
        assertTrue(person1.equals(personCompare));
    }

    @Test
    public void shouldUpdatePersonRecord() throws MRSCouchException {

        CouchPerson person1 = init.initializePerson1();

        allCouchMRSPersons.addPerson(person1);

        CouchPerson person2 = init.initializeSecondPerson();
        person2.setPersonId(person1.getPersonId());

        allCouchMRSPersons.addPerson(person2);

        List<CouchPerson> personsRetrieved = allCouchMRSPersons.findByPersonId(person1.getPersonId());

        CouchPerson personCompare = personsRetrieved.get(0);
        assertEquals(personCompare.getFirstName(), "AName");
    }

    @Test
    public void findByExternalIdShouldReturnNullIfExternalIdIsNull() {
        assertNull(null, allCouchMRSPersons.findByPersonId(null));
    }

    @Test
    public void shouldRetrieveIfOnlyExternalIdAndPhoneEntered() throws MRSCouchException {
        CouchPerson person3 = init.initializePerson3WithOnlyExternalIdAndPhone();
        allCouchMRSPersons.addPerson(person3);
        List<CouchPerson> personsRetrieved = allCouchMRSPersons.findByPersonId(person3.getPersonId());
        assertTrue(person3.equals(personsRetrieved.get(0)));
        assertTrue(person3.attrValue("phone number").equals(personsRetrieved.get(0).attrValue("phone number")));
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchPersonsImpl) allCouchMRSPersons).removeAll();
    }
}
