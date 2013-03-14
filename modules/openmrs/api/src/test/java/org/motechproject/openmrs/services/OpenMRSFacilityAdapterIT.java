package org.motechproject.openmrs.services;

import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.openmrs.api.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;

public class OpenMRSFacilityAdapterIT extends OpenMRSIntegrationTestBase {
    @Autowired
    FacilityAdapter mrsFacilityAdapter;

    @Autowired
    LocationService mrsLocationService;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Test
    @Transactional(readOnly = true)
    public void testSaveLocation() throws InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_FACILITY_SUBJECT));
        Facility facility = new OpenMRSFacility("my facility", "ghana", "region", "district", "kaseena");
        final Facility savedFacility;

        synchronized (lock) {
            savedFacility = mrsFacilityAdapter.saveFacility(facility);
            lock.wait(60000);
        }

        assertNotNull(savedFacility);
        assertEquals(facility.getCountry(), savedFacility.getCountry());
        assertEquals(facility.getCountyDistrict(), savedFacility.getCountyDistrict());
        assertEquals(facility.getRegion(), savedFacility.getRegion());
        assertEquals(facility.getStateProvince(), savedFacility.getStateProvince());
        assertEquals(facility.getName(), savedFacility.getName());

        assertTrue(mrsListener.created);
        assertEquals(savedFacility.getName(), mrsListener.eventParameters.get(EventKeys.FACILITY_NAME));
        assertEquals(savedFacility.getCountry(), mrsListener.eventParameters.get(EventKeys.FACILITY_COUNTRY));
        assertEquals(savedFacility.getRegion(), mrsListener.eventParameters.get(EventKeys.FACILITY_REGION));
        assertEquals(savedFacility.getCountyDistrict(), mrsListener.eventParameters.get(EventKeys.FACILITY_COUNTY_DISTRICT));
        assertEquals(savedFacility.getStateProvince(), mrsListener.eventParameters.get(EventKeys.FACILITY_STATE_PROVINCE));
        eventListenerRegistry.clearListenersForBean("mrsTestListener");

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

    public class MrsListener implements EventListener {

        private boolean created = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.CREATED_NEW_FACILITY_SUBJECT})
        public void handle(MotechEvent event) {
            created = true;
            eventParameters = event.getParameters();
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public String getIdentifier() {
            return "mrsTestListener";
        }
    }
}
