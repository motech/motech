package org.motechproject.openmrs.services;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.model.MRSObservation;
import org.openmrs.*;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyDouble;
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
        assertThat(openMrsObservations, is(equalTo((Set<Obs>) new HashSet<Obs>() {{
            add(openMrsObservation1);
            add(openMrsObservation2);
        }})));

    }


    @Test
    public void shouldConvertOpenMRSObservationsToMRSObservations() {
        final Obs obs1 = new Obs();
        final Obs obs2 = new Obs();

        Concept concept1 = mock(Concept.class);
        ConceptName conceptName = mock(ConceptName.class);
        when(concept1.getName()).thenReturn(conceptName);
        when(conceptName.getName()).thenReturn("name1");

        Concept concept2 = mock(Concept.class);
        ConceptName conceptName2 = mock(ConceptName.class);
        when(concept2.getName()).thenReturn(conceptName2);
        when(conceptName2.getName()).thenReturn("name2");

        obs1.setId(1);
        obs1.setConcept(concept1);
        obs1.setEncounter(new Encounter());
        obs1.setPerson(new Patient());
        obs1.setObsDatetime(new Date());
        obs1.setCreator(new User());

        obs2.setId(2);
        obs2.setConcept(concept2);
        obs2.setEncounter(new Encounter());
        obs2.setPerson(new Patient());
        obs2.setObsDatetime(new Date());
        obs2.setCreator(new User());

        Set<Obs> openMRSObservations = new HashSet<Obs>() {{
            add(obs1);
            add(obs2);
        }};
        Set<MRSObservation> actualMrsObservations = observationAdaptor.convertOpenMRSToMRSObservations(openMRSObservations);

        assertThat(actualMrsObservations.size(), Matchers.is(equalTo(2)));

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
