package org.motechproject.openmrs.ws.impl.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.OpenMRSObservation;
import org.motechproject.mrs.services.ObservationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractObservationIT {

    @Autowired
    private ObservationAdapter obsAdapter;

    @Test
    public void shouldFindSearchedConcept() {
        OpenMRSObservation obs = (OpenMRSObservation) obsAdapter.findObservation("700", "Search Concept");
        assertNotNull(obs);
    }

    @Test
    public void shouldFindListOfObservations() {
        List<Observation> obs = obsAdapter.findObservations("700", "Search Concept");

        assertNotNull(obs);
        assertTrue(obs.size() > 0);
    }

    @Test
    public void shouldVoidObservation() throws ObservationNotFoundException {
        OpenMRSObservation obsToVoid = (OpenMRSObservation) obsAdapter.findObservation("700", "Voidable Concept");
        obsAdapter.voidObservation(obsToVoid, null, null);
    }
}
