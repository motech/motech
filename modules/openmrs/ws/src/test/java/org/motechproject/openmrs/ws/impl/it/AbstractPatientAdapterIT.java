package org.motechproject.openmrs.ws.impl.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.OpenMRSAttribute;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractPatientAdapterIT {

    @Autowired
    private FacilityAdapter facilityAdapter;

    @Autowired
    private PatientAdapter patientAdapter;

    @Test
    public void shouldCreatePatient() {
        OpenMRSPerson person = new OpenMRSPerson().firstName("John").lastName("Smith").address("10 Fifth Avenue")
                .birthDateEstimated(false).gender("M").preferredName("Jonathan");
        OpenMRSAttribute attr = new OpenMRSAttribute("Birthplace", "Motech");
        OpenMRSFacility facility = (OpenMRSFacility) facilityAdapter.getFacilities("Clinic 1").get(0);
        OpenMRSPatient patient = new OpenMRSPatient("600", person, facility);

        OpenMRSPatient created = (OpenMRSPatient) patientAdapter.savePatient(patient);

        assertNotNull(created.getPatientId());
    }

    @Test
    public void shouldUpdatePatient() {
        OpenMRSPatient patient = (OpenMRSPatient) patientAdapter.getPatientByMotechId("750");
        patient.getPerson().firstName("Changed Name");
        patient.getPerson().address("Changed Address");
        patientAdapter.updatePatient(patient);

        Patient fetched = patientAdapter.getPatientByMotechId("750");

        assertEquals("Changed Name", fetched.getPerson().getFirstName());
        // Bug in OpenMRS Web Services does not currently allow updating address
        // assertEquals("Changed Address", fetched.getPerson().getAddress());
    }

    @Test
    public void shouldGetPatientByMotechId() {
        Patient patient = patientAdapter.getPatientByMotechId("700");
        assertNotNull(patient);
    }

    @Test
    public void shouldSearchForPatient() {
        List<Patient> found = patientAdapter.search("Bill", null);

        assertEquals(1, found.size());
    }

    @Test
    public void shouldDeceasePerson() throws HttpException, PatientNotFoundException {
        patientAdapter.deceasePatient("750", "Death Concept", new Date(), null);

        Patient patient = patientAdapter.getPatientByMotechId("750");
        assertTrue(patient.getPerson().isDead());
    }

}
