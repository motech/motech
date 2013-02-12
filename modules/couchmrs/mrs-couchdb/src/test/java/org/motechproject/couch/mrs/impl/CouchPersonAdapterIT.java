package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchPersonAdapterIT extends SpringIntegrationTest {

    @Autowired
    private CouchPersonAdapter couchMRSService;

    private Initializer init;

    @Autowired
    @Qualifier("couchPersonDatabaseConnector")
    CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
    }

    @Test
    public void shouldSaveAPersonAndRetrieveByExternalId() {
        CouchPerson person1 = init.initializePerson1();
        try {
            couchMRSService.addPerson(person1.getPersonId(), person1.getFirstName(), person1.getLastName(),
                    person1.getDateOfBirth(), person1.getGender(), person1.getAddress(), person1.getAttributes());
        } catch (MRSCouchException e) {
            e.printStackTrace();
        }
        List<CouchPerson> personsRetrieved = couchMRSService.findByPersonId(person1.getPersonId());
        assertEquals(person1, personsRetrieved.get(0));
    }

    @Test
    public void shouldHandleNullFirstAndLastName() throws MRSCouchException {
        CouchPerson person2 = new CouchPerson();
        person2.setPersonId("externalid");
        couchMRSService.addPerson(person2.getPersonId(), null, null, null, null, null, null);
        List<CouchPerson> personsRetrieved = couchMRSService.findByPersonId(person2.getPersonId());
        assertEquals(person2, personsRetrieved.get(0));
    }

    @Test
    public void shouldThrowExceptionIfNullExternalId() throws MRSCouchException {
        CouchPerson person3 = new CouchPerson();
        assertNull(person3.getPersonId());
        boolean thrown = false;
        try {
            couchMRSService.addPerson(person3.getPersonId(), null, null, null, null, null, null);
        } catch (NullPointerException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void shouldUpdatePerson() throws MRSCouchException {
        CouchPerson person = init.initializeSecondPerson();
        couchMRSService.addPerson(person);
        assertTrue(person.getFirstName().matches("AName"));
        person.setFirstName("ANewName");
        couchMRSService.updatePerson(person);
        List<CouchPerson> personsRetrieved = couchMRSService.findByPersonId(person.getPersonId());
        assertTrue(personsRetrieved.get(0).getFirstName().matches("ANewName"));
    }

    @Test
    public void shouldUpdatePersonIfExistsInDB() throws MRSCouchException {
        CouchPerson person = init.initializeSecondPerson();
        couchMRSService.addPerson(person);
        assertTrue(person.getFirstName().matches("AName"));
        assertTrue(person.getPersonId().matches("externalId"));
        person.setFirstName("ANewName");
        couchMRSService.addPerson(person);
        List<CouchPerson> personsRetrieved = couchMRSService.findByPersonId(person.getPersonId());
        assertTrue(personsRetrieved.get(0).getFirstName().matches("ANewName"));
        assertTrue(person.getPersonId().matches("externalId"));
    }

    @Test
    public void shouldNotAllowUpdatesToExternalID() throws MRSCouchException {
        CouchPerson person = init.initializeSecondPerson();
        couchMRSService.addPerson(person);
        assertTrue(person.getFirstName().matches("AName"));
        assertTrue(person.getPersonId().matches("externalId"));
        person.setPersonId("newExternalId");
        person.setFirstName("ANewName");
        boolean thrown = false;
        try {
            couchMRSService.addPerson(person);
        } catch (MRSCouchException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void shouldRemovePerson() throws MRSCouchException {
        CouchPerson person = init.initializeSecondPerson();
        couchMRSService.addPerson(person);
        List<CouchPerson> personsRetrieved = couchMRSService.findByPersonId(person.getPersonId());
        couchMRSService.removePerson(personsRetrieved.get(0));
        List<CouchPerson> shouldBeEmpty = couchMRSService.findByPersonId(person.getPersonId());
        assertTrue(shouldBeEmpty.isEmpty());
    }

    @Test
    public void shouldFindThreePersonsWhenFindAll() throws MRSCouchException {
        CouchPerson first = init.initializePerson1();
        couchMRSService.addPerson(first);
        CouchPerson second = init.initializeSecondPerson();
        couchMRSService.addPerson(second);
        CouchPerson third = init.initializeThirdPerson();
        couchMRSService.addPerson(third);

        List<CouchPerson> allPersons = couchMRSService.findAllPersons();

        assertEquals(asList(first, second, third), allPersons);
    }

    @Test
    public void sizeShouldBeZeroWhenFindAllNoPersons() {
        List<CouchPerson> allPersons = couchMRSService.findAllPersons();
        assertTrue(allPersons.isEmpty());
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        couchMRSService.removeAll();
    }

}
