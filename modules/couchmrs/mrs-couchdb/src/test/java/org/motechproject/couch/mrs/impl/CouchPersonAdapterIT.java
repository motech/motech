package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchPersonAdapterIT extends SpringIntegrationTest {

    @Autowired
    private CouchPersonAdapter couchMRSService;

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    private Initializer init;
    private MrsListener mrsListener;

    final Object lock = new Object();

    @Autowired
    @Qualifier("couchPersonDatabaseConnector")
    CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PERSON_SUBJECT, EventKeys.UPDATED_PERSON_SUBJECT, EventKeys.DELETED_PERSON_SUBJECT));
    }

    @Test
    public void shouldSaveAPersonAndRetrieveByExternalId() throws InterruptedException {
        CouchPerson person1 = init.initializePerson1();
        try {
            synchronized (lock) {
                couchMRSService.addPerson(person1.getPersonId(), person1.getFirstName(), person1.getLastName(),
                        person1.getDateOfBirth(), person1.getGender(), person1.getAddress(), person1.getAttributes());
                lock.wait(60000);
            }
        } catch (MRSCouchException e) {
            e.printStackTrace();
        }
        List<CouchPerson> personsRetrieved = couchMRSService.findByPersonId(person1.getPersonId());
        assertEquals(person1, personsRetrieved.get(0));

        assertEquals(personsRetrieved.get(0).getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(personsRetrieved.get(0).getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(personsRetrieved.get(0).getLastName(), mrsListener.eventParameters.get(EventKeys.PERSON_LAST_NAME));

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertFalse( mrsListener.removed);
    }

    @Test
    public void shouldHandleNullFirstAndLastName() throws MRSCouchException, InterruptedException {
        CouchPerson person2 = new CouchPerson();
        person2.setPersonId("externalid");

        synchronized (lock) {
            couchMRSService.addPerson(person2.getPersonId(), null, null, null, null, null, null);
            lock.wait(60000);
        }

        List<CouchPerson> personsRetrieved = couchMRSService.findByPersonId(person2.getPersonId());
        assertEquals(person2, personsRetrieved.get(0));

        assertEquals(personsRetrieved.get(0).getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(personsRetrieved.get(0).getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(personsRetrieved.get(0).getLastName(), mrsListener.eventParameters.get(EventKeys.PERSON_LAST_NAME));

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertFalse(mrsListener.removed);
    }

    @Test
    public void shouldThrowExceptionIfNullExternalId() throws MRSCouchException, InterruptedException {
        CouchPerson person3 = new CouchPerson();
        assertNull(person3.getPersonId());
        boolean thrown = false;
        try {
            synchronized (lock) {
                couchMRSService.addPerson(person3.getPersonId(), null, null, null, null, null, null);
                lock.wait(60000);
            }
        } catch (NullPointerException e) {
            thrown = true;
        }

        assertTrue(thrown);
        assertNull(mrsListener.eventParameters);
        assertFalse(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertFalse(mrsListener.removed);
    }

    @Test
    public void shouldUpdatePerson() throws MRSCouchException, InterruptedException {
        CouchPerson person = init.initializeSecondPerson();

        synchronized (lock) {
            couchMRSService.addPerson(person);
            lock.wait(60000);
        }

        assertTrue(person.getFirstName().matches("AName"));

        assertEquals(person.getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(person.getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(person.getLastName(), mrsListener.eventParameters.get(EventKeys.PERSON_LAST_NAME));

        person.setFirstName("ANewName");

        synchronized (lock) {
            couchMRSService.updatePerson(person);
            lock.wait(60000);
        }

        assertEquals(person.getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(person.getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(person.getLastName(), mrsListener.eventParameters.get(EventKeys.PERSON_LAST_NAME));

        List<CouchPerson> personsRetrieved = couchMRSService.findByPersonId(person.getPersonId());
        assertTrue(personsRetrieved.get(0).getFirstName().matches("ANewName"));
        assertTrue(mrsListener.created);
        assertTrue(mrsListener.updated);
        assertFalse(mrsListener.removed);
    }

    @Test
    public void shouldUpdatePersonIfExistsInDB() throws MRSCouchException, InterruptedException {
        CouchPerson person = init.initializeSecondPerson();
        couchMRSService.addPerson(person);
        assertTrue(person.getFirstName().matches("AName"));
        assertTrue(person.getPersonId().matches("externalId"));
        person.setFirstName("ANewName");
        synchronized (lock) {
            couchMRSService.addPerson(person);
            lock.wait(60000);
        }
        List<CouchPerson> personsRetrieved = couchMRSService.findByPersonId(person.getPersonId());
        assertTrue(personsRetrieved.get(0).getFirstName().matches("ANewName"));
        assertTrue(person.getPersonId().matches("externalId"));

        assertEquals(personsRetrieved.get(0).getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(personsRetrieved.get(0).getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(personsRetrieved.get(0).getLastName(), mrsListener.eventParameters.get(EventKeys.PERSON_LAST_NAME));

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertFalse(mrsListener.removed);
    }

    @Test
    public void shouldNotAllowUpdatesToExternalID() throws MRSCouchException, InterruptedException {
        CouchPerson person = init.initializeSecondPerson();

        synchronized (lock) {
            couchMRSService.addPerson(person);
            lock.wait(60000);
        }

        assertTrue(person.getFirstName().matches("AName"));
        assertTrue(person.getPersonId().matches("externalId"));

        assertEquals(person.getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(person.getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(person.getLastName(), mrsListener.eventParameters.get(EventKeys.PERSON_LAST_NAME));

        person.setPersonId("newExternalId");
        person.setFirstName("ANewName");
        boolean thrown = false;
        try {
            couchMRSService.addPerson(person);
        } catch (MRSCouchException e) {
            thrown = true;
        }
        assertTrue(thrown);

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertFalse(mrsListener.removed);
    }

    @Test
    public void shouldRemovePerson() throws MRSCouchException, InterruptedException {
        CouchPerson person = init.initializeSecondPerson();
        couchMRSService.addPerson(person);
        List<CouchPerson> personsRetrieved = couchMRSService.findByPersonId(person.getPersonId());

        synchronized (lock) {
            couchMRSService.removePerson(personsRetrieved.get(0));
            lock.wait(60000);
        }

        List<CouchPerson> shouldBeEmpty = couchMRSService.findByPersonId(person.getPersonId());
        assertTrue(shouldBeEmpty.isEmpty());

        assertEquals(personsRetrieved.get(0).getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(personsRetrieved.get(0).getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(personsRetrieved.get(0).getLastName(), mrsListener.eventParameters.get(EventKeys.PERSON_LAST_NAME));

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertTrue(mrsListener.removed);
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
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    public class MrsListener implements EventListener {

        private boolean created = false;
        private boolean updated = false;
        private boolean removed = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.CREATED_NEW_PERSON_SUBJECT, EventKeys.UPDATED_PERSON_SUBJECT, EventKeys.DELETED_PERSON_SUBJECT})
        public void handle(MotechEvent event) {
            if (event.getSubject().equals(EventKeys.CREATED_NEW_PERSON_SUBJECT)) {
                created = true;
            } else if (event.getSubject().equals(EventKeys.UPDATED_PERSON_SUBJECT)) {
                updated = true;
            } else if (event.getSubject().equals(EventKeys.DELETED_PERSON_SUBJECT)) {
                removed = true;
            }
            eventParameters = event.getParameters();
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public String getIdentifier() {
            return "mrsTestListener";
        }
    }
}
