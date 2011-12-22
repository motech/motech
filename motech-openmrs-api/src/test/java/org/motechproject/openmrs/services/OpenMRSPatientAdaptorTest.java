package org.motechproject.openmrs.services;

import org.apache.commons.collections.ListUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.IdentifierType;
import org.motechproject.openmrs.helper.PatientHelper;
import org.motechproject.openmrs.util.PatientTestUtil;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
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
    @Autowired
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
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final boolean birthdateEstimated = true;
        final String gender = "male";
        String facilityId = "1000";
        final MRSFacility facility = new MRSFacility(facilityId, "name", "country", "region", "district", "province");
        String motechId = "1234567";
        final Location location = new Location(Integer.parseInt(facilityId));

        final org.openmrs.Patient openMRSPatient = patientTestUtil.setUpOpenMRSPatient(person, first, middle, last, address1, birthDate, birthdateEstimated, gender, facility, motechId);
        when(mockPatientService.savePatient(Matchers.<org.openmrs.Patient>any())).thenReturn(openMRSPatient);
        when(mockFacilityAdapter.getLocation(facilityId)).thenReturn(location);

        when(mockFacilityAdapter.convertLocationToFacility(any(Location.class))).thenReturn(facility);

        MRSPerson mrsPerson = new MRSPerson().firstName(first).middleName(middle).lastName(last).birthDateEstimated(birthdateEstimated).dateOfBirth(birthDate).address(address1).gender(gender);
        MRSPatient mrsPatient = new MRSPatient(motechId, mrsPerson, facility);
        final MRSPatient actualPatient = openMRSPatientAdaptor.savePatient(mrsPatient);

        ArgumentCaptor<org.openmrs.Patient> openMrsPatientArgumentCaptor = ArgumentCaptor.forClass(org.openmrs.Patient.class);

        verify(mockPatientService).savePatient(openMrsPatientArgumentCaptor.capture());
        patientTestUtil.assertEqualsForOpenMrsPatient(openMrsPatientArgumentCaptor.getValue(), openMRSPatient);

        patientTestUtil.verifyReturnedPatient(first, middle, last, address1, birthDate, birthdateEstimated, gender, facility, actualPatient, motechId);
    }

    @Test
    public void shouldGetPatientById() {
        final Person person = new Person();
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final boolean birthDateEstimated = true;
        final String gender = "male";
        final MRSFacility facility = new MRSFacility("1000", "name", "country", "region", "district", "province");
        String motechId = "1234567";

        final org.openmrs.Patient mrsPatient = patientTestUtil.setUpOpenMRSPatient(person, first, middle, last, address1, birthDate, birthDateEstimated, gender, facility, motechId);
        int patientId = 12;
        when(mockPatientService.getPatient(patientId)).thenReturn(mrsPatient);
        when(mockFacilityAdapter.convertLocationToFacility(any(Location.class))).thenReturn(facility);
        MRSPatient returnedPatient = openMRSPatientAdaptor.getPatient(String.valueOf(patientId));

        verify(mockPatientService).getPatient(patientId);
        patientTestUtil.verifyReturnedPatient(first, middle, last, address1, birthDate, birthDateEstimated, gender, facility, returnedPatient, motechId);
    }

    @Test
    public void shouldGetPatientByMotechId() {
        final Person person = new Person();
        final String first = "First";
        final String middle = "Middle";
        final String last = "Last";
        final String address1 = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final boolean birthDateEstimated = true;
        final String gender = "male";
        final MRSFacility facility = new MRSFacility("1000", "name", "country", "region", "district", "province");
        String motechId = "11";
        PatientIdentifierType motechIdType = mock(PatientIdentifierType.class);

        final org.openmrs.Patient mrsPatient = patientTestUtil.setUpOpenMRSPatient(person, first, middle, last, address1, birthDate, birthDateEstimated, gender, facility, motechId);
        List<PatientIdentifierType> idTypes = Arrays.asList(motechIdType);
        when(mockPatientService.getPatients(null, motechId, idTypes, true)).thenReturn(Arrays.asList(mrsPatient));
        when(mockPatientService.getPatientIdentifierTypeByName(IdentifierType.IDENTIFIER_MOTECH_ID.getName())).thenReturn(motechIdType);
        when(mockFacilityAdapter.convertLocationToFacility(any(Location.class))).thenReturn(facility);
        MRSPatient returnedPatient = openMRSPatientAdaptor.getPatientByMotechId(motechId);

        patientTestUtil.verifyReturnedPatient(first, middle, last, address1, birthDate, birthDateEstimated, gender, facility, returnedPatient, motechId);
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
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        Boolean birthDateEstimated = true;
        final MRSFacility facility = new MRSFacility("1000", "name", "country", "region", "district", "province");
        String motechId = "1234567";

        final org.openmrs.Patient mockPatient = mock(org.openmrs.Patient.class);
        final HashSet<PersonName> names = new HashSet<PersonName>();
        names.add(new PersonName(1212));
        when(mockPatient.getNames()).thenReturn(names);
        when(mockPatientService.savePatient(Matchers.<org.openmrs.Patient>any())).thenReturn(mockPatient);

        MRSPerson mrsPerson = new MRSPerson().firstName(first).lastName(last).preferredName(preferredName).birthDateEstimated(birthDateEstimated).dateOfBirth(birthDate);
        MRSPatient mrsPatient = new MRSPatient(motechId, mrsPerson, facility);
        openMRSPatientAdaptor.savePatient(mrsPatient);

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

    @Test
    public void shouldSearchByPatientNameOrId() {
        OpenMRSPatientAdaptor openMRSPatientAdaptorSpy = spy(openMRSPatientAdaptor);
        String name = "name";
        String id = "1000";
        Patient openMrsPatient1 = mock(Patient.class);
        Patient openMrsPatient2 = mock(Patient.class);
        List<Patient> patientsMatchingSearchQuery = Arrays.asList(openMrsPatient1, openMrsPatient2);
        PatientIdentifierType identifierTypeMock = mock(PatientIdentifierType.class);
        when(mockPatientService.getPatientIdentifierTypeByName(IdentifierType.IDENTIFIER_MOTECH_ID.getName())).thenReturn(identifierTypeMock);
        when(mockPatientService.getPatients(name, id, Arrays.asList(identifierTypeMock), false)).thenReturn(patientsMatchingSearchQuery);

        MRSPatient mrsPatient1 = new MRSPatient(null, new MRSPerson().firstName("Zef"), null);
        MRSPatient mrsPatient2 = new MRSPatient(null, new MRSPerson().firstName("Abc"), null);
        doReturn(mrsPatient1).when(openMRSPatientAdaptorSpy).getMrsPatient(openMrsPatient1);
        doReturn(mrsPatient2).when(openMRSPatientAdaptorSpy).getMrsPatient(openMrsPatient2);

        List<MRSPatient> returnedPatients = openMRSPatientAdaptorSpy.search(name, id);
        assertThat(returnedPatients, is(equalTo(Arrays.asList(mrsPatient2, mrsPatient1))));

    }
}