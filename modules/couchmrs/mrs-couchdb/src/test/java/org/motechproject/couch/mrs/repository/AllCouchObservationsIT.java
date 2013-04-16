package org.motechproject.couch.mrs.repository;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.impl.CouchObservationAdapter;
import org.motechproject.couch.mrs.model.CouchObservation;
import org.motechproject.couch.mrs.model.CouchObservationImpl;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchObservations;
import org.motechproject.couch.mrs.repository.impl.AllCouchObservationsImpl;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class AllCouchObservationsIT extends SpringIntegrationTest {

    @Autowired
    private CouchObservationAdapter observationAdapter;

    @Autowired
    private AllCouchObservations allObservations;

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    @Autowired
    @Qualifier("couchObservationDatabaseConnector")
    CouchDbConnector connector;

    @Test
    public void shouldSaveAnObservationAndRetrievedById() throws MRSCouchException {

        CouchObservation observation = new CouchObservation(new DateTime(), "testConcept", "stringValue");

        allObservations.addOrUpdateObservation(observation);

        CouchObservationImpl retrieved = allObservations.findByObservationId(observation.getObservationId()).get(0);

        assertEquals(retrieved.getConceptName(), observation.getConceptName());
    }

    @Test
    public void shouldSaveAnObservationAndRetrieveByPatientIdAndConceptName() {
        CouchObservation observation = new CouchObservation(new DateTime(), "testConcept", "stringValue", "patient1");

        CouchObservation observation2 = new CouchObservation(new DateTime(), "testConcept", "stringValue", "patient2");

        CouchObservation observation3 = new CouchObservation(new DateTime(), "testConcept", "stringValue2", "patient2");

        CouchObservation observation4 = new CouchObservation(new DateTime(), "testConcept2", "stringValue", "patient2");

        allObservations.addOrUpdateObservation(observation);
        allObservations.addOrUpdateObservation(observation2);
        allObservations.addOrUpdateObservation(observation3);
        allObservations.addOrUpdateObservation(observation4);

        List<CouchObservationImpl> observations = allObservations.findByMotechIdAndConceptName("patient2", "testConcept");

        assertEquals(2, observations.size());
    }

    @Test
    public void shouldUpdateAnObservation() {
        CouchObservation observation = new CouchObservation(new DateTime(), "testConcept", "stringValue", "patient1");

        allObservations.addOrUpdateObservation(observation);

        CouchObservation observation2 = new CouchObservation(observation.getObservationId(), new DateTime(), "testConceptRevised", "stringValueRevised", "patient1");

        allObservations.addOrUpdateObservation(observation2);

        List<CouchObservationImpl> observationRetrieved = allObservations.findByObservationId(observation.getObservationId());

        assertEquals(1, observationRetrieved.size());
        assertEquals(observationRetrieved.get(0).getConceptName(), observation2.getConceptName());
        assertEquals(observationRetrieved.get(0).getValue(), observation2.getValue());

    }

    @Test
    public void shouldRemoveObservationOnVoid() {
        CouchObservation observation = new CouchObservation(new DateTime(), "testConcept", "stringValue", "patient1");

        allObservations.addOrUpdateObservation(observation);

        List<CouchObservationImpl> oldObs = allObservations.findByObservationId(observation.getObservationId());

        assertEquals(1, oldObs.size());

        allObservations.removeObservation(observation);

        List<CouchObservationImpl> removedObs = allObservations.findByObservationId(observation.getObservationId());

        assertEquals(0, removedObs.size());

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

}
