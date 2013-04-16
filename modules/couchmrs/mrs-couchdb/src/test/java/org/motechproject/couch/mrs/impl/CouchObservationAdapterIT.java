package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.impl.CouchFacilityAdapterIT.MrsListener;
import org.motechproject.couch.mrs.model.CouchObservation;
import org.motechproject.couch.mrs.model.CouchObservationImpl;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.Initializer;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchObservations;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.motechproject.couch.mrs.repository.impl.AllCouchObservationsImpl;
import org.motechproject.couch.mrs.repository.impl.AllCouchProvidersImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchObservationAdapterIT extends SpringIntegrationTest {

    @Autowired
    private CouchObservationAdapter observationAdapter;

    @Autowired
    private AllCouchObservations allObservations;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Autowired
    @Qualifier("couchObservationDatabaseConnector")
    CouchDbConnector connector;

    @Test
    public void shouldReturnFullObservationObjectGraph() throws MRSCouchException {
        CouchObservation<String> observation = new CouchObservation<String>(new DateTime(), "testConcept", "stringValue", "patient1");

        CouchObservation<String> observation2 = new CouchObservation<String>(new DateTime(), "testConcep2t", "stringValue2", "patient1");

        CouchObservation<String> observation3 = new CouchObservation<String>(new DateTime(), "testConcept3", "stringValue3", "patient1");

        CouchObservation<String> observation4 = new CouchObservation<String>(new DateTime(), "testConcept4", "stringValue4", "patient1");

        Set<MRSObservation> dependantObservations = new HashSet<MRSObservation>();

        dependantObservations.add(observation);
        dependantObservations.add(observation2);
        dependantObservations.add(observation3);

        observation4.setDependantObservations(dependantObservations);

        allObservations.addOrUpdateObservation(observation);
        allObservations.addOrUpdateObservation(observation2);
        allObservations.addOrUpdateObservation(observation3);
        allObservations.addOrUpdateObservation(observation4);

        List<MRSObservation> obsList = observationAdapter.findObservations(observation4.getPatientId(), observation4.getConceptName());

        assertEquals(1, obsList.size());

        Set<MRSObservation> dependantObsReturned = obsList.get(0).getDependantObservations();

        assertEquals(3, dependantObsReturned.size());

        Iterator<MRSObservation> obsIterator = dependantObsReturned.iterator();

        while (obsIterator.hasNext()) {
            assertTrue(obsIterator.next().getConceptName() != null);
        }
    }

    @Test
    public void shouldRaiseVoidEvent() throws ObservationNotFoundException, InterruptedException {
        CouchObservation observation = new CouchObservation(new DateTime(), "testConcept", "stringValue", "patient1");

        allObservations.addOrUpdateObservation(observation);

        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, EventKeys.DELETED_OBSERVATION_SUBJECT);

        synchronized (lock) {
            observationAdapter.voidObservation(observation, null, null);
            lock.wait(60000);
        }

        assertEquals(observation.getConceptName(), mrsListener.eventParameters.get(EventKeys.OBSERVATION_CONCEPT_NAME));
        assertTrue(mrsListener.voided);
    }


    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchObservationsImpl) allObservations).removeAll();
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    public class MrsListener implements EventListener {

        private boolean voided = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.DELETED_OBSERVATION_SUBJECT})
        public void handle(MotechEvent event) {
            voided = true;
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
