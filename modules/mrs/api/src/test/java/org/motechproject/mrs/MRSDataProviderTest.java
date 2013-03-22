package org.motechproject.mrs;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.MotechObject;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSPersonAdapter;
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
    private MRSPatientAdapter patientAdapter;

    @Mock
    private MRSFacilityAdapter facilityAdapter;

    @Mock
    private MRSPersonAdapter personAdapter;

    @Mock
    private MRSPatient patient;

    @Mock
    private MRSFacility facility;

    @Mock
    private MRSPerson person;

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
        String clazz = MRSFacility.class.getSimpleName();
        HashMap<String, String> fields = new HashMap<>();

        // when
        Object object = provider.lookup(clazz, fields);

        // then
        assertNull(object);
    }

    @Test
    public void shouldReturnNullWhenListIsNull() {
        // given
        String patientClass = MRSPatient.class.getSimpleName();
        String facilityClass = MRSFacility.class.getSimpleName();
        String personClass = MRSPerson.class.getSimpleName();

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
        String patientClass = MRSPatient.class.getSimpleName();
        String facilityClass = MRSFacility.class.getSimpleName();
        String personClass = MRSPerson.class.getSimpleName();

        provider.setFacilityAdapters(new ArrayList<MRSFacilityAdapter>());
        provider.setPatientAdapters(new ArrayList<MRSPatientAdapter>());
        provider.setPersonAdapters(new ArrayList<MRSPersonAdapter>());

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
        String patientClass = MRSPatient.class.getSimpleName();
        String facilityClass = MRSFacility.class.getSimpleName();
        String personClass = MRSPerson.class.getSimpleName();

        provider.setPatientAdapters(Arrays.asList(patientAdapter));
        provider.setFacilityAdapters(Arrays.asList(facilityAdapter));
        provider.setPersonAdapters(Arrays.asList(personAdapter));

        // when
        MRSPatient patient = (MRSPatient) provider.lookup(patientClass, lookupFields);
        MRSFacility facility = (MRSFacility) provider.lookup(facilityClass, lookupFields);
        MRSPerson person = (MRSPerson) provider.lookup(personClass, lookupFields);

        // then
        assertEquals(this.patient, patient);
        assertEquals(this.facility, facility);
        assertEquals(this.person, person);
    }
}
