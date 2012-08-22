package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.util.DateUtil;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.springframework.http.HttpStatus;

public class MRSObservationAdapterImplTest {

    @Mock
    private MRSPatientAdapterImpl patientAdapter;

    @Mock
    private RestClient client;

    @Mock
    private OpenMrsUrlHolder urlHolder;

    private MRSObservationAdapterImpl impl;

    @Before
    public void setUp() {
        initMocks(this);
        impl = new MRSObservationAdapterImpl(patientAdapter, client, urlHolder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullMotechId() {
        impl.findObservation(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyMotechId() {
        impl.findObservation("", null);
    }

    @Test
    public void shouldReturnNullOnNoPatientFound() {
        when(patientAdapter.getPatientByMotechId("555")).thenReturn(null);
        MRSObservation obs = impl.findObservation("555", "concept");

        assertNull(obs);
    }

    @Test
    public void shouldReturnNullOnEmptyResults() throws HttpException {
        when(patientAdapter.getPatientByMotechId("555")).thenReturn(new MRSPatient("AAA"));
        when(client.getJson(null)).thenReturn("{\"results\":[]}");

        MRSObservation obs = impl.findObservation("555", "concept");

        assertNull(obs);
    }

    @Test
    public void shouldReadObservationJson() throws IOException, HttpException, ParseException {
        String obsListResult = TestUtils.parseJsonFileAsString("json/observation-list-response.json");
        when(patientAdapter.getPatientByMotechId("555")).thenReturn(new MRSPatient("AAA"));
        when(client.getJson(null)).thenReturn(obsListResult);

        MRSObservation obs = impl.findObservation("555", "CCC");

        MRSObservation expected = makeExpectedObservation();

        assertEquals(expected, obs);
    }

    private MRSObservation makeExpectedObservation() throws ParseException {
        return new MRSObservation("OOO", DateUtil.parseOpenMrsDate("1962-01-01T00:00:00.000+0000"), "CCC", "VVV");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnVoidWithNullObservation() throws ObservationNotFoundException {
        impl.voidObservation(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnVoidWithEmptyIdObservation() throws ObservationNotFoundException {
        impl.voidObservation(new MRSObservation("", null, null, null), null, null);
    }

    @Test(expected = ObservationNotFoundException.class)
    public void shouldThrowObservationNotFound() throws HttpException, ObservationNotFoundException {
        doThrow(new HttpException(null, HttpStatus.NOT_FOUND)).when(client).delete(null);
        impl.voidObservation(new MRSObservation("AAA", null, null, null), null, null);
    }

    @Test
    public void shouldVoidObservation() throws ObservationNotFoundException, HttpException {
        impl.voidObservation(new MRSObservation("AAA", null, null, null), null, null);

        verify(client).delete(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnObservationsWithNullMotechId() throws ObservationNotFoundException {
        impl.findObservations(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnObservationsWIthEmptyMotechId() throws ObservationNotFoundException {
        impl.findObservations("", null);
    }

    @Test
    public void shouldParseObservationResult() throws IOException, HttpException, ParseException {
        String obsListResult = TestUtils.parseJsonFileAsString("json/observation-list-response.json");
        when(patientAdapter.getPatientByMotechId("555")).thenReturn(new MRSPatient("AAA"));
        when(client.getJson(null)).thenReturn(obsListResult);

        List<MRSObservation> observations = impl.findObservations("555", "CCC");

        MRSObservation expected = makeExpectedObservation();

        assertEquals(1, observations.size());
        assertEquals(expected, observations.get(0));
    }

    @Test
    public void shouldFilterObservationWithoutConceptName() throws IOException, HttpException, ParseException {
        String obsListResult = TestUtils.parseJsonFileAsString("json/observation-list-response2.json");
        when(patientAdapter.getPatientByMotechId("555")).thenReturn(new MRSPatient("AAA"));
        when(client.getJson(null)).thenReturn(obsListResult);

        List<MRSObservation> observations = impl.findObservations("555", "CCC");

        MRSObservation expected = makeExpectedObservation();

        assertEquals(1, observations.size());
    }
}
