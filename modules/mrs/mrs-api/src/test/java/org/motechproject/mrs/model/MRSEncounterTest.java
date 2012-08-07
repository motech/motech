package org.motechproject.mrs.model;

import org.junit.Test;

import java.util.Date;
import java.util.Set;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class MRSEncounterTest {
    @Test
    public void shouldUpdateFromExistingEncounter() {
        final String encounterTpye = "encounterTpye";
        final Set observations = mock(Set.class);
        final MRSPatient patient = mock(MRSPatient.class);
        final Date date = mock(Date.class);
        final MRSFacility facility = mock(MRSFacility.class);
        final MRSUser creator = mock(MRSUser.class);
        final MRSPerson provider = mock(MRSPerson.class);
        MRSEncounter fromEncounter = new MRSEncounter("id", provider, creator, facility, date, patient, observations, encounterTpye);

        final MRSEncounter actualEncounter = new MRSEncounter("id2", null, null, null, null, null, null, null).updateWithoutObs(fromEncounter);
        assertNull(actualEncounter.getObservations());

        assertThat(actualEncounter.getId(), is("id2"));
        assertThat(actualEncounter.getCreator(), is(creator));
        assertThat(actualEncounter.getProvider(), is(provider));
        assertThat(actualEncounter.getEncounterType(), is(encounterTpye));
        assertThat(actualEncounter.getDate(), is(date));
        assertThat(actualEncounter.getFacility(), is(facility));
    }
}
