package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.model.MRSObservation;
import org.openmrs.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class OpenMRSObservationAdaptorTest {

    OpenMRSObservationAdaptor observationAdaptor;

    @Mock
    OpenMRSConceptAdaptor mockConceptAdaptor;

    @Before
    public void setUp() {
        initMocks(this);
        observationAdaptor = new OpenMRSObservationAdaptor();
        ReflectionTestUtils.setField(observationAdaptor, "conceptAdaptor", mockConceptAdaptor);
    }

    @Test
    public void shouldCreateAObservationForAnEncounter() {
        Patient patient = mock(Patient.class);
        Location facility = mock(Location.class);
        Encounter encounter = mock(Encounter.class);
        User staff = mock(User.class);

        Date observationDate = new Date(2011, 12, 31);
        String conceptName = "concept";

        String feverValue = "high";
        Double temperatureValue = 99.0;
        Boolean hivValue = false;
        Date expectedDeliveryDateValue = new Date(2012, 12, 21);

        MRSObservation<String> fever = new MRSObservation<String>(observationDate, conceptName, feverValue);
        MRSObservation<Double> temperature = new MRSObservation<Double>(observationDate, conceptName, temperatureValue);
        MRSObservation<Boolean> hiv = new MRSObservation<Boolean>(observationDate, conceptName, hivValue);
        MRSObservation<Date> expectedDelvieryDate = new MRSObservation<Date>(observationDate, conceptName, expectedDeliveryDateValue);

        Concept concept = mock(Concept.class);
        when(mockConceptAdaptor.getConceptByName(conceptName)).thenReturn(concept);

        Obs openMrsObservation = observationAdaptor.<String>createOpenMRSObservationForEncounter(fever, encounter, patient, facility, staff);
        assertOpenMrsObservationProperties(openMrsObservation, fever, patient, facility, encounter, staff, concept);
        assertThat(openMrsObservation.getValueText(), is(equalTo(feverValue)));

        openMrsObservation = observationAdaptor.<Double>createOpenMRSObservationForEncounter(temperature, encounter, patient, facility, staff);
        assertOpenMrsObservationProperties(openMrsObservation, temperature, patient, facility, encounter, staff, concept);
        assertThat(openMrsObservation.getValueNumeric(), is(equalTo(temperatureValue)));

        openMrsObservation = observationAdaptor.<Boolean>createOpenMRSObservationForEncounter(hiv, encounter, patient, facility, staff);
        assertOpenMrsObservationProperties(openMrsObservation, hiv, patient, facility, encounter, staff, concept);
        assertThat(openMrsObservation.getValueAsBoolean(), is(equalTo(hivValue)));

        openMrsObservation = observationAdaptor.<Date>createOpenMRSObservationForEncounter(expectedDelvieryDate, encounter, patient, facility, staff);
        assertOpenMrsObservationProperties(openMrsObservation, expectedDelvieryDate, patient, facility, encounter, staff, concept);
        assertThat(openMrsObservation.getValueDatetime(), is(equalTo(expectedDeliveryDateValue)));
    }

    @Test
    public void shouldCreateObservationsForAnEncounter() {
        Patient patient = mock(Patient.class);
        Location facility = mock(Location.class);
        Encounter encounter = mock(Encounter.class);
        User staff = mock(User.class);

        OpenMRSObservationAdaptor observationAdaptorSpy = spy(observationAdaptor);
        final MRSObservation observation1 = mock(MRSObservation.class);
        final MRSObservation observation2 = mock(MRSObservation.class);

        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>() {{
            add(observation1);
            add(observation2);
        }};

        final Obs openMrsObservation1 = mock(Obs.class);
        final Obs openMrsObservation2 = mock(Obs.class);
        doReturn(openMrsObservation1).when(observationAdaptorSpy).createOpenMRSObservationForEncounter(observation1, encounter, patient, facility, staff);
        doReturn(openMrsObservation2).when(observationAdaptorSpy).createOpenMRSObservationForEncounter(observation2, encounter, patient, facility, staff);

        Set<Obs> openMrsObservations = observationAdaptorSpy.createOpenMRSObservationForEncounters(mrsObservations, encounter, patient, facility, staff);
        assertThat(openMrsObservations, is(equalTo((Set<Obs>)new HashSet<Obs>() {{
            add(openMrsObservation1);
            add(openMrsObservation2);
        }})));

    }

    private void assertOpenMrsObservationProperties(Obs openMrsObservation, MRSObservation mrsObservation, Patient patient,
                                                    Location facility, Encounter encounter, User staff, Concept concept) {
        assertThat(openMrsObservation.getObsDatetime(), is(equalTo(mrsObservation.getDate())));
        assertThat(openMrsObservation.getConcept(), is(equalTo(concept)));
        assertThat(openMrsObservation.getPerson(), is(equalTo((Person) patient)));
        assertThat(openMrsObservation.getLocation(), is(equalTo(facility)));
        assertThat(openMrsObservation.getEncounter(), is(equalTo(encounter)));
        assertThat(openMrsObservation.getCreator(), is(equalTo(staff)));
    }


}
