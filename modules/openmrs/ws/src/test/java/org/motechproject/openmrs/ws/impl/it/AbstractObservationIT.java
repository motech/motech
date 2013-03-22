package org.motechproject.openmrs.ws.impl.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.motechproject.openmrs.model.OpenMRSObservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractObservationIT {

    @Autowired
    private MRSObservationAdapter obsAdapter;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Test
    public void shouldFindSearchedConcept() {
        OpenMRSObservation obs = (OpenMRSObservation) obsAdapter.findObservation("700", "Search Concept");
        assertNotNull(obs);
    }

    @Test
    public void shouldFindListOfObservations() {
        List<MRSObservation> obs = obsAdapter.findObservations("700", "Search Concept");

        assertNotNull(obs);
        assertTrue(obs.size() > 0);
    }

    @Test
    public void shouldVoidObservation() throws ObservationNotFoundException, InterruptedException {
        MrsListener mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, Arrays.asList(EventKeys.DELETED_OBSERVATION_SUBJECT));

        OpenMRSObservation obsToVoid = (OpenMRSObservation) obsAdapter.findObservation("700", "Voidable Concept");

        synchronized (lock) {
            obsAdapter.voidObservation(obsToVoid, null, null);
            lock.wait(60000);
        }

        assertTrue(mrsListener.deleted);
        assertEquals(obsToVoid.getConceptName(), mrsListener.eventParameters.get(EventKeys.OBSERVATION_CONCEPT_NAME));
        assertEquals(obsToVoid.getDate(), mrsListener.eventParameters.get(EventKeys.OBSERVATION_DATE));
        assertEquals(obsToVoid.getPatientId(), mrsListener.eventParameters.get(EventKeys.PATIENT_ID));
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    public class MrsListener implements EventListener {

        private boolean deleted = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.DELETED_OBSERVATION_SUBJECT})
        public void handle(MotechEvent event) {
            deleted = true;
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
