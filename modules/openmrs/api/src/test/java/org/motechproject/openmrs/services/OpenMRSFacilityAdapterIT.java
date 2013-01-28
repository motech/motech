package org.motechproject.openmrs.services;

import org.junit.Test;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.openmrs.api.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.equalTo;

public class OpenMRSFacilityAdapterIT extends OpenMRSIntegrationTestBase {
    @Autowired
    FacilityAdapter mrsFacilityAdapter;

    @Autowired
    LocationService mrsLocationService;

    @Test
    @Transactional(readOnly = true)
    public void testSaveLocation() {
        Facility facility = new OpenMRSFacility("my facility", "ghana", "region", "district", "kaseena");
        final Facility savedFacility = mrsFacilityAdapter.saveFacility(facility);
        assertNotNull(savedFacility);
        assertEquals(facility.getCountry(), savedFacility.getCountry());
        assertEquals(facility.getCountyDistrict(), savedFacility.getCountyDistrict());
        assertEquals(facility.getRegion(), savedFacility.getRegion());
        assertEquals(facility.getStateProvince(), savedFacility.getStateProvince());
        assertEquals(facility.getName(), savedFacility.getName());
    }

    @Test
    @Transactional(readOnly = true)
    public void testIdempotencyWhileSavingLocation() {
        Facility facility = new OpenMRSFacility("my facility", "ghana", "region", "district", "kaseena");
        final Facility savedFacility = mrsFacilityAdapter.saveFacility(facility);
        assertNotNull(savedFacility);
        final Facility duplicateFacility = mrsFacilityAdapter.saveFacility(facility);

        assertEquals(savedFacility.getFacilityId(), duplicateFacility.getFacilityId());
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetLocations() {
        int size = mrsFacilityAdapter.getFacilities().size();
        String facilityName = "my facility";
        Facility facility = new OpenMRSFacility(facilityName, "ghana", "region", "district", "kaseena");
        final OpenMRSFacility savedFacility = (OpenMRSFacility) mrsFacilityAdapter.saveFacility(facility);
        List<? extends Facility> facilities = mrsFacilityAdapter.getFacilities();
        int alteredSize = facilities.size();
        List<? extends Facility> addedFacilities = select(facilities, having(on(OpenMRSFacility.class).getName(), equalTo(facilityName)));
        assertEquals(size + 1, alteredSize);
        assertEquals(Arrays.asList(savedFacility), addedFacilities);

    }

    @Test
    @Transactional(readOnly = true)
    public void testGetLocationsByName() {
        String facilityName = "my facility";
        Facility facility = new OpenMRSFacility(facilityName, "ghana", "region", "district", "kaseena");
        final Facility savedFacility = mrsFacilityAdapter.saveFacility(facility);
        final List<? extends Facility> facilities = mrsFacilityAdapter.getFacilities(facilityName);
        assertEquals(Arrays.asList(savedFacility), facilities);
    }
}
