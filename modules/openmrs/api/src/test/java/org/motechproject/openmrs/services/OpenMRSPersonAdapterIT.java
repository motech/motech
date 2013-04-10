package org.motechproject.openmrs.services;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class OpenMRSPersonAdapterIT extends OpenMRSIntegrationTestBase {

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Test
    @Transactional
    public void shouldSaveAPersonAndRetrieve() throws InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PERSON_SUBJECT, EventKeys.UPDATED_PERSON_SUBJECT, EventKeys.DELETED_PERSON_SUBJECT));

        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address = "long street";
        final Date birthDate = new LocalDate(1988, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;

        final MRSPerson person = createPersonMrs(first, middle, last, birthDate, birthDateEstimated, gender, address);
        final MRSPerson savedPerson;

        synchronized (lock) {
            savedPerson = personAdapter.addPerson(person);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deleted);
        assertFalse(mrsListener.updated);
        assertEquals(savedPerson.getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(savedPerson.getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(savedPerson.getDateOfBirth(), mrsListener.eventParameters.get(EventKeys.PERSON_DATE_OF_BIRTH));
        assertEquals(savedPerson.getFirstName(), first);

        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldUpdatePerson() throws InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PERSON_SUBJECT, EventKeys.UPDATED_PERSON_SUBJECT, EventKeys.DELETED_PERSON_SUBJECT));

        final String first = "Ali";
        final String middle = "Middle";
        final String last = "Sheen";
        final String address = "long street";
        final Date birthDate = new LocalDate(1920, 6, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;

        MRSPerson person = createPersonMrs(first, middle, last, birthDate, birthDateEstimated, gender, address);
        MRSPerson savedPerson;


        synchronized (lock) {
            savedPerson = personAdapter.addPerson(person);
            lock.wait(60000);
        }

        person = createPersonMrs("new", null, "mike", birthDate, birthDateEstimated, "W", "new street");
        person.setPersonId(savedPerson.getPersonId());

        synchronized (lock) {
            savedPerson = personAdapter.updatePerson(person);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deleted);
        assertTrue(mrsListener.updated);
        assertEquals(savedPerson.getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(savedPerson.getFirstName(), mrsListener.eventParameters.get(EventKeys.PERSON_FIRST_NAME));
        assertEquals(savedPerson.getLastName(), "mike");

        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldFindPersonById() {
        OpenMRSPerson savedPerson = (OpenMRSPerson) personAdapter.addPerson(createPersonMrs("test", "middle2", "grey", null, false, "M", "open street"));
        OpenMRSPerson foundedPerson = (OpenMRSPerson) personAdapter.findByPersonId(savedPerson.getPersonId()).get(0);

        assertEquals(savedPerson.getPersonId(), foundedPerson.getPersonId());
        assertEquals(savedPerson.getFirstName(), foundedPerson.getFirstName());
        assertEquals(foundedPerson.getLastName(), "grey");
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldGetAllPersonList() {
        final String firstName1 = "Amesh";
        final String middleName1 = "Ben";
        final String lastName1 = "Doug";

        final String firstName2 = "Amet";
        final String middleName2 = "Brit";
        final String lastName2 = "Cathey";

        final String firstName3 = null;
        final String middleName3 = "nullFirstNameCheck1";
        final String lastName3 = "Douglas";

        final String firstName4 = null;
        final String middleName4 = "nullFirstNameCheck1";
        final String lastName4 = "Catherina";

        final String address = "silver street";
        final Date birthDate = new LocalDate(1985, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;

        OpenMRSPerson savedPerson = (OpenMRSPerson) personAdapter.addPerson(createPersonMrs(firstName1, middleName1, lastName1, birthDate, birthDateEstimated, gender, address));
        personAdapter.addPerson(createPersonMrs(firstName2, middleName2, lastName2, birthDate, birthDateEstimated, gender, address));
        personAdapter.addPerson(createPersonMrs(firstName3, middleName3, lastName3, birthDate, birthDateEstimated, gender, address));
        personAdapter.addPerson(createPersonMrs(firstName4, middleName4, lastName4, birthDate, birthDateEstimated, gender, address));

        List<OpenMRSPerson> returnedPerson = (List<OpenMRSPerson>) personAdapter.findAllPersons();

        assertThat(returnedPerson.size(), is(equalTo(5))); //+1 because first in db is super person
        assertThat(savedPerson.getAddress(), is(equalTo(address)));
        assertThat(savedPerson.getGender(), is(equalTo(gender)));
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    private MRSPerson createPersonMrs(String first, String middle, String last, Date birthDate, Boolean birthDateEstimated, String gender, String address1) {
        return new OpenMRSPerson().firstName(first).middleName(middle).lastName(last).dateOfBirth(new DateTime(birthDate)).birthDateEstimated(birthDateEstimated)
                .gender(gender).address(address1);
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
