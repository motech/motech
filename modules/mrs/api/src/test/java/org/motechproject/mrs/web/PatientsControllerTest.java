package org.motechproject.mrs.web;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.util.MrsImplementationManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.testing.utils.rest.RestTestUtil.jsonMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class PatientsControllerTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", Charset.forName("UTF-8"));

    private MockMvc controller;

    @Mock
    private MRSPatient patient1;

    @Mock
    private MRSPatient patient2;

    @Mock
    private MRSPerson person1;

    @Mock
    private MRSPerson person2;

    @Mock
    private MRSFacility facility1;

    @Mock
    private MRSFacility facility2;

    @Mock
    private MrsImplementationManager mrsImplementationDataProvider;

    @Mock
    private MRSPatientAdapter patientAdapter;

    @Mock
    private MRSFacilityAdapter facilityAdapter;

    @InjectMocks
    private PatientsController patientsController = new PatientsController();

    @Before
    public void setUp() {
        initMocks(this);
        initPatients();
        controller = MockMvcBuilders.standaloneSetup(patientsController).build();
    }

    @Test
    public void shouldReturnListOfPatients() throws Exception {
        when(mrsImplementationDataProvider.getPatientAdapter()).thenReturn(patientAdapter);
        when(patientAdapter.getAllPatients()).thenReturn(asList(patient1, patient2));

        final String expectedJson = readJson("patientList.json");

        controller.perform(
                get("/patients")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedJson))
        );

        verify(mrsImplementationDataProvider).getPatientAdapter();
        verify(patientAdapter).getAllPatients();
    }

    @Test
    public void shouldReturnPatient() throws Exception {
        when(mrsImplementationDataProvider.getPatientAdapter()).thenReturn(patientAdapter);
        when(patientAdapter.getPatientByMotechId("id1")).thenReturn(patient1);

        final String expectedJson = readJson("patient.json");

        controller.perform(
                get("/patients/id1")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
                content().string(jsonMatcher(expectedJson))
        );

        verify(mrsImplementationDataProvider).getPatientAdapter();
        verify(patientAdapter).getPatientByMotechId("id1");
    }

    @Test
    public void shouldUpdatePatient() throws Exception {
        when(mrsImplementationDataProvider.getPatientAdapter()).thenReturn(patientAdapter);
        when(mrsImplementationDataProvider.getFacilityAdapter()).thenReturn(facilityAdapter);
        when(facilityAdapter.getFacility("fid1")).thenReturn(facility1);

        controller.perform(
                put("/patients/id1")
                        .body(readJson("patient.json").getBytes("UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.OK.value())
        );

        verify(mrsImplementationDataProvider).getPatientAdapter();
        verify(mrsImplementationDataProvider).getFacilityAdapter();
        verify(facilityAdapter).getFacility("fid1");

        ArgumentCaptor<MRSPatientDto> captor = ArgumentCaptor.forClass(MRSPatientDto.class);
        verify(patientAdapter).updatePatient(captor.capture());
        assertPatient(captor.getValue());
    }

    @Test
    public void shouldCreatePatient() throws Exception {
        when(mrsImplementationDataProvider.getPatientAdapter()).thenReturn(patientAdapter);
        when(mrsImplementationDataProvider.getFacilityAdapter()).thenReturn(facilityAdapter);
        when(facilityAdapter.getFacility("fid1")).thenReturn(facility1);

        controller.perform(
            post("/patients/id1")
                .body(readJson("patient.json").getBytes("UTF-8"))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.OK.value())
        );

        verify(mrsImplementationDataProvider).getPatientAdapter();
        verify(mrsImplementationDataProvider).getFacilityAdapter();
        verify(facilityAdapter).getFacility("fid1");

        ArgumentCaptor<MRSPatientDto> captor = ArgumentCaptor.forClass(MRSPatientDto.class);
        verify(patientAdapter).savePatient(captor.capture());
        assertPatient(captor.getValue());
    }

    @Test
    public void shouldReturn400ForInvalidFacility() throws Exception {
        when(mrsImplementationDataProvider.getPatientAdapter()).thenReturn(patientAdapter);
        when(mrsImplementationDataProvider.getFacilityAdapter()).thenReturn(facilityAdapter);
        when(facilityAdapter.getFacility("fid1")).thenReturn(null);

        controller.perform(
                post("/patients/id1")
                        .body(readJson("patient.json").getBytes("UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.BAD_REQUEST.value())
        ).andExpect(
                content().string("key:mrs.noSuchFacility")
        );

        verify(mrsImplementationDataProvider).getFacilityAdapter();
        verify(mrsImplementationDataProvider, never()).getPatientAdapter();
        verify(facilityAdapter).getFacility("fid1");
        verify(patientAdapter, never()).savePatient(any(MRSPatient.class));
    }

    private void assertPatient(MRSPatient patient) {
        assertEquals("id1", patient.getMotechId());
        assertEquals("Patient", patient.getPerson().getFirstName());
        assertEquals("One", patient.getPerson().getLastName());
        assertEquals("MiddleName", patient.getPerson().getMiddleName());
        assertEquals("Addr1", patient.getPerson().getAddress());
        assertFalse(patient.getPerson().isDead());
        assertEquals("male", patient.getPerson().getGender());
        assertEquals("fid1", patient.getFacility().getFacilityId());
        assertEquals("PL", patient.getFacility().getCountry());
        assertEquals("Facility1", patient.getFacility().getName());
    }

    private void initPatients() {
        when(patient1.getMotechId()).thenReturn("id1");
        when(patient2.getMotechId()).thenReturn("id2");
        when(patient1.getPerson()).thenReturn(person1);
        when(patient2.getPerson()).thenReturn(person2);
        when(patient1.getFacility()).thenReturn(facility1);
        when(patient2.getFacility()).thenReturn(facility2);

        when(person1.getFirstName()).thenReturn("Patient");
        when(person2.getFirstName()).thenReturn("Patient");
        when(person1.getLastName()).thenReturn("One");
        when(person2.getLastName()).thenReturn("Two");
        when(person1.isDead()).thenReturn(false);
        when(person2.isDead()).thenReturn(true);
        when(person1.getMiddleName()).thenReturn("MiddleName");
        when(person1.getAddress()).thenReturn("Addr1");
        when(person2.getAddress()).thenReturn("Addr2");
        when(person1.getGender()).thenReturn("male");
        when(person2.getGender()).thenReturn("female");

        when(facility1.getFacilityId()).thenReturn("fid1");
        when(facility2.getFacilityId()).thenReturn("fid2");
        when(facility1.getCountry()).thenReturn("PL");
        when(facility2.getCountry()).thenReturn("US");
        when(facility1.getName()).thenReturn("Facility1");
        when(facility2.getName()).thenReturn("Facility2");
    }

    private String readJson(String filename) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("rest/" + filename)) {
            return IOUtils.toString(in);
        }
    }
}
