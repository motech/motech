package org.motechproject.openmrs.ws.impl.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.services.FacilityAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractFacilityAdapterIT {

    @Autowired
    private FacilityAdapter facilityAdapter;

    @Test
    public void shouldSaveFacility() {
        OpenMRSFacility facilty = new OpenMRSFacility("Test Facility", "US", "New York", "First", "Manhattan");
        OpenMRSFacility created = (OpenMRSFacility) facilityAdapter.saveFacility(facilty);

        assertNotNull(created.getId());
    }

    @Test
    public void shouldFindMultipleFacilities() {
        List<? extends Facility> facilities = facilityAdapter.getFacilities();

        assertTrue(facilities.size() > 0);
    }

    @Test
    public void shouldFindSingleFacilityByName() {
        List<? extends Facility> facilities = facilityAdapter.getFacilities("Clinic 1");

        assertEquals(1, facilities.size());
    }

    @Test
    public void shouldFindFacilityById() {
        OpenMRSFacility facility = (OpenMRSFacility) facilityAdapter.getFacility("Clinic 2");
        OpenMRSFacility persisted = (OpenMRSFacility) facilityAdapter.getFacility(facility.getId());

        assertNotNull(persisted);
    }
}
