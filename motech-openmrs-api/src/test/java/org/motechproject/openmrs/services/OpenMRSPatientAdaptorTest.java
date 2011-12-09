package org.motechproject.openmrs.services;

import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.mrs.model.Facility;
import org.motechproject.mrs.model.Patient;
import org.motechproject.openmrs.IdentifierType;
import org.motechproject.openmrs.helper.PatientHelper;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSPatientAdaptorTest {

    @Mock
    private PatientService mockPatientService;
    @Mock
    private UserService mockUserService;
    @Mock
    private PersonService mockPersonService;
    @Mock
    private OpenMRSFacilityAdaptor mockFacilityAdapter;

    OpenMRSPatientAdaptor openMRSPatientAdaptor;
    PatientTestUtil patientTestUtil;

    @Before
    public void setUp() {
        initMocks(this);
        openMRSPatientAdaptor = new OpenMRSPatientAdaptor();
        patientTestUtil = new PatientTestUtil();
        ReflectionTestUtils.setField(openMRSPatientAdaptor, "patientService", mockPatientService);
        ReflectionTestUtils.setField(openMRSPatientAdaptor, "personService", mockPersonService);
        ReflectionTestUtils.setField(openMRSPatientAdaptor, "userService", mockUserService);
        ReflectionTestUtils.setField(openMRSPatientAdaptor, "facilityAdaptor", mockFacilityAdapter);
        ReflectionTestUtils.setField(openMRSPatientAdaptor, "patientHelper", new PatientHelper());
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
        final Facility facility = new Facility("1000", "name", "country", "region", "district", "province");

        final org.openmrs.Patient mrsPatient = patientTestUtil.setUpOpenMRSPatient(person, first, middle, last, address1, birthdate, birthdateEstimated, gender, facility);
        when(mockPatientService.savePatient(Matchers.<org.openmrs.Patient>any())).thenReturn(mrsPatient);
        when(mockFacilityAdapter.convertLocationToFacility(any(Location.class))).thenReturn(facility);

        final Patient actualPatient = openMRSPatientAdaptor.savePatient(new Patient(first, middle, last, null, birthdate, birthdateEstimated, gender, address1, facility));

        verify(mockPatientService).savePatient(Matchers.<org.openmrs.Patient>any());
        patientTestUtil.verifyReturnedPatient(first, middle, last, address1, birthdate, birthdateEstimated, gender, facility, actualPatient);
    }

    @Test
    public void shouldGetPatientById() {
        final Person person = new Person();
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthdate = new Date(1970, 3, 11);
        final boolean birthdateEstimated = true;
        final String gender = "male";
        final Facility facility = new Facility("1000", "name", "country", "region", "district", "province");

        final org.openmrs.Patient mrsPatient = patientTestUtil.setUpOpenMRSPatient(person, first, middle, last, address1, birthdate, birthdateEstimated, gender, facility);
        int patientId = 12;
        when(mockPatientService.getPatient(patientId)).thenReturn(mrsPatient);
        when(mockFacilityAdapter.convertLocationToFacility(any(Location.class))).thenReturn(facility);
        Patient returnedPatient = openMRSPatientAdaptor.getPatient(String.valueOf(patientId));

        verify(mockPatientService).getPatient(patientId);
        patientTestUtil.verifyReturnedPatient(first, middle, last, address1, birthdate, birthdateEstimated, gender, facility, returnedPatient);
    }

    @Test
    public void shouldGetPatientByMotechId() {
        final Person person = new Person();
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthdate = new Date(1970, 3, 11);
        final boolean birthdateEstimated = true;
        final String gender = "male";
        final Facility facility = new Facility("1000", "name", "country", "region", "district", "province");
        String motechId = "11";
        PatientIdentifierType motechIdType = mock(PatientIdentifierType.class);

        final org.openmrs.Patient mrsPatient = patientTestUtil.setUpOpenMRSPatient(person, first, middle, last, address1, birthdate, birthdateEstimated, gender, facility);
        List<PatientIdentifierType> idTypes = Arrays.asList(motechIdType);
        when(mockPatientService.getPatients(null, motechId, idTypes, true)).thenReturn(Arrays.asList(mrsPatient));
        when(mockPatientService.getPatientIdentifierTypeByName(IdentifierType.IDENTIFIER_MOTECH_ID.getName())).thenReturn(motechIdType);
        when(mockFacilityAdapter.convertLocationToFacility(any(Location.class))).thenReturn(facility);
        Patient returnedPatient = openMRSPatientAdaptor.getPatientByMotechId(motechId);

        patientTestUtil.verifyReturnedPatient(first, middle, last, address1, birthdate, birthdateEstimated, gender, facility, returnedPatient);
    }

    @Test
    public void shouldReturnNullGetPatientByMotechId() {
        String motechId = "11";
        PatientIdentifierType motechIdType = mock(PatientIdentifierType.class);

        List<PatientIdentifierType> idTypes = Arrays.asList(motechIdType);
        when(mockPatientService.getPatients(null, motechId, idTypes, true)).thenReturn(ListUtils.EMPTY_LIST);
        when(mockPatientService.getPatientIdentifierTypeByName(IdentifierType.IDENTIFIER_MOTECH_ID.getName())).thenReturn(motechIdType);
        assertNull(openMRSPatientAdaptor.getPatientByMotechId(motechId));

    }

    @Test
    public void shouldReturnNullIfPatientByIdIsNotFound() {
        int patientId = 12;
        when(mockPatientService.getPatient(patientId)).thenReturn(null);
        assertNull(openMRSPatientAdaptor.getPatient(String.valueOf(patientId)));
    }

    @Test
    public void shouldUsePreferredNameWhenGiven() {
        final String first = "First";
        final String last = "Last";
        String preferredName = "Preferred";
        final Date birthdate = new Date(1970, 3, 11);
        Boolean birthDateEstimated = true;
        final Facility facility = new Facility("1000", "name", "country", "region", "district", "province");

        final org.openmrs.Patient mockPatient = mock(org.openmrs.Patient.class);
        final HashSet<PersonName> names = new HashSet<PersonName>();
        names.add(new PersonName(1212));
        when(mockPatient.getNames()).thenReturn(names);
        when(mockPatientService.savePatient(Matchers.<org.openmrs.Patient>any())).thenReturn(mockPatient);

        openMRSPatientAdaptor.savePatient(new Patient(first, null, last, preferredName, birthdate, birthDateEstimated, null, null, facility));

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

    @Test
    public void shouldRetrieveOpenMrsIdentifierTypeGivenTheIdentifierName() {
        PatientIdentifierType patientIdentiferTypeMock = mock(PatientIdentifierType.class);
        when(mockPatientService.getPatientIdentifierTypeByName(IdentifierType.IDENTIFIER_MOTECH_ID.getName())).thenReturn(patientIdentiferTypeMock);
        assertThat(openMRSPatientAdaptor.getPatientIdentifierType(IdentifierType.IDENTIFIER_MOTECH_ID), is(patientIdentiferTypeMock));
    }

    @Test
    public void shouldGetOpenMrsPatientById(){
        org.openmrs.Patient mrsPatient = mock(org.openmrs.Patient.class);
        Integer patientId = 1000;

        when(mockPatientService.getPatient(patientId)).thenReturn(mrsPatient);
        org.openmrs.Patient returnedPatient = openMRSPatientAdaptor.getOpenMrsPatient(String.valueOf(patientId));
        assertThat(returnedPatient, is(equalTo(mrsPatient)));
    }

    public static class PatientTestUtil {
        public org.openmrs.Patient setUpOpenMRSPatient(Person person, String first, String middle, String last, String address1, Date birthdate, boolean birthdateEstimated, String gender, Facility facility) {
            PersonName personName = new PersonName(first, middle, last);
            person.addName(personName);
            setAddress(person, address1);
            final org.openmrs.Patient mrsPatient = new org.openmrs.Patient(person);
            mrsPatient.setBirthdate(birthdate);
            mrsPatient.setBirthdateEstimated(birthdateEstimated);
            mrsPatient.setGender(gender);
            mrsPatient.addIdentifier(new PatientIdentifier(null, null, new Location(Integer.parseInt(facility.getId()))));
            return mrsPatient;
        }

        private void setAddress(Person person, String address1) {
            final PersonAddress address = new PersonAddress();
            address.setAddress1(address1);
            final HashSet<PersonAddress> addresses = new HashSet<PersonAddress>();
            addresses.add(address);
            person.setAddresses(addresses);
        }

        public void verifyReturnedPatient(String first, String middle, String last, String address1, Date birthdate, Boolean birthDateEstimated, String gender, Facility facility, Patient actualPatient) {
            assertThat(actualPatient.getFirstName(), is(first));
            assertThat(actualPatient.getLastName(), is(last));
            assertThat(actualPatient.getMiddleName(), is(middle));
            assertThat(actualPatient.getAddress(), is(address1));
            assertThat(actualPatient.getDateOfBirth(), is(birthdate));
            assertThat(actualPatient.getGender(), is(gender));
            assertThat(actualPatient.getFacility(), is(equalTo(facility)));
        }
    }
}