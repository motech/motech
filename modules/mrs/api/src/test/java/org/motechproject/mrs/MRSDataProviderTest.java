package org.motechproject.mrs;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.MotechObject;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.mrs.services.PersonAdapter;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MRSDataProviderTest {
    private static final String FIELD_KEY = "id";
    private static final String FIELD_VALUE = "12345";

    private static Map<String, String> lookupFields;


    @Mock
    private PatientAdapter patientAdapter;

    @Mock
    private FacilityAdapter facilityAdapter;

    @Mock
    private PersonAdapter personAdapter;

    @Mock
    private Patient patient;

    @Mock
    private Facility facility;

    @Mock
    private Person person;

    @Mock
    private ResourceLoader resourceLoader;

    private MRSDataProvider provider;

    @BeforeClass
    public static void setLookupFields() {
        lookupFields = new HashMap<>();
        lookupFields.put(FIELD_KEY, FIELD_VALUE);
    }

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(resourceLoader.getResource("task-data-provider.json")).thenReturn(null);
        when(patientAdapter.getPatient(FIELD_VALUE)).thenReturn(patient);
        when(facilityAdapter.getFacility(FIELD_VALUE)).thenReturn(facility);

        doReturn(Arrays.asList(person)).when(personAdapter).findByPersonId(FIELD_VALUE);

        provider = new MRSDataProvider(resourceLoader);
    }

    @Test
    public void shouldReturnNullWhenClassIsNotSupported() {
        // given
        String clazz = MotechObject.class.getSimpleName();

        // when
        Object object = provider.lookup(clazz, lookupFields);

        // then
        assertNull(object);
    }

    @Test
    public void shouldReturnNullWhenMapNotContainsSupportedField() {
        // given
        String clazz = Facility.class.getSimpleName();
        HashMap<String, String> fields = new HashMap<>();

        // when
        Object object = provider.lookup(clazz, fields);

        // then
        assertNull(object);
    }

    @Test
    public void shouldReturnNullWhenListIsNull() {
        // given
        String patientClass = Patient.class.getSimpleName();
        String facilityClass = Facility.class.getSimpleName();
        String personClass = Person.class.getSimpleName();

        // when
        Object patient = provider.lookup(patientClass, lookupFields);
        Object facility = provider.lookup(facilityClass, lookupFields);
        Object person = provider.lookup(personClass, lookupFields);

        // then
        assertNull(patient);
        assertNull(facility);
        assertNull(person);
    }

    @Test
    public void shouldReturnNullWhenListIsEmpty() {
        // given
        String patientClass = Patient.class.getSimpleName();
        String facilityClass = Facility.class.getSimpleName();
        String personClass = Person.class.getSimpleName();

        provider.setFacilityAdapters(new ArrayList<FacilityAdapter>());
        provider.setPatientAdapters(new ArrayList<PatientAdapter>());
        provider.setPersonAdapters(new ArrayList<PersonAdapter>());

        // when
        Object patient = provider.lookup(patientClass, lookupFields);
        Object facility = provider.lookup(facilityClass, lookupFields);
        Object person = provider.lookup(personClass, lookupFields);

        // then
        assertNull(patient);
        assertNull(facility);
        assertNull(person);
    }

    @Test
    public void shouldReturnObject() {
        // given
        String patientClass = Patient.class.getSimpleName();
        String facilityClass = Facility.class.getSimpleName();
        String personClass = Person.class.getSimpleName();

        provider.setPatientAdapters(Arrays.asList(patientAdapter));
        provider.setFacilityAdapters(Arrays.asList(facilityAdapter));
        provider.setPersonAdapters(Arrays.asList(personAdapter));

        // when
        Patient patient = (Patient) provider.lookup(patientClass, lookupFields);
        Facility facility = (Facility) provider.lookup(facilityClass, lookupFields);
        Person person = (Person) provider.lookup(personClass, lookupFields);

        // then
        assertEquals(this.patient, patient);
        assertEquals(this.facility, facility);
        assertEquals(this.person, person);
    }
}
