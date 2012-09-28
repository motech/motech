package org.motechproject.openmrs.ws.impl.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractObservationIT {

    @Autowired
    private MRSObservationAdapter obsAdapter;

    @Test
    public void shouldFindSearchedConcept() {
        MRSObservation obs = obsAdapter.findObservation("700", "Search Concept");
        assertNotNull(obs);
    }

    @Test
    public void shouldFindListOfObservations() {
        List<MRSObservation> obs = obsAdapter.findObservations("700", "Search Concept");

        assertNotNull(obs);
        assertTrue(obs.size() > 0);
    }

    @Test
    public void shouldVoidObservation() throws ObservationNotFoundException {
        MRSObservation obsToVoid = obsAdapter.findObservation("700", "Voidable Concept");
        obsAdapter.voidObservation(obsToVoid, null, null);
    }
}
