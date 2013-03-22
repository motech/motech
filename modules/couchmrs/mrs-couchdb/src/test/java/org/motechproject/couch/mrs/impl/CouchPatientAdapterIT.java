package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.CouchPatient;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.motechproject.couch.mrs.repository.AllCouchPatients;
import org.motechproject.couch.mrs.repository.impl.AllCouchFacilitiesImpl;
import org.motechproject.couch.mrs.repository.impl.AllCouchPatientsImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchPatientAdapterIT extends SpringIntegrationTest {

    @Autowired
    private CouchPatientAdapter patientAdapter;

    @Autowired
    private AllCouchPatients allPatients;

    @Autowired
    private AllCouchFacilities allFacilities;

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    private Initializer init;

    private MrsListener mrsListener;

    final Object lock = new Object();

    @Autowired
    @Qualifier("couchPatientDatabaseConnector")
    CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT));
    }

    @Test
    public void shouldSaveAPatientAndRetrieveByExternalId() throws MRSCouchException, InterruptedException {
        CouchPerson person = init.initializePerson1();

        CouchFacility facility = new CouchFacility();
        facility.setFacilityId("facilityId");
        allFacilities.addFacility(facility);
        CouchPatient patient = new CouchPatient("123", "456", person, facility);

        synchronized (lock) {
            patientAdapter.savePatient(patient);
            lock.wait(60000);
        }

        MRSPatient patientRetrieved = patientAdapter.getPatientByMotechId("456");

        assertEquals(patientRetrieved.getMotechId(), "456");
        assertEquals(patientRetrieved.getFacility().getFacilityId(), "facilityId");

        assertEquals(patient.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(facility.getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(person.getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));

        assertTrue(mrsListener.created);
        assertFalse(mrsListener.deceased);
    }

    @Test
    public void shouldDeceasePatient() throws MRSCouchException, PatientNotFoundException, InterruptedException {
        CouchPerson person = init.initializePerson1();
        person.setDead(false);

        CouchFacility facility = new CouchFacility();
        facility.setFacilityId("facilityId");
        allFacilities.addFacility(facility);
        CouchPatient patient = new CouchPatient("123", "456", person, facility);
        patientAdapter.savePatient(patient);

        synchronized (lock) {
            patientAdapter.deceasePatient("456", null, DateTime.now().toDate(), null);
            lock.wait(60000);
        }

        MRSPatient patientRetrieved = patientAdapter.getPatientByMotechId("456");

        assertEquals(patientRetrieved.getMotechId(), "456");
        assertTrue(patientRetrieved.getPerson().isDead());

        assertEquals(patient.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        assertEquals(facility.getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(person.getPersonId(), mrsListener.eventParameters.get(EventKeys.PERSON_ID));

        assertTrue(mrsListener.created);
        assertTrue(mrsListener.deceased);
    }

    @Test
    public void shouldSearchPatientByNameandMotechId() throws MRSCouchException, PatientNotFoundException {
        CouchPerson person = init.initializePerson1();
        person.setPreferredName("John Doe");

        CouchFacility facility = new CouchFacility();
        facility.setFacilityId("facilityId");
        allFacilities.addFacility(facility);
        CouchPatient patient = new CouchPatient("123", "456", person, facility);
        patientAdapter.savePatient(patient);

        List<MRSPatient> patientsRetrieved = patientAdapter.search("John Doe", "456");

        assertEquals(asList("456"), extract(patientsRetrieved, on(MRSPatient.class).getMotechId()));
        assertEquals(asList("John Doe"), extract(patientsRetrieved, on(MRSPatient.class).getPerson().getPreferredName()));
    }

    @Test
    public void shouldGetAllPatientsList() throws MRSCouchException, PatientNotFoundException {
        CouchPerson person = init.initializePerson1();

        CouchFacility facility = new CouchFacility();
        facility.setFacilityId("facilityId");
        allFacilities.addFacility(facility);
        CouchPatient patient = new CouchPatient("1", "11", person, facility);
        patientAdapter.savePatient(patient);

        CouchPatient patient2 = new CouchPatient("2", "22", person, facility);
        patientAdapter.savePatient(patient2);

        List<MRSPatient> patientsRetrieved = patientAdapter.getAllPatients();


        assertThat(patientsRetrieved.size(), is(equalTo(2)));
        assertEquals(patientsRetrieved.get(0).getFacility().getFacilityId(), "facilityId");
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchPatientsImpl) allPatients).removeAll();
        ((AllCouchFacilitiesImpl) allFacilities).removeAll();
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    public class MrsListener implements EventListener {

        private boolean created = false;
        private boolean deceased = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventKeys.PATIENT_DECEASED_SUBJECT})
        public void handle(MotechEvent event) {
            if (event.getSubject().equals(EventKeys.CREATED_NEW_PATIENT_SUBJECT)) {
                created = true;
            } else if (event.getSubject().equals(EventKeys.PATIENT_DECEASED_SUBJECT)) {
                deceased = true;
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
