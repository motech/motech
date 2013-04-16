package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchEncounter;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.CouchObservation;
import org.motechproject.couch.mrs.model.CouchPatient;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchEncounters;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.motechproject.couch.mrs.repository.AllCouchObservations;
import org.motechproject.couch.mrs.repository.AllCouchPatients;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.motechproject.couch.mrs.repository.impl.AllCouchEncountersImpl;
import org.motechproject.couch.mrs.repository.impl.AllCouchFacilitiesImpl;
import org.motechproject.couch.mrs.repository.impl.AllCouchObservationsImpl;
import org.motechproject.couch.mrs.repository.impl.AllCouchPatientsImpl;
import org.motechproject.couch.mrs.repository.impl.AllCouchProvidersImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchEncounterAdapterIT extends SpringIntegrationTest {

    @Autowired
    private CouchEncounterAdapter encounterAdapter;

    @Autowired
    private AllCouchObservations allObservations;

    @Autowired
    private AllCouchEncounters allEncounters;

    @Autowired
    private AllCouchFacilities allFacilities;

    @Autowired
    private AllCouchPatients allPatients;

    @Autowired
    private AllCouchProviders allProviders;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Autowired
    @Qualifier("couchEncounterDatabaseConnector")
    CouchDbConnector connector;

    @Test
    public void shouldReturnFullEncounterObjectGraph() throws MRSCouchException {
        CouchEncounter encounter = generateEncounter();
        MRSEncounter retrievedEncounter = encounterAdapter.getEncounterById(encounter.getEncounterId());

    }

    @Test
    public void shouldReturnEncountersByMotechIdAndType() throws MRSCouchException {
        CouchPatient encounterPatient = new CouchPatient("patientId", "motechId123", null, null);
        CouchPatient encounterPatient2 = new CouchPatient("patientId2", "motechId1234", null, null);

        CouchEncounter encounter1 = new CouchEncounter("12345", null, null, null, new DateTime(), null, encounterPatient, "encounterType1");
        CouchEncounter encounter2 = new CouchEncounter("123456", null, null, null, new DateTime(), null, encounterPatient, "encounterType1");
        CouchEncounter encounter3 = new CouchEncounter("1234567", null, null, null, new DateTime(), null, encounterPatient2, "encounterType1");
        CouchEncounter encounter4 = new CouchEncounter("12345678", null, null, null, new DateTime(), null, encounterPatient, "encounterType2");
        CouchEncounter encounter5 = new CouchEncounter("123456789", null, null, null, new DateTime(), null, encounterPatient2, "encounterType2");


        encounterAdapter.createEncounter(encounter1);
        encounterAdapter.createEncounter(encounter2);
        encounterAdapter.createEncounter(encounter3);
        encounterAdapter.createEncounter(encounter4);
        encounterAdapter.createEncounter(encounter5);

        List<MRSEncounter> retrievedEncounters = encounterAdapter.getEncountersByEncounterType("motechId123", "encounterType1");

        assertEquals(2, retrievedEncounters.size());
    }

    @Test
    public void shouldRaiseCreatedEncounterEventWithObservations() throws InterruptedException {
        CouchEncounter encounter = null;

        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, EventKeys.CREATED_NEW_ENCOUNTER_SUBJECT);

        synchronized (lock) {
            encounter = generateEncounter();
            lock.wait(60000);
        }

        Map<String, Object> parameters = mrsListener.eventParameters;
        
        assertEquals(encounter.getEncounterType(), mrsListener.eventParameters.get(EventKeys.ENCOUNTER_TYPE));
        assertEquals(encounter.getDate(), mrsListener.eventParameters.get(EventKeys.ENCOUNTER_DATE));
        assertEquals(encounter.getEncounterId(), mrsListener.eventParameters.get(EventKeys.ENCOUNTER_ID));
        assertTrue(mrsListener.created);
    }

    private CouchEncounter generateEncounter() {
        CouchObservation<String> observation = new CouchObservation<String>(new DateTime(), "testConcept", "stringValue", "patient1");

        CouchObservation<String> observation2 = new CouchObservation<String>(new DateTime(), "testConcep2", "stringValue2", "patient1");


        allObservations.addOrUpdateObservation(observation);
        allObservations.addOrUpdateObservation(observation2);

        CouchProvider provider = new CouchProvider("ProviderId", null);

        allProviders.addProvider(provider);

        CouchFacility facility = new CouchFacility("FacilityId");

        allFacilities.addFacility(facility);

        CouchPatientImpl patient = new CouchPatientImpl("patientId", "motechId", null, null);

        allPatients.addPatient(patient);

        CouchPatient encounterPatient = new CouchPatient("patientId", "motechId", null, null);

        Set<MRSObservation> observations = new HashSet<MRSObservation>();

        observations.add(observation);
        observations.add(observation2);

        CouchEncounter encounter = new CouchEncounter("12345", provider, null, facility, new DateTime(), observations, encounterPatient, "encounterType");

        encounterAdapter.createEncounter(encounter);

        return encounter;
    }

    public class MrsListener implements EventListener {

        private boolean created = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.CREATED_NEW_ENCOUNTER_SUBJECT})
        public void handle(MotechEvent event) {
            created = true;
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

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchObservationsImpl) allObservations).removeAll();
        ((AllCouchEncountersImpl) allEncounters).removeAll();
        ((AllCouchPatientsImpl) allPatients).removeAll();
        ((AllCouchProvidersImpl) allProviders).removeAll();
        ((AllCouchFacilitiesImpl) allFacilities).removeAll();
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

}
