package org.motechproject.openmrs.services;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.model.*;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class OpenMRSEncounterAdaptorTest {
    OpenMRSEncounterAdaptor encounterAdaptor;

    @Mock
    private OpenMRSUserAdaptor mockOpenMrsUserAdaptor;
    @Mock
    private OpenMRSFacilityAdaptor mockOpenMrsFacilityAdaptor;
    @Mock
    private OpenMRSPatientAdaptor mockOpenMrsPatientAdaptor;
    @Mock
    private OpenMRSObservationAdaptor mockOpenMrsObservationAdaptor;
    @Mock
    private EncounterService mockEncounterService;
    @Mock
    private OpenMRSPersonAdaptor mockOpenMRSPersonAdaptor;

    @Before
    public void setUp() {
        initMocks(this);
        encounterAdaptor = new OpenMRSEncounterAdaptor();
        ReflectionTestUtils.setField(encounterAdaptor, "openMrsUserAdaptor", mockOpenMrsUserAdaptor);
        ReflectionTestUtils.setField(encounterAdaptor, "openMrsFacilityAdaptor", mockOpenMrsFacilityAdaptor);
        ReflectionTestUtils.setField(encounterAdaptor, "openMrsPatientAdaptor", mockOpenMrsPatientAdaptor);
        ReflectionTestUtils.setField(encounterAdaptor, "openMrsObservationAdaptor", mockOpenMrsObservationAdaptor);
        ReflectionTestUtils.setField(encounterAdaptor, "encounterService", mockEncounterService);
        ReflectionTestUtils.setField(encounterAdaptor, "openMRSPersonAdaptor", mockOpenMRSPersonAdaptor);
    }

    @Test
    public void shouldConvertMrsEncounterToOpenMrsEncounter() {
        String staffId = "333";
        String facilityId = "99";
        String patientId = "199";
        String providerId ="100";
        MRSUser staff = new MRSUser().id(staffId);
        MRSFacility facility = new MRSFacility(facilityId);
        MRSPatient patient = new MRSPatient(patientId);
        Set<MRSObservation> observations = Collections.EMPTY_SET;

        String encounterType = "encounterType";
        String encounterId = "100";

        Date encounterDate = Calendar.getInstance().getTime();
        MRSPerson provider = new MRSPerson().id(providerId);
        MRSEncounter mrsEncounter = new MRSEncounter(encounterId,provider, staff, facility, encounterDate, patient, observations, encounterType);

        Location expectedLocation = mock(Location.class);
        when(mockOpenMrsFacilityAdaptor.getLocation(facilityId)).thenReturn(expectedLocation);

        org.openmrs.Patient expectedPatient = mock(org.openmrs.Patient.class);
        when(mockOpenMrsPatientAdaptor.getOpenMrsPatient(patientId)).thenReturn(expectedPatient);

        org.openmrs.User expectedCreator = mock(org.openmrs.User.class);
        Person expectedPerson = mock(Person.class);
        when(mockOpenMrsUserAdaptor.getOpenMrsUserById(staffId)).thenReturn(expectedCreator);
        when(expectedCreator.getPerson()).thenReturn(expectedPerson);

        Person expectedProvider = mock(Person.class);
        when(mockOpenMRSPersonAdaptor.getPersonById(providerId)).thenReturn(expectedProvider);

        EncounterType expectedEncounterType = mock(EncounterType.class);
        when(mockEncounterService.getEncounterType(encounterType)).thenReturn(expectedEncounterType);

        Set<Obs> expectedObservations = new HashSet<Obs>();
        Encounter expectedEncounter = mock(Encounter.class);

        when(mockOpenMrsObservationAdaptor.createOpenMRSObservationsForEncounter(observations, expectedEncounter, expectedPatient, expectedLocation, expectedCreator)).thenReturn(expectedObservations);

        Encounter returnedEncounter = encounterAdaptor.mrsToOpenMRSEncounter(mrsEncounter);

        assertThat(returnedEncounter.getLocation(), is(equalTo(expectedLocation)));
        assertThat(returnedEncounter.getPatient(), is(equalTo(expectedPatient)));
        assertThat(returnedEncounter.getCreator(), is(equalTo(expectedCreator)));
        assertThat(returnedEncounter.getProvider(), is(equalTo(expectedProvider)));
        assertThat(returnedEncounter.getEncounterDatetime(), is(equalTo(encounterDate)));
        assertThat(returnedEncounter.getEncounterType(), is(equalTo(expectedEncounterType)));
        assertThat(returnedEncounter.getObs(), is(equalTo(expectedObservations)));
    }

    @Test
    public void shouldConvertOpenMRSEncounterToMRSEncounter() {

        String encounterTypeName = "ANCVisit";
        EncounterType openMrsEncounterType = new EncounterType(encounterTypeName, "Ghana Antenatal Care (ANC) Visit");
        HashSet<Obs> openMrsObservations = new HashSet<Obs>();
        org.openmrs.Patient mockOpenMRSPatient = mock(org.openmrs.Patient.class);
        org.openmrs.User mockOpenMRSUser = mock(org.openmrs.User.class);
        Location mockLocation = mock(Location.class);
        int encounterId = 12;
        Date encounterDate = new LocalDate(2011, 12, 12).toDate();

        Encounter openMrsEncounter = createOpenMRSEncounter(encounterDate, openMrsEncounterType, openMrsObservations, mockOpenMRSPatient, mockOpenMRSUser, mockLocation, encounterId);

        MRSUser mrsStaff = mock(MRSUser.class);
        MRSFacility mrsfacility = mock(MRSFacility.class);
        MRSPatient mrspatient = mock(MRSPatient.class);


        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>() {{
            MRSObservation mockObservation = mock(MRSObservation.class);
            add(mockObservation);
        }};
        MRSPerson mrsPerson = mock(MRSPerson.class);
        Person mockOpenMRSPerson = mock(Person.class);

        when(mockOpenMrsUserAdaptor.openMrsToMrsUser(mockOpenMRSUser)).thenReturn(mrsStaff);
        when(mockOpenMRSUser.getPerson()).thenReturn(mockOpenMRSPerson);
        when(mockOpenMRSPersonAdaptor.openMRSToMRSPerson(mockOpenMRSPerson)).thenReturn(mrsPerson);
        when(mockOpenMrsFacilityAdaptor.convertLocationToFacility(mockLocation)).thenReturn(mrsfacility);
        when(mockOpenMrsPatientAdaptor.getMrsPatient(mockOpenMRSPatient)).thenReturn(mrspatient);
        when(mockOpenMrsObservationAdaptor.convertOpenMRSToMRSObservations(openMrsObservations)).thenReturn(mrsObservations);

        MRSEncounter mrsEncounter = encounterAdaptor.openmrsToMrsEncounter(openMrsEncounter);

        assertThat(mrsEncounter.getId(), is(equalTo(Integer.toString(encounterId))));
        assertThat(mrsEncounter.getEncounterType(), is(equalTo(encounterTypeName)));
        assertThat(mrsEncounter.getCreator(), is(equalTo(mrsStaff)));
        assertThat(mrsEncounter.getPatient(), is(equalTo(mrspatient)));
        assertThat(mrsEncounter.getDate(), is(equalTo(encounterDate)));
        assertThat(mrsEncounter.getFacility(), is(equalTo(mrsfacility)));
        assertThat(mrsEncounter.getObservations(), is(equalTo(mrsObservations)));
    }

    private Encounter createOpenMRSEncounter(Date encounterDate, EncounterType openMrsEncounterType, HashSet<Obs> openMrsObservations, org.openmrs.Patient mockOpenMRSPatient, org.openmrs.User mockOpenMRSUser, Location mockLocation, int encounterId) {
        Encounter openMrsEncounter = new Encounter();
        openMrsEncounter.setId(encounterId);
        openMrsEncounter.setObs(openMrsObservations);
        openMrsEncounter.setEncounterType(openMrsEncounterType);
        openMrsEncounter.setCreator(mockOpenMRSUser);
        openMrsEncounter.setLocation(mockLocation);
        openMrsEncounter.setPatient(mockOpenMRSPatient);
        openMrsEncounter.setEncounterDatetime(encounterDate);
        return openMrsEncounter;
    }

    @Test
    public void shouldSaveAnEncounter() {
        OpenMRSEncounterAdaptor encounterAdaptorSpy = spy(encounterAdaptor);
        Encounter openMrsEncounter = mock(Encounter.class);
        MRSEncounter mrsEncounter = mock(MRSEncounter.class);
        Encounter savedOpenMrsEncounter = mock(Encounter.class);
        MRSEncounter savedMrsEncounter = mock(MRSEncounter.class);

        doReturn(openMrsEncounter).when(encounterAdaptorSpy).mrsToOpenMRSEncounter(mrsEncounter);
        when(mockEncounterService.saveEncounter(openMrsEncounter)).thenReturn(savedOpenMrsEncounter);
        doReturn(savedMrsEncounter).when(encounterAdaptorSpy).openmrsToMrsEncounter(savedOpenMrsEncounter);

        MRSEncounter returnedMRSEncounterAfterSaving = encounterAdaptorSpy.createEncounter(mrsEncounter);
        assertThat(returnedMRSEncounterAfterSaving, is(equalTo(savedMrsEncounter)));
    }
    
    @Test
    public void shouldFetchLatestEncounterForMotechId() {
        String encounterType = "Encounter Type";
        String motechId = "1234567";
        encounterAdaptor.getLatestEncounterByPatientMotechId(motechId, encounterType);
        verify(mockEncounterService).getEncountersByPatientIdentifier(motechId);
    }
         
    @Test
    public void shouldFetchTheLatestEncounterIfThereAreMoreThanOneEncounters() {
        String encounterName = "Encounter Type";
        String motechId = "1234567";
        Encounter encounter1 = new Encounter(1);
        EncounterType encounterType1 = new EncounterType();
        encounterType1.setName(encounterName);
        encounter1.setEncounterType(encounterType1);
        Date encounterDatetime1 = new Date(1999, 11, 2);
        encounter1.setEncounterDatetime(encounterDatetime1);
        Encounter encounter2 = new Encounter(2);
        encounter2.setEncounterType(encounterType1);
        encounter2.setEncounterDatetime(new Date(1998,11,6));
        
        when(mockEncounterService.getEncountersByPatientIdentifier(motechId)).thenReturn(Arrays.asList(encounter1,encounter2));
        MRSEncounter actualEncounter = encounterAdaptor.getLatestEncounterByPatientMotechId(motechId, encounterName);
        
        assertThat(actualEncounter.getDate(), is(equalTo(encounterDatetime1)));
    }
    
    @Test
    public void shouldReturnNullIfEncounterIsNotFound() {
        final String motechId = "1332";
        final String encounterName = "patientRegistration";
        when(mockEncounterService.getEncountersByPatientIdentifier(motechId)).thenReturn(Collections.<Encounter>emptyList());
        MRSEncounter patientRegistration = encounterAdaptor.getLatestEncounterByPatientMotechId(motechId, encounterName);
        assertNull(patientRegistration);
    }
}
