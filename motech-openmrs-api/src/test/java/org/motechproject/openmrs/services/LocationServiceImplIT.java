package org.motechproject.openmrs.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.services.Location;
import org.motechproject.mrs.services.LocationService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationOpenmrsAPI.xml"})
public class LocationServiceImplIT {
    @Autowired
    private LocationService locationService;

    @Autowired
    private org.openmrs.api.LocationService mrsLocationService;

    @Test
    public void testSaveLocation() {
        Context.openSession();
        Context.authenticate("admin", "P@ssw0rd");

        Location location = locationService.saveLocation("foo", "bar");
        org.openmrs.Location mrsLocation = mrsLocationService.getLocation("foo");
        assertNotNull(mrsLocation);
        assertEquals(location.getName(), mrsLocation.getName());
        Context.closeSession();
    }

}
