package org.motechproject.openmrs.services;

import org.junit.Test;
import org.motechproject.mrs.model.Facility;
import org.motechproject.mrs.model.Patient;
import org.motechproject.mrs.services.MRSFacilityAdaptor;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static java.lang.Integer.parseInt;

public class OpenMRSPatientAdaptorIT extends OpenMRSIntegrationTestBase {
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
        final Facility savedFacility = mrsFacilityAdaptor.saveFacility(new Facility("name", "country", "region", "district", "province"));

        final OpenMRSPatientAdaptorTest.PatientTestUtil patientTestUtil = new OpenMRSPatientAdaptorTest.PatientTestUtil();
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthdate = new Date(1970, 3, 11);
        final String gender = "M";

        final Patient patient = new Patient(first, middle, last, "", birthdate, gender, address1, savedFacility);
        final Patient savedPatient = mrsPatientAdaptor.savePatient(patient);

        authorizeAndRollback(new DirtyData() {
            public void rollback() {
                final org.openmrs.Patient openmrsPatient = openmrsPatientService.getPatient(parseInt(savedPatient.getId()));
                openmrsPatientService.purgePatient(openmrsPatient);
                openmrsLocationService.purgeLocation(openmrsLocationService.getLocation(parseInt(savedFacility.getId())));
            }
        });


        patientTestUtil.verifyReturnedPatient(first, middle, last, address1, birthdate, gender, savedFacility, savedPatient);
    }
}
