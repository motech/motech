package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.Initializer;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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

    @Before
    public void initialize() {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_FACILITY_SUBJECT, EventKeys.UPDATED_FACILITY_SUBJECT, EventKeys.DELETED_FACILITY_SUBJECT));
    }

    @Test
    public void shouldSaveAFacilityAndRetrieveByName() throws MRSCouchException, InterruptedException {
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
        assertFalse(mrsListener.updated);
        assertFalse(mrsListener.deleted);
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
    }

    @Test
    public void shouldUpdateFacility() throws MRSCouchException, InterruptedException {
        CouchFacility facility = new CouchFacility();
        facility.setFacilityId("facilityIdToUpdate");
        facility.setName("beforeUpdate");

        synchronized (lock) {
            facilityAdapter.saveFacility(facility);
            lock.wait(60000);
        }

        assertEquals(facility.getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(facility.getName(), mrsListener.eventParameters.get(EventKeys.FACILITY_NAME));
        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertFalse(mrsListener.deleted);

        facility.setName("afterUpdate");
        CouchFacility updatedFacility;

        synchronized (lock) {
            updatedFacility = (CouchFacility) facilityAdapter.updateFacility(facility);
            lock.wait(60000);
        }

        assertEquals(updatedFacility.getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(updatedFacility.getName(), mrsListener.eventParameters.get(EventKeys.FACILITY_NAME));
        assertEquals(updatedFacility.getName(), "afterUpdate");
        assertTrue(mrsListener.created);
        assertTrue(mrsListener.updated);
        assertFalse(mrsListener.deleted);
    }

    @Test
    public void shouldDeleteFacility() throws MRSCouchException, InterruptedException {
        CouchFacility facility = new CouchFacility();
        facility.setFacilityId("facilityIdTest");
        facility.setName("town");

        synchronized (lock) {
            facilityAdapter.saveFacility(facility);
            lock.wait(60000);
        }

        assertEquals(facility.getFacilityId(), mrsListener.eventParameters.get(EventKeys.FACILITY_ID));
        assertEquals(facility.getName(), mrsListener.eventParameters.get(EventKeys.FACILITY_NAME));

        synchronized (lock) {
            facilityAdapter.deleteFacility(facility.getFacilityId());
            lock.wait(60000);
        }

        assertNull(facilityAdapter.getFacility("facilityIdTest"));
        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertTrue(mrsListener.deleted);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
        ((AllCouchFacilitiesImpl) allFacilities).removeAll();
    }

    public class MrsListener implements EventListener {

        private boolean created = false;
        private boolean updated = false;
        private boolean deleted = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.CREATED_NEW_FACILITY_SUBJECT, EventKeys.UPDATED_FACILITY_SUBJECT, EventKeys.DELETED_FACILITY_SUBJECT})
        public void handle(MotechEvent event) {
            if (event.getSubject().equals(EventKeys.CREATED_NEW_FACILITY_SUBJECT)) {
                created = true;
            } else if (event.getSubject().equals(EventKeys.UPDATED_FACILITY_SUBJECT)) {
                updated = true;
            } else if (event.getSubject().equals(EventKeys.DELETED_FACILITY_SUBJECT)) {
                deleted = true;
            }
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
