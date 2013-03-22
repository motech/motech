package org.motechproject.mrs.model;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.domain.MRSUser;
import org.motechproject.openmrs.model.OpenMRSEncounter;
import org.motechproject.openmrs.model.OpenMRSFacility;
import org.motechproject.openmrs.model.OpenMRSPatient;
import org.motechproject.openmrs.model.OpenMRSProvider;
import org.motechproject.openmrs.model.OpenMRSUser;

import java.util.Date;
import java.util.Set;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class OpenMRSEncounterTest {
    @Test
    public void shouldUpdateFromExistingEncounter() {
        final String encounterTpye = "encounterTpye";
        final Set observations = mock(Set.class);
        final OpenMRSPatient patient = mock(OpenMRSPatient.class);
        final Date date = mock(Date.class);
        final MRSFacility facility = mock(OpenMRSFacility.class);
        final MRSUser creator = mock(OpenMRSUser.class);
        final MRSProvider provider = mock(OpenMRSProvider.class);
        MRSEncounter fromEncounter = new OpenMRSEncounter.MRSEncounterBuilder().withId("id").withProvider(provider).withCreator(creator)
                .withFacility(facility).withDate(date).withPatient(patient).withObservations(observations)
                .withEncounterType(encounterTpye).build();

        final MRSEncounter actualEncounter = new OpenMRSEncounter.MRSEncounterBuilder().withId("id2").build().updateWithoutObs(fromEncounter);
        assertNull(actualEncounter.getObservations());

        assertThat(actualEncounter.getEncounterId(), is("id2"));
        assertThat(actualEncounter.getCreator(), is(creator));
        assertThat(actualEncounter.getProvider(), is(provider));
        assertThat(actualEncounter.getEncounterType(), is(encounterTpye));
        assertThat(actualEncounter.getDate().toDate(), is(new DateTime(date).toDate()));
        assertThat(actualEncounter.getFacility(), is(facility));
    }
}
