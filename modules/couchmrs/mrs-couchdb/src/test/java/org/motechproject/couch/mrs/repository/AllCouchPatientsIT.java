package org.motechproject.couch.mrs.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.CouchPatient;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.impl.AllCouchFacilitiesImpl;
import org.motechproject.couch.mrs.repository.impl.AllCouchPatientsImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class AllCouchPatientsIT extends SpringIntegrationTest {

    @Autowired
    private AllCouchPatients allCouchPatients;

    private Initializer init;

    @Autowired
    @Qualifier("couchPatientDatabaseConnector")
    private CouchDbConnector connector;

    @Before
    public void initialize() {
        init = new Initializer();
    }

    @Test
    public void shouldSavePatientAndRetrieveById() throws MRSCouchException {
        CouchPerson person = init.initializePerson1();

        CouchPatientImpl patient = new CouchPatientImpl("patientId", "MotechID", person, "facilityId");

        allCouchPatients.addPatient(patient);

        List<CouchPatientImpl> patientsRetrieved = allCouchPatients.findByMotechId("MotechID");

        CouchPatientImpl patientRetrieved = patientsRetrieved.get(0);

        assertEquals(patientRetrieved.getMotechId(), "MotechID");
        assertNotNull(patientRetrieved.getPerson());
    }

    @Test
    public void shouldUpdatePatientRecord() throws MRSCouchException {
        CouchPerson person = init.initializePerson1();

        CouchPatientImpl patient = new CouchPatientImpl("patientId", "MotechID", person, "facilityId");

        allCouchPatients.addPatient(patient);

        CouchPerson person2 = init.initializePerson1();
        person2.setFirstName("MotechName");

        CouchPatientImpl patient2 = new CouchPatientImpl("patientId2", "MotechID", person2, "facilityId2");

        allCouchPatients.addPatient(patient2);

        List<CouchPatientImpl> patientsRetrieved = allCouchPatients.findByMotechId("MotechID");

        CouchPatientImpl patientRetrieved = patientsRetrieved.get(0);

        assertEquals(patientRetrieved.getMotechId(), "MotechID");
        assertNotNull(patientRetrieved.getPerson());
        assertEquals(patientRetrieved.getPatientId(), "patientId2");
        assertEquals(patientRetrieved.getFacilityId(), "facilityId2");
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchPatientsImpl) allCouchPatients).removeAll();
    }
}
