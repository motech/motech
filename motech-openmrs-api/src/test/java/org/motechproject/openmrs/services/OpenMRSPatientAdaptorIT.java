package org.motechproject.openmrs.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.Patient;
import org.motechproject.mrs.services.MRSFacilityAdaptor;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static java.lang.Integer.parseInt;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationOpenmrsAPI.xml"})
public class OpenMRSPatientAdaptorIT extends OpenMRSIntegrationTestBase {

    @Before
    public void setUp() {
        super.setUp();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Autowired
    MRSPatientAdaptor mrsPatientAdaptor;

    @Autowired
    MRSFacilityAdaptor mrsFacilityAdaptor;

    @Autowired
    private LocationService openmrsLocationService;

    @Autowired
    private PatientService openmrsPatientService;

    @Test
    public void shouldSaveAPatientAndRetrieve() {
        final MRSFacility savedFacility = mrsFacilityAdaptor.saveFacility(new MRSFacility("name", "country", "region", "district", "province"));

        final OpenMRSPatientAdaptorTest.PatientTestUtil patientTestUtil = new OpenMRSPatientAdaptorTest.PatientTestUtil();
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthdate = new Date(1970, 3, 11);
        final String gender = "M";
        Boolean birthDateEstimated = true;

        final Patient patient = new Patient(first, middle, last, "", birthdate, birthDateEstimated, gender, address1, savedFacility);
        final Patient savedPatient = mrsPatientAdaptor.savePatient(patient);

        authorizeAndRollback(new DirtyData() {
            public void rollback() {
                final org.openmrs.Patient openmrsPatient = openmrsPatientService.getPatient(parseInt(savedPatient.getId()));
                openmrsPatientService.purgePatient(openmrsPatient);
                openmrsLocationService.purgeLocation(openmrsLocationService.getLocation(parseInt(savedFacility.getId())));
            }
        });
        patientTestUtil.verifyReturnedPatient(first, middle, last, address1, birthdate, birthDateEstimated, gender, savedFacility, savedPatient);
    }
}
