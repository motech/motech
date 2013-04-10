package org.motechproject.openmrs.ws.impl.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.model.OpenMRSAttribute;
import org.motechproject.openmrs.model.OpenMRSFacility;
import org.motechproject.openmrs.model.OpenMRSPatient;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.motechproject.openmrs.ws.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractPatientAdapterIT {

    @Autowired
    private MRSFacilityAdapter facilityAdapter;

    @Autowired
    private MRSPatientAdapter patientAdapter;

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Test
    public void shouldCreatePatient() throws InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.UPDATED_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT, EventKeys.DELETED_PATIENT_SUBJECT));

        OpenMRSPerson person = new OpenMRSPerson().firstName("John").lastName("Smith").address("10 Fifth Avenue")
                .birthDateEstimated(false).gender("M");
        OpenMRSAttribute attr = new OpenMRSAttribute("Birthplace", "Motech");
        List<MRSAttribute> attributes = new ArrayList<>();
        attributes.add(attr);
        person.setAttributes(attributes);
        OpenMRSFacility facility = (OpenMRSFacility) facilityAdapter.getFacilities("Clinic 1").get(0);
        OpenMRSPatient patient = new OpenMRSPatient("600", person, facility);

        OpenMRSPatient created;

        synchronized (lock) {
            created = (OpenMRSPatient) patientAdapter.savePatient(patient);
            lock.wait(60000);
        }

        assertNotNull(created.getMotechId());

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deceased);
        assertFalse(mrsListener.updated);
        assertEquals(created.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(created.getFacility().getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(created.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(created.getMotechId(), mrsListener.eventParameters.get(EventKeys.MOTECH_ID));

        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    @Test
    public void shouldUpdatePatient() throws InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.UPDATED_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT, EventKeys.DELETED_PATIENT_SUBJECT));

        MRSPatient patient = patientAdapter.getPatientByMotechId("750");
        patient.getPerson().setFirstName("Changed Name");
        patient.getPerson().setAddress("Changed Address");

        synchronized (lock) {
            patientAdapter.updatePatient(patient);
            lock.wait(60000);
        }

        MRSPatient fetched = patientAdapter.getPatientByMotechId("750");

        assertEquals("Changed Name", fetched.getPerson().getFirstName());
        // Bug in OpenMRS Web Services does not currently allow updating address
        // assertEquals("Changed Address", fetched.getPerson().getAddress());

        assertFalse(mrsListener.created);
        assertFalse(mrsListener.deceased);
        assertTrue(mrsListener.updated);
        assertEquals(fetched.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(fetched.getFacility().getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(fetched.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(fetched.getMotechId(), mrsListener.eventParameters.get(EventKeys.MOTECH_ID));
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    @Test
    public void shouldGetPatientByMotechId() {
        MRSPatient patient = patientAdapter.getPatientByMotechId("700");
        assertNotNull(patient);
    }

    @Test
    public void shouldSearchForPatient() {
        List<MRSPatient> found = patientAdapter.search("Bill", null);

        assertEquals(asList("Bill"), extract(found, on(MRSPatient.class).getPerson().getFirstName()));
    }

    @Test
    public void shouldDeceasePerson() throws HttpException, PatientNotFoundException, InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.UPDATED_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT, EventKeys.DELETED_PATIENT_SUBJECT));

        synchronized (lock) {
            patientAdapter.deceasePatient("750", "Death Concept", new Date(), null);
            lock.wait(60000);
        }

        MRSPatient patient = patientAdapter.getPatientByMotechId("750");
        assertTrue(patient.getPerson().isDead());

        assertFalse(mrsListener.created);
        assertTrue(mrsListener.deceased);
        assertFalse(mrsListener.updated);
        assertEquals(patient.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(patient.getFacility().getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(patient.getPerson().getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));
        assertEquals(patient.getMotechId(), mrsListener.eventParameters.get(EventKeys.MOTECH_ID));
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    @Test
    public void shouldDeletePatient() throws InterruptedException, PatientNotFoundException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.UPDATED_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT, EventKeys.DELETED_PATIENT_SUBJECT));

        OpenMRSPerson person = new OpenMRSPerson().firstName("Test12").lastName("Name3").address("Black River")
                .birthDateEstimated(false).gender("M");
        OpenMRSFacility facility = (OpenMRSFacility) facilityAdapter.getFacilities("Clinic 1").get(0);
        OpenMRSPatient patient = new OpenMRSPatient("3487", person, facility);

        MRSPatient created;

        synchronized (lock) {
            created = patientAdapter.savePatient(patient);
            lock.wait(60000);
        }

        assertNotNull(created.getMotechId());
        assertNotNull(patientAdapter.getPatientByMotechId("3487"));

        synchronized (lock) {
            patientAdapter.deletePatient(created);
            lock.wait(60000);
        }

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deceased);
        assertFalse(mrsListener.updated);
        assertTrue(mrsListener.deleted);

        assertNull(patientAdapter.getPatientByMotechId("3487"));


        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    public class MrsListener implements EventListener {

        private boolean created = false;
        private boolean updated = false;
        private boolean deceased = false;
        private boolean deleted = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.UPDATED_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT, EventKeys.DELETED_PATIENT_SUBJECT})
        public void handle(MotechEvent event) {
            if (event.getSubject().equals(EventKeys.CREATED_NEW_PATIENT_SUBJECT)) {
                created = true;
            } else if (event.getSubject().equals(EventKeys.UPDATED_PATIENT_SUBJECT)) {
                updated = true;
            } else if (event.getSubject().equals(EventKeys.PATIENT_DECEASED_SUBJECT)) {
                deceased = true;
            } else if (event.getSubject().equals(EventKeys.DELETED_PATIENT_SUBJECT)) {
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
