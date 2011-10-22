package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.mrs.model.Facility;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.motechproject.openmrs.model.OpenMRSPatient;
import org.motechproject.openmrs.model.PatientType;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSPatientAdaptorTest {

    @Mock
    org.openmrs.api.PatientService mockPatientService;

    MRSPatientAdaptor<OpenMRSPatient> openMrsPatientService = new OpenMRSPatientAdaptor();

    @Before
    public void setUp() {
        initMocks(this);
        ReflectionTestUtils.setField(openMrsPatientService, "patientService", mockPatientService);
    }

    @Ignore
    @Test
    public void shouldSaveAMotherWithPreferredName() {
        // Story has been parked, test is incomplete
        Patient patient = mock(Patient.class);
        when(mockPatientService.savePatient(Matchers.<Patient>any())).thenReturn(patient);

        String firstName = "firstName";
        String middleName = "middleName";
        String lastName = "lastName";
        String preferredName = "preferredName";
        Date dateOfBirth = new GregorianCalendar(2011, 10, 30).getTime();
        Boolean estimateDateOfBirth = true;
        String gender = "Female";
        Boolean insured = true;
        String nhis = "1211";
        Date nhisExpires = new GregorianCalendar(2011, 11, 30).getTime();
        Facility facility = new Facility("Facility Name", "country", "region", "district", "sub-district");
        String address = "address";

        OpenMRSPatient openMRSPatient = new OpenMRSPatient.Builder().firstName(firstName)
                .middleName(middleName).lastName(lastName).preferredName(preferredName).patientType(PatientType.PREGNANT_MOTHER)
                .dateOfBirth(dateOfBirth).estimateDateOfBirth(estimateDateOfBirth).gender(gender)
                .insured(insured).nhis(nhis).nhisExpires(nhisExpires).facility(facility).address(address).build();

        OpenMRSPatient savedMRSPatient = openMrsPatientService.savePatient(openMRSPatient);

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(mockPatientService).savePatient(patientArgumentCaptor.capture());


        Patient patientObjectPassedToOpenMrsService = patientArgumentCaptor.getValue();

        assertThat(patientObjectPassedToOpenMrsService.getNames().size(), is(equalTo(1)));
        assertThat(patientObjectPassedToOpenMrsService.getNames().iterator().next(), is(equalTo(new PersonName(preferredName, middleName, lastName))));
    }

}
