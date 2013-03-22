package org.motechproject.openmrs.ws.impl.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.openmrs.model.OpenMRSFacility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractFacilityAdapterIT {

    @Autowired
    private MRSFacilityAdapter facilityAdapter;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Test
    public void shouldSaveFacility() throws InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_FACILITY_SUBJECT));

        OpenMRSFacility facilty = new OpenMRSFacility("Test Facility", "US", "New York", "First", "Manhattan");
        OpenMRSFacility created;

        synchronized (lock) {
            created = (OpenMRSFacility) facilityAdapter.saveFacility(facilty);
            lock.wait(60000);
        }

        assertNotNull(created.getId());

        assertTrue(mrsListener.created);
        assertEquals(created.getName(), mrsListener.eventParameters.get(EventKeys.FACILITY_NAME));
        assertEquals(created.getCountry(), mrsListener.eventParameters.get(EventKeys.FACILITY_COUNTRY));
        assertEquals(created.getRegion(), mrsListener.eventParameters.get(EventKeys.FACILITY_REGION));
        assertEquals(created.getCountyDistrict(), mrsListener.eventParameters.get(EventKeys.FACILITY_COUNTY_DISTRICT));
        assertEquals(created.getStateProvince(), mrsListener.eventParameters.get(EventKeys.FACILITY_STATE_PROVINCE));
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    @Test
    public void shouldFindMultipleFacilities() {
        List<? extends MRSFacility> facilities = facilityAdapter.getFacilities();

        assertTrue(facilities.size() > 0);
    }

    @Test
    public void shouldFindSingleFacilityByName() {
        List<? extends MRSFacility> facilities = facilityAdapter.getFacilities("Clinic 1");

        assertEquals(1, facilities.size());
    }

    @Test
    public void shouldFindFacilityById() {
        OpenMRSFacility facility = (OpenMRSFacility) facilityAdapter.getFacility("Clinic 2");
        OpenMRSFacility persisted = (OpenMRSFacility) facilityAdapter.getFacility(facility.getFacilityId());

        assertNotNull(persisted);
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
