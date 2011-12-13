package org.motechproject.openmrs.services;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.mrs.model.MRSObservation;
import org.openmrs.*;
import org.openmrs.api.ObsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSObservationAdaptorTest {

    OpenMRSObservationAdaptor observationAdaptor;

    @Mock
    OpenMRSConceptAdaptor mockConceptAdaptor;

    @Mock
    private ObsService mockObservationService;

    @Before
    public void setUp() {
        initMocks(this);
        observationAdaptor = new OpenMRSObservationAdaptor();
        ReflectionTestUtils.setField(observationAdaptor, "conceptAdaptor", mockConceptAdaptor);
        ReflectionTestUtils.setField(observationAdaptor, "obsService", mockObservationService);
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

        Set<Obs> openMrsObservations = observationAdaptorSpy.createOpenMRSObservationsForEncounter(mrsObservations, encounter, patient, facility, staff);
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
        ConceptDatatype conceptDatatype = mock(ConceptDatatype.class);
        when(concept1.getDatatype()).thenReturn(conceptDatatype);
        when(conceptDatatype.isText()).thenReturn(true);
        ConceptName conceptName1 = mock(ConceptName.class);
        when(concept1.getName()).thenReturn(conceptName1);
        when(conceptName1.getName()).thenReturn("name1");

        Concept concept2 = mock(Concept.class);
        ConceptDatatype conceptDatatype1 = mock(ConceptDatatype.class);
        when(concept2.getDatatype()).thenReturn(conceptDatatype1);
        when(conceptDatatype1.isNumeric()).thenReturn(true);
        ConceptName conceptName2 = mock(ConceptName.class);
        when(concept2.getName()).thenReturn(conceptName2);
        when(conceptName2.getName()).thenReturn("name2");

        obs1.setId(1);
        obs1.setConcept(concept1);
        Encounter encounter = new Encounter();
        Patient person = new Patient();
        obs1.setEncounter(encounter);
        obs1.setPerson(person);
        obs1.setObsDatetime(new Date());
        obs1.setCreator(new User());
        obs1.setValueText("tr");

        obs2.setId(2);
        obs2.setConcept(concept2);
        obs2.setEncounter(encounter);
        obs2.setPerson(person);
        obs2.setObsDatetime(new Date());
        obs2.setCreator(new User());
        obs2.setValueNumeric(10.12);

        Set<Obs> openMRSObservations = new HashSet<Obs>() {{
            add(obs1);
            add(obs2);
        }};
        Set<MRSObservation> actualMrsObservations = observationAdaptor.convertOpenMRSToMRSObservations(openMRSObservations);

        assertThat(actualMrsObservations.size(), Matchers.is(equalTo(2)));
        assertMRSObservation(observationBy(conceptName1, actualMrsObservations)
                , new MRSObservation(obs1.getObsDatetime(), conceptName1.getName(), obs1.getValueText()));
        assertMRSObservation(observationBy(conceptName2, actualMrsObservations),
                new MRSObservation(obs2.getObsDatetime(), conceptName2.getName(), obs2.getValueNumeric()));
    }

    private MRSObservation observationBy(ConceptName conceptName1, Set<MRSObservation> actualMrsObservations) {
        return (MRSObservation)selectFirst(actualMrsObservations, having(on(MRSObservation.class).getConceptName(), equalTo(conceptName1.getName())));
    }

    private void assertMRSObservation(MRSObservation actualObservation, MRSObservation expectedObservation) {
        assertThat(actualObservation.getConceptName(), is(expectedObservation.getConceptName()));
        assertThat(actualObservation.getValue(), is(expectedObservation.getValue()));
        assertThat(actualObservation.getDate(), is(expectedObservation.getDate()));
    }

    @Test
    public void shouldSetTheValueBasedOnType() {
        Obs fever = new Obs();
        Obs temperature = new Obs();
        Obs expectedDeliveryDate = new Obs();
        Obs HIV = new Obs();

        String feverValue = "high";
        Double temperatureValue = 99.0;
        Boolean hivValue = false;
        Date expectedDeliveryDateValue = new Date(2012, 12, 21);

        observationAdaptor.writeValueToOpenMRSObservation(feverValue, fever);
        observationAdaptor.writeValueToOpenMRSObservation(temperatureValue, temperature);
        observationAdaptor.writeValueToOpenMRSObservation(expectedDeliveryDateValue, expectedDeliveryDate);
        observationAdaptor.writeValueToOpenMRSObservation(hivValue, HIV);

        assertThat(fever.getValueText(), is(equalTo(feverValue)));
        assertThat(temperature.getValueNumeric(), is(equalTo(temperatureValue)));
        assertThat(expectedDeliveryDate.getValueDatetime(), is(equalTo(expectedDeliveryDateValue)));
        assertThat(HIV.getValueAsBoolean(), is(equalTo(hivValue)));

    }
    
    @Test    
    public void shouldThrowExceptionIfInvalidArgumentIsSet() {
        try {
            observationAdaptor.writeValueToOpenMRSObservation(new Object(),  new Obs());
            Assert.fail("should throw exception");
        } catch(IllegalArgumentException e){               
        }
    }

    @Test
    public void shouldSaveObservation() {
        OpenMRSObservationAdaptor observationAdaptorSpy = Mockito.spy(observationAdaptor);
        MRSObservation mrsObservation = mock(MRSObservation.class);
        MRSObservation savedMRSObservation = mock(MRSObservation.class);
        Obs openMRSObservation = Mockito.mock(Obs.class);
        Obs savedOpenMRSObservation = Mockito.mock(Obs.class);

        Encounter encounter = new Encounter();
        Patient patient = new Patient();
        User user = new User();
        Location facility=new Location();

        doReturn(openMRSObservation).when(observationAdaptorSpy).createOpenMRSObservationForEncounter(mrsObservation, encounter, patient, facility, user);
        when(mockObservationService.saveObs(openMRSObservation, null)).thenReturn(savedOpenMRSObservation);
        doReturn(savedMRSObservation).when(observationAdaptorSpy).convertOpenMRSToMRSObservation(savedOpenMRSObservation);

        MRSObservation returnedMRSObservation = observationAdaptorSpy.saveObservation(mrsObservation, encounter,patient,facility,user);
        Assert.assertThat(returnedMRSObservation, Matchers.is(equalTo(savedMRSObservation)));
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
