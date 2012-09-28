package org.motechproject.openmrs.ws.impl.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractFacilityAdapterIT {

    @Autowired
    private MRSFacilityAdapter facilityAdapter;

    @Test
    public void shouldSaveFacility() {
        MRSFacility facilty = new MRSFacility("Test Facility", "US", "New York", "First", "Manhattan");
        MRSFacility created = facilityAdapter.saveFacility(facilty);

        assertNotNull(created.getId());
    }

    @Test
    public void shouldFindMultipleFacilities() {
        List<MRSFacility> facilities = facilityAdapter.getFacilities();

        assertTrue(facilities.size() > 0);
    }

    @Test
    public void shouldFindSingleFacilityByName() {
        List<MRSFacility> facilities = facilityAdapter.getFacilities("Clinic 1");

        assertEquals(1, facilities.size());
    }

    @Test
    public void shouldFindFacilityById() {
        MRSFacility facility = facilityAdapter.getFacility("Clinic 2");
        MRSFacility persisted = facilityAdapter.getFacility(facility.getId());

        assertNotNull(persisted);
    }
}
