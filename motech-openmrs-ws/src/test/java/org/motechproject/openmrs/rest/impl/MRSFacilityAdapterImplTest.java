package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.model.Location;
import org.motechproject.openmrs.rest.util.JsonUtils;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;

import com.google.gson.JsonElement;

public class MRSFacilityAdapterImplTest {

    private static final String FACILITY_STATE = "Facility State";
    private static final String FACILITY_DISTRICT = "Facility District";
    private static final String REGION = "Region";
    private static final String FACILITY_COUNTRY = "Facility Country";
    private static final String LOCATION_NAME = "Location Name";
    private static final String UUID = "AAABBBCCC";

    @Mock
    private RestClient client;

    @Mock
    private OpenMrsUrlHolder urlHolder;

    private MRSFacilityAdapterImpl impl;

    @Before
    public void setUp() {
        initMocks(this);
        impl = new MRSFacilityAdapterImpl(client, urlHolder);
    }

    @Test
    public void shouldParseAllLocations() throws HttpException, IOException {
        List<MRSFacility> expected = buildExpectedFacilityList();

        String json = TestUtils.parseJsonFileAsString("json/location-list-response.json");
        when(client.getJson(any(URI.class))).thenReturn(json);

        List<MRSFacility> mrsFacilities = impl.getFacilities();

        assertEquals(expected, mrsFacilities);
    }

    private List<MRSFacility> buildExpectedFacilityList() {
        List<MRSFacility> facilities = new ArrayList<MRSFacility>();
        MRSFacility facility = makeFacility(true);
        facilities.add(facility);
        return facilities;
    }

    private MRSFacility makeFacility(boolean includeUuid) {
        MRSFacility facility;
        if (includeUuid) {
            facility = new MRSFacility(UUID, LOCATION_NAME, FACILITY_COUNTRY, REGION, FACILITY_DISTRICT, FACILITY_STATE);
        } else {
            facility = new MRSFacility(null, LOCATION_NAME, FACILITY_COUNTRY, REGION, FACILITY_DISTRICT, FACILITY_STATE);
        }
        return facility;
    }

    @Test
    public void shouldParseAllLocationsByName() throws HttpException, IOException {
        List<MRSFacility> expected = buildExpectedFacilityList();

        String json = TestUtils.parseJsonFileAsString("json/location-list-response.json");
        when(client.getJson(any(URI.class))).thenReturn(json);

        List<MRSFacility> mrsFacilities = impl.getFacilities("Test");

        assertEquals(expected, mrsFacilities);
    }

    @Test
    public void shouldParseSingleLocation() throws IOException, HttpException {
        MRSFacility expected = makeFacility(true);

        String json = TestUtils.parseJsonFileAsString("json/location-response.json");

        when(client.getJson(null)).thenReturn(json);

        MRSFacility result = impl.getFacility("Test");

        assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnEmptyFacilityId() {
        impl.getFacility("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullFacilityId() {
        impl.getFacility(null);
    }

    @Test
    public void shouldNotIncludeUuidOnCreate() throws IOException, HttpException {
        String emptyObject = "{}";

        when(client.postForJson(any(URI.class), any(String.class))).thenReturn(emptyObject);

        impl.saveFacility(makeFacility(true));

        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        verify(client).postForJson(any(URI.class), sentJson.capture());

        Location location = (Location) JsonUtils.readJson(sentJson.getValue(), Location.class);

        assertNull(location.getUuid());
    }

    @Test
    public void shouldSendCorrectLocationJson() throws HttpException, IOException {
        String emptyObject = "{}";

        when(client.postForJson(any(URI.class), any(String.class))).thenReturn(emptyObject);

        impl.saveFacility(makeFacility(false));

        JsonElement expected = TestUtils.parseJsonFile("json/location-create.json");
        ArgumentCaptor<String> sentJson = ArgumentCaptor.forClass(String.class);
        verify(client).postForJson(any(URI.class), sentJson.capture());

        JsonElement sent = TestUtils.parseJsonString(sentJson.getValue());

        assertEquals(expected, sent);
    }
}
