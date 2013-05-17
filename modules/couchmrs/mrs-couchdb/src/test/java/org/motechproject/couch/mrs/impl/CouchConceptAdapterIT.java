package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchConcept;
import org.motechproject.couch.mrs.model.CouchConceptName;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchConcepts;
import org.motechproject.couch.mrs.repository.impl.AllCouchConceptsImpl;
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchConceptAdapterIT extends SpringIntegrationTest {

    public static final String CONCEPT_ID = "conceptId";
    public static final String CONCEPT_ID2 = "conceptId2";
    public static final String CONCEPT_NAME = "conceptName";
    public static final String CONCEPT_TEST = "conceptIdTest";
    public static final String CONCEPT_BEFORE_UPDATE = "beforeUpdate";
    public static final String CONCEPT_AFTER_UPDATE = "afterUpdate";

    @Autowired
    private CouchConceptAdapter conceptAdapter;

    @Autowired
    private AllCouchConcepts allConcepts;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    @Autowired
    @Qualifier("couchConceptDatabaseConnector")
    CouchDbConnector connector;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Before
    public void initialize() {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_CONCEPT_SUBJECT, EventKeys.UPDATED_CONCEPT_SUBJECT, EventKeys.DELETED_CONCEPT_SUBJECT));
    }

    @Test
    public void shouldSaveAConceptAndRetrieveByName() throws MRSCouchException, InterruptedException {
        CouchConcept concept = new CouchConcept();
        concept.setId(CONCEPT_ID);
        concept.setName(new CouchConceptName(CONCEPT_NAME));

        CouchConcept concept2 = new CouchConcept();
        concept2.setId(CONCEPT_ID2);
        concept2.setName(new CouchConceptName(CONCEPT_NAME));

        synchronized (lock) {
            conceptAdapter.saveConcept(concept);
            lock.wait(60000);
        }

        assertEquals(concept.getName(), mrsListener.eventParameters.get(EventKeys.CONCEPT_NAME));
        assertTrue(mrsListener.created);
        mrsListener.created = false;

        synchronized (lock) {
            conceptAdapter.saveConcept(concept2);
            lock.wait(60000);
        }

        List<CouchConcept> concepts = conceptAdapter.search(CONCEPT_NAME);

        assertEquals(asList(CONCEPT_ID, CONCEPT_ID2), extract(concepts, on(CouchConcept.class).getId()));
        assertEquals(concept2.getName(), mrsListener.eventParameters.get(EventKeys.CONCEPT_NAME));
        assertTrue(mrsListener.created);

        eventListenerRegistry.clearListenersForBean(mrsListener.getIdentifier());
    }

    @Test
    public void shouldUpdateConcept() throws MRSCouchException, InterruptedException {
        CouchConcept concept = new CouchConcept();
        concept.setId(CONCEPT_ID);
        concept.setName(new CouchConceptName(CONCEPT_BEFORE_UPDATE));

        synchronized (lock) {
            conceptAdapter.saveConcept(concept);
            lock.wait(60000);
        }

        assertEquals(concept.getName(), mrsListener.eventParameters.get(EventKeys.CONCEPT_NAME));
        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertFalse(mrsListener.deleted);

        concept.setName(new CouchConceptName(CONCEPT_AFTER_UPDATE));
        CouchConcept updatedConcept;

        synchronized (lock) {
            updatedConcept = (CouchConcept) conceptAdapter.updateConcept(concept);
            lock.wait(60000);
        }

        assertEquals(updatedConcept.getName(), mrsListener.eventParameters.get(EventKeys.CONCEPT_NAME));
        assertEquals(updatedConcept.getName().getName(), CONCEPT_AFTER_UPDATE);
        assertTrue(mrsListener.created);
        assertTrue(mrsListener.updated);
        assertFalse(mrsListener.deleted);

        eventListenerRegistry.clearListenersForBean(mrsListener.getIdentifier());
    }

    @Test
    public void shouldDeleteConcept() throws MRSCouchException, InterruptedException {
        CouchConcept concept = new CouchConcept();
        concept.setId(CONCEPT_TEST);
        concept.setName(new CouchConceptName(CONCEPT_NAME));

        synchronized (lock) {
            conceptAdapter.saveConcept(concept);
            lock.wait(60000);
        }

        assertEquals(concept.getName(), mrsListener.eventParameters.get(EventKeys.CONCEPT_NAME));

        synchronized (lock) {
            conceptAdapter.deleteConcept(concept.getId());
            lock.wait(60000);
        }

        assertNull(conceptAdapter.getConcept(CONCEPT_TEST));
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
        ((AllCouchConceptsImpl) allConcepts).removeAll();
    }

    public class MrsListener implements EventListener {

        private boolean created = false;
        private boolean deleted = false;
        private boolean updated = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.CREATED_NEW_CONCEPT_SUBJECT, EventKeys.UPDATED_CONCEPT_SUBJECT, EventKeys.DELETED_CONCEPT_SUBJECT})
        public void handle(MotechEvent event) {
            if (event.getSubject().equals(EventKeys.CREATED_NEW_CONCEPT_SUBJECT)) {
                created = true;
            } else if (event.getSubject().equals(EventKeys.UPDATED_CONCEPT_SUBJECT)) {
                updated = true;
            } else if (event.getSubject().equals(EventKeys.DELETED_CONCEPT_SUBJECT)) {
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
