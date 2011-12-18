package org.motechproject.openmrs.services;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.motechproject.openmrs.util.PatientTestUtil;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static java.lang.Integer.parseInt;

public class OpenMRSPatientAdaptorIT extends OpenMRSIntegrationTestBase {

    @Autowired
    private LocationService locationService;

    @Autowired
    private PatientService patientService;

    @Test
    public void shouldSaveAPatientAndRetrieve() {
        final MRSFacility savedFacility = facilityAdaptor.saveFacility(new MRSFacility("name", "country", "region", "district", "province"));

        final PatientTestUtil patientTestUtil = new PatientTestUtil();
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;

        MRSPerson mrsPerson = new MRSPerson().firstName(first).middleName(middle).lastName(last).dateOfBirth(birthDate).birthDateEstimated(birthDateEstimated)
                .gender(gender).address(address1);
        final MRSPatient patient = new MRSPatient(mrsPerson, savedFacility);
        final MRSPatient savedPatient = patientAdaptor.savePatient(patient);

        authorizeAndRollback(new DirtyData() {
            public void rollback() {
                final org.openmrs.Patient openmrsPatient = patientService.getPatient(parseInt(savedPatient.getId()));
                patientService.purgePatient(openmrsPatient);
                locationService.purgeLocation(locationService.getLocation(parseInt(savedFacility.getId())));
            }
        });
        patientTestUtil.verifyReturnedPatient(first, middle, last, address1, birthDate, birthDateEstimated, gender, savedFacility, savedPatient);
    }
}
