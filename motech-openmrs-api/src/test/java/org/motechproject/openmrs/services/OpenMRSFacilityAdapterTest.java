package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.mrs.model.Facility;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSFacilityAdapterTest {

    @Mock
    LocationService mockLocationService;

    MRSFacilityAdapter MRSFacilityAdapter = new OpenMRSFacilityAdapter();

    @Before
    public void setUp() {
        initMocks(this);
        ReflectionTestUtils.setField(MRSFacilityAdapter, "locationService", mockLocationService);
    }

    @Test
    public void testSaveLocation() {
        String name = "name";
        String country = "country";
        String region = "region";
        String district = "district";
        String province = "province";
        Facility facility = new Facility(name, country, region, district, province);
        Location location = mock(Location.class);
        when(mockLocationService.saveLocation(Matchers.<Location>any())).thenReturn(location);

        MRSFacilityAdapter.saveFacility(facility);

        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(mockLocationService).saveLocation(locationCaptor.capture());
        Location actualLocation = locationCaptor.getValue();
        assertEquals(province, actualLocation.getStateProvince());
        assertEquals(name, actualLocation.getName());
        assertEquals(country, actualLocation.getCountry());
        assertEquals(district, actualLocation.getCountyDistrict());
        assertEquals(region, actualLocation.getAddress6());
    }

    private Location createALocation(Integer id, String name, String country, String region, String district, String province){
        Location location = new Location();
        location.setId(id);
        location.setName(name);
        location.setCountry(country);
        location.setAddress6(region);
        location.setCountyDistrict(district);
        location.setStateProvince(province);
        return location;
    }

    @Test
    public void testGetFacilities() {
        Integer locationId = 100;
        String name = "name";
        String country = "country";
        String region = "region";
        String district = "district";
        String province = "province";

        List<Location> locations = Arrays.asList(this.createALocation(locationId, name, country, region, district, province));
        when(mockLocationService.getAllLocations()).thenReturn(locations);
        List<Facility> returnedFacilities = MRSFacilityAdapter.getFacilities();
        assertEquals(Arrays.asList(new Facility(String.valueOf(locationId), name, country, region, district, province)), returnedFacilities);
    }

    @Test
    public void testGetAFacilityByName() {
        Integer locationId = 100;
        String name = "name";
        String country = "country";
        String region = "region";
        String district = "district";
        String province = "province";

        Location location = this.createALocation(locationId, name, country, region, district, province);
        when(mockLocationService.getLocations(name)).thenReturn(Arrays.asList(location));
        final List<Facility> facilities = MRSFacilityAdapter.getFacilities(name);
        assertEquals(Arrays.asList(new Facility(String.valueOf(locationId), name, country, region, district, province)), facilities);
    }

    @Test
    public void testGetAFacilityByNameForANonExistentFacililty() {
        String name = "name";
        when(mockLocationService.getLocation(name)).thenReturn(null);
        assertEquals(Collections.EMPTY_LIST, MRSFacilityAdapter.getFacilities(name));
    }
}
