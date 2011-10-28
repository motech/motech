package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.mrs.model.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSPatientAdaptorTest {

    @Mock
    PatientService mockPatientService;

    OpenMRSPatientAdaptor openMRSPatientAdaptor;

    @Before
    public void setUp() {
        initMocks(this);
        openMRSPatientAdaptor = new OpenMRSPatientAdaptor();
        ReflectionTestUtils.setField(openMRSPatientAdaptor, "patientService", mockPatientService);
    }

    @Test
    public void shouldSaveAPatient() {
        final Person person = new Person();
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthdate = new Date(1970, 3, 11);
        final boolean birthdateEstimated = true;
        final String gender = "male";

        PersonName personName = new PersonName(first, middle, last);
        person.addName(personName);
        setAddress(person, address1);
        final org.openmrs.Patient mrsPatient = new org.openmrs.Patient(person);
        mrsPatient.setBirthdate(birthdate);
        mrsPatient.setBirthdateEstimated(birthdateEstimated);
        mrsPatient.setGender(gender);

        when(mockPatientService.savePatient(Matchers.<org.openmrs.Patient>any())).thenReturn(mrsPatient);
        final Patient actualPatient = openMRSPatientAdaptor.savePatient(new Patient(first, middle, last, null, birthdate, gender, address1));

        assertThat(actualPatient.getFirstName(), is(first));
        assertThat(actualPatient.getLastName(), is(last));
        assertThat(actualPatient.getMiddleName(), is(middle));
        assertThat(actualPatient.getAddress(), is(address1));
        assertThat(actualPatient.getDateOfBirth(), is(birthdate));
        assertThat(actualPatient.getGender(), is(gender));
    }

    private void setAddress(Person person, String address1) {
        final PersonAddress address = new PersonAddress();
        address.setAddress1(address1);
        final HashSet<PersonAddress> addresses = new HashSet<PersonAddress>();
        addresses.add(address);
        person.setAddresses(addresses);
    }

    @Test
    public void shouldSetPatientsNameWithPreferredNameIfAPreferredNameIsFound() {
        final Person person = new Person();
        final String first = "First";
        final String last = "Last";
        String preferredName = "Preferred";
        final Date birthdate = new Date(1970, 3, 11);

        person.addName(new PersonName(first, null, last));
        final PersonName preferred = new PersonName(preferredName, null, last);
        preferred.setPreferred(true);
        person.addName(preferred);
        final org.openmrs.Patient mrsPatient = new org.openmrs.Patient(person);

        when(mockPatientService.savePatient(Matchers.<org.openmrs.Patient>any())).thenReturn(mrsPatient);
        final Patient actualPatient = openMRSPatientAdaptor.savePatient(new Patient(first, null, last, preferredName, birthdate, null, null));

        assertThat(actualPatient.getFirstName(), is(preferredName));
    }

    @Test
    public void shouldUsePreferredNameWhenGiven() {
        final String first = "First";
        final String last = "Last";
        String preferredName = "Preferred";
        final Date birthdate = new Date(1970, 3, 11);


        final org.openmrs.Patient mockPatient = mock(org.openmrs.Patient.class);
        final HashSet<PersonName> names = new HashSet<PersonName>();
        names.add(new PersonName(1212));
        when(mockPatient.getNames()).thenReturn(names);
        when(mockPatientService.savePatient(Matchers.<org.openmrs.Patient>any())).thenReturn(mockPatient);

        openMRSPatientAdaptor.savePatient(new Patient(first, null, last, preferredName, birthdate, null, null));

        ArgumentCaptor<org.openmrs.Patient> captor = ArgumentCaptor.forClass(org.openmrs.Patient.class);
        verify(mockPatientService).savePatient(captor.capture());
        final org.openmrs.Patient actualPatient = captor.getValue();

        assertThat(actualPatient.getNames().size(), is(2));
        final ArrayList<String> actualNames = new ArrayList<String>();
        for (PersonName name : actualPatient.getNames()) {
            actualNames.add(name.getGivenName());
        }
        assertTrue(actualNames.contains(preferredName));
        assertTrue(actualNames.contains(first));
    }
}