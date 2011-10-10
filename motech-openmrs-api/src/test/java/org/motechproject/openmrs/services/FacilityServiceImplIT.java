package org.motechproject.openmrs.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.services.Facility;
import org.motechproject.mrs.services.FacilityService;
import org.motechproject.openmrs.OpenMRSAbstractSessionContext;
import org.motechproject.openmrs.OpenMRSAuthenticationProviderForTests;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationOpenmrsAPI.xml", "classpath*:openMRSTestSessionScope.xml"})
public class FacilityServiceImplIT extends OpenMRSAbstractSessionContext {
    @Autowired
    FacilityService facilityService;

    @Autowired
    LocationService mrsLocationService;

    @Autowired
    OpenMRSAuthenticationProviderForTests openMRSAuthenticationProvider;

    @Before
    public void setUp() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("openmrs");
        startSession();
        startRequest();
        openMRSAuthenticationProvider.authenticate(resourceBundle.getString("openmrs.admin.username"),
                resourceBundle.getString("openmrs.admin.password"));
    }

    @After
    public void tearDown() {
        endRequest();
        endSession();
    }

    @Ignore
    @Test
    public void testSaveLocation() {
        Facility facility = new Facility("my facility", "ghana", "region", "district", "kaseena");
        final Facility savedFacility = facilityService.saveFacility(facility);
        authorizeAndRollback(new DirtyData() {
            public void rollback() {
                mrsLocationService.purgeLocation(mrsLocationService.getLocation(Integer.parseInt(savedFacility.getId())));
            }
        });
        assertNotNull(savedFacility);
        assertEquals(facility.getCountry(), savedFacility.getCountry());
        assertEquals(facility.getCountyDistrict(), savedFacility.getCountyDistrict());
        assertEquals(facility.getRegion(), savedFacility.getRegion());
        assertEquals(facility.getStateProvince(), savedFacility.getStateProvince());
        assertEquals(facility.getName(), savedFacility.getName());
    }

    @Ignore
    @Test
    public void testGetLocations() {
        int size = facilityService.getFacilities().size();
        String facilityName = "my facility";
        Facility facility = new Facility(facilityName, "ghana", "region", "district", "kaseena");
        final Facility savedFacility = facilityService.saveFacility(facility);
        List<Facility> facilities = facilityService.getFacilities();
        authorizeAndRollback(new DirtyData() {
            public void rollback() {
                mrsLocationService.purgeLocation(mrsLocationService.getLocation(Integer.parseInt(savedFacility.getId())));
            }
        });
        int alteredSize = facilities.size();
        List<Facility> addedFacilities = select(facilities, having(on(Facility.class).getName(), equalTo(facilityName)));
        assertEquals(size + 1, alteredSize);
        assertEquals(Arrays.asList(savedFacility), addedFacilities);

    }

    private void authorizeAndRollback(DirtyData dirtyData) {
        Context.openSession();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("openmrs");
        Context.authenticate(resourceBundle.getString("openmrs.admin.username"), resourceBundle.getString("openmrs.admin.password"));
        dirtyData.rollback();
        Context.closeSession();
    }

    interface DirtyData {
        void rollback();
    }
}
