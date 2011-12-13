package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.model.*;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    @Before
    public void setUp() {
        initMocks(this);
        encounterAdaptor = new OpenMRSEncounterAdaptor();
        ReflectionTestUtils.setField(encounterAdaptor, "openMrsUserAdaptor", mockOpenMrsUserAdaptor);
        ReflectionTestUtils.setField(encounterAdaptor, "openMrsFacilityAdaptor", mockOpenMrsFacilityAdaptor);
        ReflectionTestUtils.setField(encounterAdaptor, "openMrsPatientAdaptor", mockOpenMrsPatientAdaptor);
        ReflectionTestUtils.setField(encounterAdaptor, "openMrsObservationAdaptor", mockOpenMrsObservationAdaptor);
        ReflectionTestUtils.setField(encounterAdaptor, "encounterService", mockEncounterService);
    }


    @Test
    public void shouldConvertMrsEncounterToOpenMrsEncounter() {
        MRSUser staff = mock(MRSUser.class);
        String staffId = "333";
        when(staff.getId()).thenReturn(staffId);

        MRSFacility facility = mock(MRSFacility.class);
        String facilityId = "99";
        when(facility.getId()).thenReturn(facilityId);

        Date encounterDate = new Date(2001, 1, 1);
        MRSPatient patient = mock(MRSPatient.class);
        String patientId = "199";
        when(patient.getId()).thenReturn(patientId);

        Set<MRSObservation> observations = mock(Set.class);

        String encounterType = "encounterType";
        String encounterId = "100";

        MRSEncounter mrsEncounter = new MRSEncounter(encounterId, staff, facility, encounterDate, patient, observations, encounterType);

        Location expectedLocation = mock(Location.class);
        when(mockOpenMrsFacilityAdaptor.getLocation(facilityId)).thenReturn(expectedLocation);

        org.openmrs.Patient expectedPatient = mock(org.openmrs.Patient.class);
        when(mockOpenMrsPatientAdaptor.getOpenMrsPatient(patientId)).thenReturn(expectedPatient);

        org.openmrs.User expectedCreator = mock(org.openmrs.User.class);
        when(mockOpenMrsUserAdaptor.getOpenMrsUserByUserName(staffId)).thenReturn(expectedCreator);

        EncounterType expectedEncounterType = mock(EncounterType.class);
        when(mockEncounterService.getEncounterType(encounterType)).thenReturn(expectedEncounterType);

        Set<Obs> expectedObservations = new HashSet<Obs>();
        Encounter expectedEncounter = mock(Encounter.class);

        when(mockOpenMrsObservationAdaptor.createOpenMRSObservationsForEncounter(observations, expectedEncounter, expectedPatient, expectedLocation, expectedCreator)).thenReturn(expectedObservations);

        Encounter returnedEncounter = encounterAdaptor.mrsToOpenMrsEncounter(mrsEncounter);

        assertThat(returnedEncounter.getEncounterId(), is(equalTo(100)));
        assertThat(returnedEncounter.getLocation(), is(equalTo(expectedLocation)));
        assertThat(returnedEncounter.getPatient(), is(equalTo(expectedPatient)));
        assertThat(returnedEncounter.getCreator(), is(equalTo(expectedCreator)));
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
        Date encounterDate = new Date(2011, 12, 12);

        Encounter openMrsEncounter = createOpenMRSEncounter(encounterDate, openMrsEncounterType, openMrsObservations, mockOpenMRSPatient, mockOpenMRSUser, mockLocation, encounterId);

        MRSUser mrsStaff = mock(MRSUser.class);
        MRSFacility mrsfacility = mock(MRSFacility.class);
        MRSPatient mrspatient = mock(MRSPatient.class);


        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>() {{
            MRSObservation mockObservation = mock(MRSObservation.class);
            add(mockObservation);
        }};

        when(mockOpenMrsUserAdaptor.openMrsToMrsUser(mockOpenMRSUser)).thenReturn(mrsStaff);
        when(mockOpenMrsFacilityAdaptor.convertLocationToFacility(mockLocation)).thenReturn(mrsfacility);
        when(mockOpenMrsPatientAdaptor.getMrsPatient(mockOpenMRSPatient)).thenReturn(mrspatient);
        when(mockOpenMrsObservationAdaptor.convertOpenMRSToMRSObservations(openMrsObservations)).thenReturn(mrsObservations);

        MRSEncounter mrsEncounter = encounterAdaptor.openMrsToMrsEncounter(openMrsEncounter);

        assertThat(mrsEncounter.getId(), is(equalTo(Integer.toString(encounterId))));
        assertThat(mrsEncounter.getEncounterType(), is(equalTo(encounterTypeName)));
        assertThat(mrsEncounter.getStaff(), is(equalTo(mrsStaff)));
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

        doReturn(openMrsEncounter).when(encounterAdaptorSpy).mrsToOpenMrsEncounter(mrsEncounter);
        when(mockEncounterService.saveEncounter(openMrsEncounter)).thenReturn(savedOpenMrsEncounter);
        doReturn(savedMrsEncounter).when(encounterAdaptorSpy).openMrsToMrsEncounter(savedOpenMrsEncounter);

        MRSEncounter returnedMRSEncounterAfterSaving = encounterAdaptorSpy.saveEncounter(mrsEncounter);
        assertThat(returnedMRSEncounterAfterSaving, is(equalTo(savedMrsEncounter)));
    }
}
