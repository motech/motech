package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationOpenMrsWS.xml" })
public class MRSFacilityAdapterImplIT {

    @Autowired
    MRSFacilityAdapter adapter;

    @Autowired
    RestOperations restOperations;

    @Value("${openmrs.url}")
    String openmrsUrl;

    @Autowired
    OpenMrsUrlHolder holder;

    @Test
    public void shouldCreateNewFacility() {
        MRSFacility facility = adapter.saveFacility(makeFacility());

        assertNotNull(facility.getId());

        deleteFacilityByUuid(facility.getId());
    }

    private MRSFacility makeFacility() {
        MRSFacility facility = new MRSFacility("Portland Clinic", "USA", "Northeast", "Cumberland", "Maine");
        return facility;
    }

    private void deleteFacilityByUuid(String uuid) {
        restOperations.delete(openmrsUrl + "/ws/rest/v1/location/{uuid}" + "?purge", uuid);
    }

    @Test
    public void shouldGetAllFacilities() {
        MRSFacility facility = adapter.saveFacility(makeFacility());
        List<MRSFacility> facilities = adapter.getFacilities();

        assertTrue(facilities.size() > 0);

        deleteFacilityByUuid(facility.getId());
    }

    @Test
    public void shouldFindFacilityByName() {
        MRSFacility facility = adapter.saveFacility(makeFacility());
        List<MRSFacility> unknownFacility = adapter.getFacilities(facility.getName());

        assertTrue(unknownFacility.size() == 1);
        assertEquals(facility.getName(), unknownFacility.get(0).getName());

        deleteFacilityByUuid(facility.getId());
    }

    @Test
    public void shouldFindFacilityByUuid() {
        MRSFacility facility = adapter.saveFacility(makeFacility());
        MRSFacility searchFacility = adapter.getFacility(facility.getId());

        assertNotNull(searchFacility);
        assertEquals(facility.getId(), searchFacility.getId());

        deleteFacilityByUuid(facility.getId());
    }
}
