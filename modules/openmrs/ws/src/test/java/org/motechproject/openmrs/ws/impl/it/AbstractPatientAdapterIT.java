package org.motechproject.openmrs.ws.impl.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractPatientAdapterIT {

    @Autowired
    private MRSFacilityAdapter facilityAdapter;

    @Autowired
    private MRSPatientAdapter patientAdapter;

    @Test
    public void shouldCreatePatient() {
        MRSPerson person = new MRSPerson().firstName("John").lastName("Smith").address("10 Fifth Avenue")
                .birthDateEstimated(false).gender("M").preferredName("Jonathan");
        Attribute attr = new Attribute("Birthplace", "Motech");
        MRSFacility facility = facilityAdapter.getFacilities("Clinic 1").get(0);
        MRSPatient patient = new MRSPatient("600", person, facility);

        MRSPatient created = patientAdapter.savePatient(patient);

        assertNotNull(created.getId());
    }

    @Test
    public void shouldUpdatePatient() {
        MRSPatient patient = patientAdapter.getPatientByMotechId("750");
        patient.getPerson().firstName("Changed Name");
        patient.getPerson().address("Changed Address");
        patientAdapter.updatePatient(patient);

        MRSPatient fetched = patientAdapter.getPatientByMotechId("750");

        assertEquals("Changed Name", fetched.getPerson().getFirstName());
        // Bug in OpenMRS Web Services does not currently allow updating address
        // assertEquals("Changed Address", fetched.getPerson().getAddress());
    }

    @Test
    public void shouldGetPatientByMotechId() {
        MRSPatient patient = patientAdapter.getPatientByMotechId("700");
        assertNotNull(patient);
    }

    @Test
    public void shouldSearchForPatient() {
        List<MRSPatient> found = patientAdapter.search("Bill", null);

        assertEquals(1, found.size());
    }

    @Test
    public void shouldDeceasePerson() throws HttpException, PatientNotFoundException {
        patientAdapter.deceasePatient("750", "Death Concept", new Date(), null);

        MRSPatient patient = patientAdapter.getPatientByMotechId("750");
        assertTrue(patient.getPerson().isDead());
    }

}
