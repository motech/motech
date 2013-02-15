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
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
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

    private Initializer init;

    @Autowired
    @Qualifier("couchPatientDatabaseConnector")
    CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
    }

    @Test
    public void shouldSaveAPatientAndRetrieveByExternalId() throws MRSCouchException {
        CouchPerson person = init.initializePerson1();

        CouchFacility facility = new CouchFacility();
        facility.setFacilityId("facilityId");
        allFacilities.addFacility(facility);
        CouchPatient patient = new CouchPatient("123", "456", person, facility);
        patientAdapter.savePatient(patient);

        Patient patientRetrieved = patientAdapter.getPatientByMotechId("456");

        assertEquals(patientRetrieved.getMotechId(), "456");
        assertEquals(patientRetrieved.getFacility().getFacilityId(), "facilityId");
    }

    @Test
    public void shouldDeceasePatient() throws MRSCouchException, PatientNotFoundException {
        CouchPerson person = init.initializePerson1();
        person.setDead(false);

        CouchFacility facility = new CouchFacility();
        facility.setFacilityId("facilityId");
        allFacilities.addFacility(facility);
        CouchPatient patient = new CouchPatient("123", "456", person, facility);
        patientAdapter.savePatient(patient);

        patientAdapter.deceasePatient("456", null, DateTime.now().toDate(), null);

        Patient patientRetrieved = patientAdapter.getPatientByMotechId("456");

        assertEquals(patientRetrieved.getMotechId(), "456");
        assertTrue(patientRetrieved.getPerson().isDead());
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

        List<Patient> patientsRetrieved = patientAdapter.search("John Doe", "456");

        assertEquals(asList("456"), extract(patientsRetrieved, on(Patient.class).getMotechId()));
        assertEquals(asList("John Doe"), extract(patientsRetrieved, on(Patient.class).getPerson().getPreferredName()));
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchPatientsImpl) allPatients).removeAll();
        ((AllCouchFacilitiesImpl) allFacilities).removeAll();
    }
}
