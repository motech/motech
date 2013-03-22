package org.motechproject.mrs.services;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.domain.MRSUser;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.MRSFacilityDto;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.model.MRSPersonDto;
import org.motechproject.mrs.model.MRSProviderDto;
import org.motechproject.mrs.model.MRSUserDto;
import org.motechproject.mrs.services.impl.MrsActionProxyServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MrsActionProxyServiceTest {

    public static final String PATIENT_ID = "1234";
    public static final String MOTECH_ID = "5678";
    public static final String PERSON_ID = "6758";
    public static final String FIRST_NAME = "Bob";
    public static final String MIDDLE_NAME = "Tom";
    public static final String LAST_NAME = "last name";
    public static final String PREFERRED_NAME = "BT";
    public static final String ADDRESS = "address";
    public static final DateTime DATE = DateTime.now();
    public static final String BIRTH_DATE_ESTIMATED = "true";
    public static final int AGE = 0;
    public static final String GENDER = "male";
    public static final String DEAD = "false";
    public static final String NAME = "name";
    public static final String COUNTRY = "country";
    public static final String REGION = "region";
    public static final String COUNTRY_DISTRICT = "country district";
    public static final String STATE_PROVINCE = "state province";
    public static final String FACILITY_ID = "4567";
    public static final String CONCEPT_NAME = "concept name";
    public static final String COMMENT = "comment";
    public static final String PROVIDER_ID = "providerId";
    public static final String USER_ID = "userId";
    public static final String VALUE = "value";
    public static final String TYPE = "type";
    public static final String USER_NAME = "user";

    private MrsActionProxyServiceImpl mrsEventHandler;

    @Mock
    private MRSPatientAdapter patientAdapter;
    @Mock
    private MRSPersonAdapter personAdapter;
    @Mock
    private MRSEncounterAdapter encounterAdapter;
    @Mock
    private MRSFacilityAdapter facilityAdapter;
    @Mock
    private MRSProviderAdapter providerAdapter;
    @Mock
    private MRSUserAdapter userAdapter;


    private List<MRSPerson> persons = new ArrayList<>();
    private MRSFacility facility;
    private MRSPerson person;
    private MRSProvider provider;
    private MRSPatient patient;
    private MRSUser user;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mrsEventHandler = new MrsActionProxyServiceImpl();
        mrsEventHandler.setPatientAdapters(Arrays.asList(patientAdapter));
        mrsEventHandler.setPersonAdapters(Arrays.asList(personAdapter));
        mrsEventHandler.setEncounterAdapters(Arrays.asList(encounterAdapter));
        mrsEventHandler.setFacilityAdapters(Arrays.asList(facilityAdapter));
        mrsEventHandler.setProviderAdapters(Arrays.asList(providerAdapter));
        mrsEventHandler.setUserAdapters(Arrays.asList(userAdapter));
    }

    @Test
    public void shouldCreatePatientFromEvent() {
        setAllData();
        when(facilityAdapter.getFacility(FACILITY_ID)).thenReturn(facility);
        doReturn(persons).when(personAdapter).findByPersonId(PERSON_ID);
        mrsEventHandler.createPatient(PATIENT_ID, MOTECH_ID, FACILITY_ID, PERSON_ID);
        ArgumentCaptor<MRSPatient> patientArgumentCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        verify(patientAdapter).savePatient(patientArgumentCaptor.capture());
        MRSPatient patient = patientArgumentCaptor.getValue();
        assertEquals(FACILITY_ID, patient.getFacility().getFacilityId());
        assertEquals(MOTECH_ID, patient.getMotechId());
        assertEquals(PATIENT_ID, patient.getPatientId());
        assertEquals(PERSON_ID, patient.getPerson().getPersonId());
    }

    @Test
    public void shouldUpdatePatientFromEvent() {
        setAllData();
        when(facilityAdapter.getFacility(FACILITY_ID)).thenReturn(facility);
        doReturn(persons).when(personAdapter).findByPersonId(PERSON_ID);
        mrsEventHandler.updatePatient(PATIENT_ID, MOTECH_ID, FACILITY_ID, PERSON_ID);
        ArgumentCaptor<MRSPatient> patientArgumentCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        verify(patientAdapter).updatePatient(patientArgumentCaptor.capture());
        MRSPatient patient = patientArgumentCaptor.getValue();
        assertEquals(FACILITY_ID, patient.getFacility().getFacilityId());
        assertEquals(MOTECH_ID, patient.getMotechId());
        assertEquals(PATIENT_ID, patient.getPatientId());
        assertEquals(PERSON_ID, patient.getPerson().getPersonId());
    }

    @Test
    public void shouldDeceasePatientFromEvent() throws PatientNotFoundException {
        mrsEventHandler.deceasePatient(MOTECH_ID, CONCEPT_NAME, DATE, COMMENT);
        verify(patientAdapter).deceasePatient(MOTECH_ID, CONCEPT_NAME, DATE.toDate(), COMMENT);
    }

    @Test
    public void shouldCreateEncounterFromEvent() {
        setAllData();
        when(patientAdapter.getPatient(MOTECH_ID)).thenReturn(patient);
        when(facilityAdapter.getFacility(FACILITY_ID)).thenReturn(facility);
        when(userAdapter.getUserByUserName(USER_NAME)).thenReturn(user);
        when(providerAdapter.getProviderByProviderId(PROVIDER_ID)).thenReturn(provider);
        mrsEventHandler.createEncounter(MOTECH_ID, FACILITY_ID, USER_NAME, PROVIDER_ID, DATE, TYPE, null, null, null, null);
        ArgumentCaptor<MRSEncounter> captor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(encounterAdapter).createEncounter(captor.capture());
        MRSEncounter encounter = captor.getValue();
        assertEquals(PROVIDER_ID, encounter.getProvider().getProviderId());
        assertEquals(USER_ID, encounter.getCreator().getUserId());
        assertEquals(FACILITY_ID, encounter.getFacility().getFacilityId());
        assertEquals(DATE, encounter.getDate());
        assertEquals(PATIENT_ID, encounter.getPatient().getPatientId());
        assertEquals(TYPE, encounter.getEncounterType());
    }

    @Test
    public void shouldCreateFacilityFromEvent() {
        mrsEventHandler.createFacility(NAME, COUNTRY, REGION, COUNTRY_DISTRICT, STATE_PROVINCE);
        ArgumentCaptor<MRSFacility> captor = ArgumentCaptor.forClass(MRSFacility.class);
        verify(facilityAdapter).saveFacility(captor.capture());
        MRSFacility fac = captor.getValue();
        assertEquals(NAME, fac.getName());
        assertEquals(COUNTRY, fac.getCountry());
        assertEquals(REGION, fac.getRegion());
        assertEquals(COUNTRY_DISTRICT, fac.getCountyDistrict());
        assertEquals(STATE_PROVINCE, fac.getStateProvince());
    }

    @Test
    public void shouldCreatePersonFromEvent() {
        mrsEventHandler.createPerson(PERSON_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, PREFERRED_NAME, ADDRESS, DATE, BIRTH_DATE_ESTIMATED, AGE, GENDER, DEAD, DATE);
        ArgumentCaptor<MRSPerson> captor = ArgumentCaptor.forClass(MRSPerson.class);
        verify(personAdapter).addPerson(captor.capture());
        MRSPerson p = captor.getValue();
        assertEquals(PERSON_ID, p.getPersonId());
        assertEquals(FIRST_NAME, p.getFirstName());
        assertEquals(MIDDLE_NAME, p.getMiddleName());
        assertEquals(LAST_NAME, p.getLastName());
        assertEquals(PREFERRED_NAME, p.getPreferredName());
        assertEquals(ADDRESS, p.getAddress());
        assertEquals(DATE, p.getDateOfBirth());
        assertEquals(Boolean.valueOf(BIRTH_DATE_ESTIMATED), p.getBirthDateEstimated());
        assertEquals(AGE, p.getAge().intValue());
        assertEquals(GENDER, p.getGender());
        assertEquals(Boolean.parseBoolean(DEAD), p.isDead().booleanValue());
    }

    @Test
    public void shouldRemovePersonFromEvent() {
        mrsEventHandler.removePerson(PERSON_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, PREFERRED_NAME, ADDRESS, DATE, BIRTH_DATE_ESTIMATED, AGE, GENDER, DEAD, DATE);
        ArgumentCaptor<MRSPerson> captor = ArgumentCaptor.forClass(MRSPerson.class);
        verify(personAdapter).removePerson(captor.capture());
        MRSPerson p = captor.getValue();
        assertEquals(PERSON_ID, p.getPersonId());
        assertEquals(FIRST_NAME, p.getFirstName());
        assertEquals(MIDDLE_NAME, p.getMiddleName());
        assertEquals(LAST_NAME, p.getLastName());
        assertEquals(PREFERRED_NAME, p.getPreferredName());
        assertEquals(ADDRESS, p.getAddress());
        assertEquals(DATE, p.getDateOfBirth());
        assertEquals(Boolean.valueOf(BIRTH_DATE_ESTIMATED), p.getBirthDateEstimated());
        assertEquals(AGE, p.getAge().intValue());
        assertEquals(GENDER, p.getGender());
        assertEquals(Boolean.parseBoolean(DEAD), p.isDead().booleanValue());
    }

    @Test
    public void shouldUpdatePersonFromEvent() {
        mrsEventHandler.updatePerson(PERSON_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, PREFERRED_NAME, ADDRESS, DATE, BIRTH_DATE_ESTIMATED, AGE, GENDER, DEAD, DATE);
        ArgumentCaptor<MRSPerson> captor = ArgumentCaptor.forClass(MRSPerson.class);
        verify(personAdapter).updatePerson(captor.capture());
        MRSPerson p = captor.getValue();
        assertEquals(PERSON_ID, p.getPersonId());
        assertEquals(FIRST_NAME, p.getFirstName());
        assertEquals(MIDDLE_NAME, p.getMiddleName());
        assertEquals(LAST_NAME, p.getLastName());
        assertEquals(PREFERRED_NAME, p.getPreferredName());
        assertEquals(ADDRESS, p.getAddress());
        assertEquals(DATE, p.getDateOfBirth());
        assertEquals(Boolean.valueOf(BIRTH_DATE_ESTIMATED), p.getBirthDateEstimated());
        assertEquals(AGE, p.getAge().intValue());
        assertEquals(GENDER, p.getGender());
        assertEquals(Boolean.parseBoolean(DEAD), p.isDead().booleanValue());
    }

    private void setAllData() {
        person = new MRSPersonDto(PERSON_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, PREFERRED_NAME, ADDRESS, DATE, Boolean.valueOf(BIRTH_DATE_ESTIMATED), AGE, GENDER, Boolean.parseBoolean(DEAD), null, null);
        persons.add(person);
        facility = new MRSFacilityDto(NAME, COUNTRY, REGION, COUNTRY_DISTRICT, STATE_PROVINCE);
        facility.setFacilityId(FACILITY_ID);
        user = new MRSUserDto(USER_ID, MOTECH_ID, "Admin", USER_NAME, person);
        provider = new MRSProviderDto(PROVIDER_ID, person);
        patient = new MRSPatientDto(PATIENT_ID, facility, person, MOTECH_ID);
    }

}
