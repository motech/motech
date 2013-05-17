package org.motechproject.openmrs.ws.impl.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSConcept;
import org.motechproject.mrs.services.MRSConceptAdapter;
import org.motechproject.openmrs.model.OpenMRSConcept;
import org.motechproject.openmrs.model.OpenMRSConceptName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractConceptAdapterIT {
    @Autowired
    private MRSConceptAdapter conceptAdapter;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Test
    public void shouldSaveConcept() throws InterruptedException {
        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.CREATED_NEW_CONCEPT_SUBJECT));

        OpenMRSConcept concept = new OpenMRSConcept(new OpenMRSConceptName("Test Concept"));
        OpenMRSConcept created;

        synchronized (lock) {
            created = (OpenMRSConcept) conceptAdapter.saveConcept(concept);
            lock.wait(60000);
        }

        assertNotNull(created.getName());

        assertTrue(mrsListener.created);
        assertEquals(created.getName(), mrsListener.eventParameters.get(EventKeys.CONCEPT_NAME));
        eventListenerRegistry.clearListenersForBean(mrsListener.getIdentifier());
    }

    @Test
    public void shouldUpdateConcept() throws InterruptedException {
        OpenMRSConcept concept = new OpenMRSConcept(new OpenMRSConceptName("Concept to update"));
        OpenMRSConcept created;

        synchronized (lock) {
            created = (OpenMRSConcept) conceptAdapter.saveConcept(concept);
            lock.wait(60000);
        }

        assertNotNull(created.getId());
        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertFalse(mrsListener.deleted);
        assertEquals(created.getName(), mrsListener.eventParameters.get(EventKeys.CONCEPT_NAME));

        created.setName(new OpenMRSConceptName("Concept after update"));
        OpenMRSConcept updatedConcept;

        synchronized (lock) {
            updatedConcept = (OpenMRSConcept) conceptAdapter.updateConcept(created);
            lock.wait(60000);
        }

        assertNotNull(updatedConcept.getId());
        assertTrue(mrsListener.created);
        assertTrue(mrsListener.updated);
        assertFalse(mrsListener.deleted);
        assertEquals(updatedConcept.getName(), mrsListener.eventParameters.get(EventKeys.CONCEPT_NAME));
        eventListenerRegistry.clearListenersForBean(mrsListener.getIdentifier());
    }

    @Test
    public void shouldDeleteConcept() throws InterruptedException {
        OpenMRSConcept concept = new OpenMRSConcept(new OpenMRSConceptName("Concept to delete"));
        OpenMRSConcept created;

        synchronized (lock) {
            created = (OpenMRSConcept) conceptAdapter.saveConcept(concept);
            lock.wait(60000);
        }

        assertNotNull(created.getId());
        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertFalse(mrsListener.deleted);
        assertEquals(created.getName(), mrsListener.eventParameters.get(EventKeys.CONCEPT_NAME));

        synchronized (lock) {
            conceptAdapter.deleteConcept(created.getId());
            lock.wait(60000);
        }

        assertNull(conceptAdapter.getConcept(created.getId()));
        assertTrue(mrsListener.created);
        assertFalse(mrsListener.updated);
        assertTrue(mrsListener.deleted);
    }

    @Test
    public void shouldFindMultipleConcepts() {
        List<? extends MRSConcept> concepts = conceptAdapter.getAllConcepts();

        assertTrue(concepts.size() > 0);
    }

    @Test
    public void shouldFindSingleConceptByName() {
        List<? extends MRSConcept> concepts = conceptAdapter.search("Test Concept");

        assertEquals(1, concepts.size());
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
