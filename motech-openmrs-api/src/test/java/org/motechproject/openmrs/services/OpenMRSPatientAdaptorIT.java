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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class OpenMRSPatientAdaptorIT extends OpenMRSIntegrationTestBase {

    @Autowired
    private LocationService locationService;

    @Autowired
    private PatientService patientService;

    @Test
    @Transactional(readOnly = true)
    public void shouldSaveAPatientAndRetrieve() {

        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;
        String motechId = "1234567";

        final MRSFacility savedFacility = facilityAdaptor.saveFacility(new MRSFacility("name", "country", "region", "district", "province"));

        MRSPerson mrsPerson = new MRSPerson().firstName(first).middleName(middle).lastName(last).dateOfBirth(birthDate).birthDateEstimated(birthDateEstimated)
                .gender(gender).address(address1);
        final MRSPatient patient = new MRSPatient(motechId, mrsPerson, savedFacility);
        final MRSPatient savedPatient = patientAdaptor.savePatient(patient);

        new PatientTestUtil().verifyReturnedPatient(first, middle, last, address1, birthDate, birthDateEstimated, gender, savedFacility, savedPatient, motechId);
    }

    @Test
    @Transactional(readOnly = true)
    public void shouldSearchPatientsByNameOrId() {
        final String firstName1 = "John";
        final String middleName1 = "Allen";
        final String lastName1 = "Raul";
        final String firstName2 = "Joseph";
        final String middleName2 = "Ra";
        final String lastName2 = "Rauak";
        final String motechId1 = "12356";
        final String motechId2 = "423546";


        final String address = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;

        final MRSFacility savedFacility = facilityAdaptor.saveFacility(new MRSFacility("name", "country", "region", "district", "province"));

        createPatientInOpenMrs(motechId1, firstName1, middleName1, lastName1, address, birthDate, gender, birthDateEstimated, savedFacility);
        createPatientInOpenMrs(motechId2, firstName2, middleName2, lastName2, address, birthDate, gender, birthDateEstimated, savedFacility);
        List<MRSPatient> returnedPatients = patientAdaptor.search("Ra", null);

        new PatientTestUtil().verifyReturnedPatient(firstName1, middleName1, lastName1, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(0), motechId1);
        new PatientTestUtil().verifyReturnedPatient(firstName2, middleName2, lastName2, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(1), motechId2);
        assertThat(returnedPatients.size(), is(equalTo(2)));

        assertThat(patientAdaptor.search("x", null), is(equalTo(Arrays.<MRSPatient>asList())));

        returnedPatients = patientAdaptor.search("All", null);
        assertThat(returnedPatients.size(), is(equalTo(1)));
        new PatientTestUtil().verifyReturnedPatient(firstName1, middleName1, lastName1, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(0), motechId1);

        returnedPatients = patientAdaptor.search("Jo All", null);
        assertThat(returnedPatients.size(), is(equalTo(1)));
        new PatientTestUtil().verifyReturnedPatient(firstName1, middleName1, lastName1, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(0), motechId1);

        returnedPatients = patientAdaptor.search(null, "12356");
        assertThat(returnedPatients.size(), is(equalTo(1)));
        new PatientTestUtil().verifyReturnedPatient(firstName1, middleName1, lastName1, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(0), motechId1);

        returnedPatients = patientAdaptor.search(null, "23");
        assertThat(returnedPatients.size(), is(equalTo(0)));

        assertThat(patientAdaptor.search(null, "0000"), is(equalTo(Arrays.<MRSPatient>asList())));

        returnedPatients = patientAdaptor.search("All", "12356");
        assertThat(returnedPatients.size(), is(equalTo(1)));
        new PatientTestUtil().verifyReturnedPatient(firstName1, middleName1, lastName1, address, birthDate, birthDateEstimated, gender, savedFacility, returnedPatients.get(0), motechId1);

        returnedPatients = patientAdaptor.search("Ra", "123");
        assertThat(returnedPatients.size(), is(equalTo(0)));

        assertThat(patientAdaptor.search("x", "0000"), is(equalTo(Arrays.<MRSPatient>asList())));
    }

    private MRSPatient createPatientInOpenMrs(String motechId, String firstName, String middleName, String lastName, String address, Date birthDate, String gender, Boolean birthDateEstimated, MRSFacility savedFacility) {

        MRSPerson mrsPerson = new MRSPerson().firstName(firstName).middleName(middleName).lastName(lastName).dateOfBirth(birthDate).birthDateEstimated(birthDateEstimated)
                .gender(gender).address(address);
        final MRSPatient patient = new MRSPatient(motechId, mrsPerson, savedFacility);
        return patientAdaptor.savePatient(patient);
    }
}
