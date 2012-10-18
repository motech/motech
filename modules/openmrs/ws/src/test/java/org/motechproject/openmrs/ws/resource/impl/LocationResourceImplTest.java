package org.motechproject.openmrs.ws.resource.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.LocationListResult;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LocationResourceImplTest extends AbstractResourceImplTest {

    private LocationResourceImpl impl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        impl = new LocationResourceImpl(getClient(), getInstance());
    }

    @Test
    public void shouldCreateLocation() throws IOException, HttpException {
        Location loc = buildLocation();
        impl.createLocation(loc);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getClient()).postForJson(Mockito.any(URI.class), captor.capture());

        String expectedJson = readJsonFromFile("json/location-create.json");

        JsonElement expectedObj = getGson().fromJson(expectedJson, JsonObject.class);
        JsonElement sentObject = getGson().fromJson(captor.getValue(), JsonObject.class);

        assertEquals(expectedObj, sentObject);
    }

    private Location buildLocation() {
        Location loc = new Location();
        loc.setName("Location Name");
        loc.setStateProvince("Facility State");
        loc.setCountry("Facility Country");
        loc.setCountyDistrict("Facility District");
        loc.setAddress6("Region");

        return loc;
    }

    @Test
    public void shouldParseAllLocations() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/location-list-response.json"));

        LocationListResult result = impl.getAllLocations();

        assertEquals(1, result.getResults().size());
    }

    @Test
    public void shouldQueryForLocationByName() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/location-list-response.json"));

        LocationListResult result = impl.queryForLocationByName("Test");

        assertEquals(1, result.getResults().size());
    }

    @Test
    public void shouldParseSingleLocation() throws HttpException, IOException {
        Mockito.when(getClient().getJson(Mockito.any(URI.class))).thenReturn(
                readJsonFromFile("json/location-create.json"));

        Location location = impl.getLocationById("LLL");

        assertNotNull(location);
    }

}
