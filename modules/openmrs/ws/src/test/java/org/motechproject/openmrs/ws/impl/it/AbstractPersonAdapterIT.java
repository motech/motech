package org.motechproject.openmrs.ws.impl.it;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.openmrs.model.OpenMRSAttribute;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.motechproject.mrs.services.MRSPersonAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractPersonAdapterIT {

    @Autowired
    private MRSPersonAdapter personAdapter;

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    private MrsListener mrsListener;
    final Object lock = new Object();

    @Before
    public void initialize() {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PERSON_SUBJECT, EventKeys.UPDATED_PERSON_SUBJECT, EventKeys.DELETED_PERSON_SUBJECT));
    }

    @Test
    public void shouldCreatePerson() throws MRSException, InterruptedException {

        OpenMRSPerson person = createPerson();
        OpenMRSPerson created;

        synchronized (lock) {
            created = (OpenMRSPerson) personAdapter.addPerson(person);
            lock.wait(60000);
        }

        assertNotNull(created.getId());

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deleted);
        assertFalse(mrsListener.updated);


        assertEquals(created.getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(created.getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(created.getLastName(), mrsListener.eventParameters.get(EventKeys.PERSON_LAST_NAME));

        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    @Test
    public void shouldUpdatePerson() throws MRSException, InterruptedException {
        OpenMRSPerson person = createPerson();
        OpenMRSPerson created;

        synchronized (lock) {
            created = (OpenMRSPerson) personAdapter.addPerson(person);
            lock.wait(60000);
        }

        assertTrue(created.getFirstName().matches("John"));

        assertEquals(created.getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(created.getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(created.getLastName(), mrsListener.eventParameters.get(EventKeys.PERSON_LAST_NAME));

        created.setFirstName("ANewName");

        OpenMRSPerson updated;

        synchronized (lock) {
            updated = (OpenMRSPerson) personAdapter.updatePerson(created);
            lock.wait(60000);
        }

        assertEquals(updated.getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(updated.getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(updated.getFirstName(), created.getFirstName());
        assertEquals(updated.getLastName(), mrsListener.eventParameters.get(EventKeys.PERSON_LAST_NAME));

        OpenMRSPerson personsRetrieved = (OpenMRSPerson) personAdapter.findByPersonId(updated.getPersonId()).get(0);
        assertTrue(personsRetrieved.getFirstName().matches("ANewName"));
        assertTrue(mrsListener.created);
        assertTrue(mrsListener.updated);
        assertFalse(mrsListener.deleted);
    }

    @Test
    public void shouldRemovePerson() throws InterruptedException {
        OpenMRSPerson person = createPerson();
        person.setFirstName("testRemove");
        OpenMRSPerson created = (OpenMRSPerson) personAdapter.addPerson(person);
        String personId = created.getPersonId();

        synchronized (lock) {
            personAdapter.removePerson(created);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertTrue(mrsListener.deleted);

        assertTrue(personAdapter.findByPersonId(personId).isEmpty());
    }

    @Test
    public void shouldGetPersonById() {
        OpenMRSPerson person = createPerson();
        OpenMRSPerson created = (OpenMRSPerson) personAdapter.addPerson(person);
        List<OpenMRSPerson> patient = (List<OpenMRSPerson>) personAdapter.findByPersonId(created.getPersonId());
        assertNotNull(patient.get(0));
    }

    private OpenMRSPerson createPerson() {
        OpenMRSPerson person = new OpenMRSPerson().firstName("John").lastName("Smith").address("10 Fifth Avenue")
                .birthDateEstimated(false).gender("M");
        OpenMRSAttribute attr = new OpenMRSAttribute("Birthplace", "Motech");
        List<MRSAttribute> attributes = new ArrayList<>();
        attributes.add(attr);
        person.setAttributes(attributes);

        return person;

    }

    public class MrsListener implements EventListener {

        private boolean created = false;
        private boolean updated = false;
        private boolean deleted = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.CREATED_NEW_PERSON_SUBJECT, EventKeys.UPDATED_PERSON_SUBJECT, EventKeys.DELETED_PERSON_SUBJECT})
        public void handle(MotechEvent event) {
            if (event.getSubject().equals(EventKeys.CREATED_NEW_PERSON_SUBJECT)) {
                created = true;
            } else if (event.getSubject().equals(EventKeys.UPDATED_PERSON_SUBJECT)) {
                updated = true;
            } else if (event.getSubject().equals(EventKeys.DELETED_PERSON_SUBJECT)) {
                deleted = true;
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
