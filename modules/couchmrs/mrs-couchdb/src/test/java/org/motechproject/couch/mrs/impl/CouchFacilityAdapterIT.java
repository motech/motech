package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.motechproject.couch.mrs.repository.impl.AllCouchFacilitiesImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchFacilityAdapterIT extends SpringIntegrationTest {

    @Autowired
    private CouchFacilityAdapter facilityAdapter;

    @Autowired
    private AllCouchFacilities allFacilities;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    @Autowired
    @Qualifier("couchPatientDatabaseConnector")
    CouchDbConnector connector;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Test
    public void shouldSaveAFacilityAndRetrieveByName() throws MRSCouchException, InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, EventKeys.CREATED_NEW_FACILITY_SUBJECT);
        CouchFacility facility = new CouchFacility();
        facility.setFacilityId("facilityId");
        facility.setName("facilityName");

        CouchFacility facility2 = new CouchFacility();
        facility2.setFacilityId("facilityId2");
        facility2.setName("facilityName");

        synchronized (lock) {
            facilityAdapter.saveFacility(facility);
            lock.wait(60000);
        }

        assertEquals(facility.getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(facility.getName(), mrsListener.eventParameters.get(EventKeys.FACILITY_NAME));
        assertTrue(mrsListener.created);
        mrsListener.created = false;

        synchronized (lock) {
            facilityAdapter.saveFacility(facility2);
            lock.wait(60000);
        }

        List<CouchFacility> facilities = facilityAdapter.getFacilities("facilityName");

        assertEquals(asList("facilityId", "facilityId2"), extract(facilities, on(CouchFacility.class).getFacilityId()));
        assertEquals(facility2.getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(facility2.getName(), mrsListener.eventParameters.get(EventKeys.FACILITY_NAME));
        assertTrue(mrsListener.created);

        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchFacilitiesImpl) allFacilities).removeAll();
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
