package org.motechproject.openmrs.services;

import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSObservation;
import org.openmrs.*;
import org.openmrs.api.ObsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSObservationAdapterTest {

    OpenMRSObservationAdapter observationAdapter;

    @Mock
    OpenMRSConceptAdapter mockConceptAdapter;
    @Mock
    private ObsService mockObservationService;
    @Mock
    private Patient patient;
    @Mock
    private Location facility;
    @Mock
    private Encounter encounter;
    @Mock
    private OpenMRSUserAdapter mockOpenMRSUserAdapter;
    @Mock
    private OpenMRSPatientAdapter mockOpenMRSPatientAdapter;
    @Mock
    private User creator;

    @Before
    public void setUp() {
        initMocks(this);
        observationAdapter = new OpenMRSObservationAdapter();
        ReflectionTestUtils.setField(observationAdapter, "openMRSConceptAdapter", mockConceptAdapter);
        ReflectionTestUtils.setField(observationAdapter, "obsService", mockObservationService);
        ReflectionTestUtils.setField(observationAdapter, "openMRSUserAdapter", mockOpenMRSUserAdapter);
        ReflectionTestUtils.setField(observationAdapter, "openMRSPatientAdapter", mockOpenMRSPatientAdapter);
    }

    @Test
    public void shouldCreateAnObservationToHoldConceptValue() {

        String observationConceptName = "concept1";
        String observationValueConceptName = "concept2";
        String dependentObservationValueConceptName = "concept3";

        Date observationDate = new LocalDate(2011, 12, 31).toDate();
        Date dependentObservationDate = new LocalDate(1999, 1, 1).toDate();

        MRSConcept observationValueConcept = new MRSConcept(observationValueConceptName);
        MRSConcept dependentConceptValue = new MRSConcept(dependentObservationValueConceptName);

        Concept openMrsConceptUsedForObsName = mock(Concept.class);
        Concept openMrsConceptUsedForObsValue = mock(Concept.class);
        Concept openMrsConceptUsedForDepObsValue = mock(Concept.class);

        when(mockConceptAdapter.getConceptByName(observationConceptName)).thenReturn(openMrsConceptUsedForObsName);
        when(mockConceptAdapter.getConceptByName(observationValueConceptName)).thenReturn(openMrsConceptUsedForObsValue);
        when(mockConceptAdapter.getConceptByName(dependentObservationValueConceptName)).thenReturn(openMrsConceptUsedForDepObsValue);

        MRSObservation<MRSConcept> expectedDeliveryConcept = new MRSObservation<MRSConcept>(observationDate, observationConceptName, observationValueConcept);
        expectedDeliveryConcept.addDependantObservation(new MRSObservation<MRSConcept>(dependentObservationDate, dependentObservationValueConceptName, dependentConceptValue));

        Obs openMRSObservation = observationAdapter.createOpenMRSObservationForEncounter(expectedDeliveryConcept, encounter, patient, facility, creator);

        assertOpenMrsObservationProperties(openMRSObservation, expectedDeliveryConcept, patient, facility, encounter, creator, openMrsConceptUsedForObsName);

        assertThat(openMRSObservation.getValueCoded(), is(equalTo(openMrsConceptUsedForObsValue)));
        assertThat(openMRSObservation.getGroupMembers().size(), is(1));
        final Obs returnedDependentObservation = openMRSObservation.getGroupMembers().iterator().next();
        assertThat(returnedDependentObservation.getObsDatetime(), is(dependentObservationDate));
        assertThat(returnedDependentObservation.getValueCoded(), is(openMrsConceptUsedForDepObsValue));
    }


    @Test
    public void shouldCreateAObservationToHoldDateStringDoubleBooleanValues() {
        Date observationDate = new LocalDate(2011, 12, 31).toDate();
        String observationConceptName = "concept1";

        String feverValue = "high";
        Double temperatureValue = 99.0;
        Boolean hivValue = false;
        Date expectedDeliveryDateValue = new LocalDate(2012, 12, 21).toDate();


        MRSObservation<String> fever = new MRSObservation<String>(observationDate, observationConceptName, feverValue);
        MRSObservation<Double> temperature = new MRSObservation<Double>(observationDate, observationConceptName, temperatureValue);
        MRSObservation<Boolean> hiv = new MRSObservation<Boolean>(observationDate, observationConceptName, hivValue);
        MRSObservation<Date> expectedDeliveryDate = new MRSObservation<Date>(observationDate, observationConceptName, expectedDeliveryDateValue);

        Concept openMrsConceptUsedForObsName = mock(Concept.class);
        when(mockConceptAdapter.getConceptByName(observationConceptName)).thenReturn(openMrsConceptUsedForObsName);

        Obs openMrsObservation = observationAdapter.createOpenMRSObservationForEncounter(fever, encounter, patient, facility, creator);
        assertOpenMrsObservationProperties(openMrsObservation, fever, patient, facility, encounter, creator, openMrsConceptUsedForObsName);
        assertThat(openMrsObservation.getValueText(), is(equalTo(feverValue)));

        openMrsObservation = observationAdapter.createOpenMRSObservationForEncounter(temperature, encounter, patient, facility, creator);
        assertOpenMrsObservationProperties(openMrsObservation, temperature, patient, facility, encounter, creator, openMrsConceptUsedForObsName);
        assertThat(openMrsObservation.getValueNumeric(), is(equalTo(temperatureValue)));

        openMrsObservation = observationAdapter.createOpenMRSObservationForEncounter(hiv, encounter, patient, facility, creator);
        assertOpenMrsObservationProperties(openMrsObservation, hiv, patient, facility, encounter, creator, openMrsConceptUsedForObsName);
        assertThat(openMrsObservation.getValueAsBoolean(), is(equalTo(hivValue)));

        openMrsObservation = observationAdapter.createOpenMRSObservationForEncounter(expectedDeliveryDate, encounter, patient, facility, creator);
        assertOpenMrsObservationProperties(openMrsObservation, expectedDeliveryDate, patient, facility, encounter, creator, openMrsConceptUsedForObsName);
        assertThat(openMrsObservation.getValueDatetime(), is(equalTo(expectedDeliveryDateValue)));
    }

    @Test
    public void shouldCreateObservationsForAnEncounter() {
        Patient patient = mock(Patient.class);
        Location facility = mock(Location.class);
        Encounter encounter = mock(Encounter.class);
        User creator = mock(User.class);

        OpenMRSObservationAdapter observationAdapterSpy = spy(observationAdapter);
        final MRSObservation observation1 = mock(MRSObservation.class);
        final MRSObservation observation2 = mock(MRSObservation.class);

        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>() {{
            add(observation1);
            add(observation2);
        }};

        final Obs openMrsObservation1 = mock(Obs.class);
        final Obs openMrsObservation2 = mock(Obs.class);
        doReturn(openMrsObservation1).when(observationAdapterSpy).createOpenMRSObservationForEncounter(observation1, encounter, patient, facility, creator);
        doReturn(openMrsObservation2).when(observationAdapterSpy).createOpenMRSObservationForEncounter(observation2, encounter, patient, facility, creator);

        Set<Obs> openMrsObservations = observationAdapterSpy.createOpenMRSObservationsForEncounter(mrsObservations, encounter, patient, facility, creator);
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
        ConceptDatatype dependentConceptDataType = mock(ConceptDatatype.class);
        when(concept1.getDatatype()).thenReturn(conceptDatatype);
        when(conceptDatatype.isText()).thenReturn(true);
        when(dependentConceptDataType.isText()).thenReturn(true);
        ConceptName conceptName1 = mock(ConceptName.class);
        ConceptName dependentconceptName = mock(ConceptName.class);
        when(concept1.getName()).thenReturn(conceptName1);
        when(conceptName1.getName()).thenReturn("name1");

        final Concept concept2 = mock(Concept.class);
        final Concept dependentConcept = mock(Concept.class);
        ConceptDatatype conceptDatatype1 = mock(ConceptDatatype.class);
        when(concept2.getDatatype()).thenReturn(conceptDatatype1);
        when(dependentConcept.getDatatype()).thenReturn(dependentConceptDataType);
        when(conceptDatatype1.isNumeric()).thenReturn(true);
        ConceptName conceptName2 = mock(ConceptName.class);
        when(concept2.getName()).thenReturn(conceptName2);
        when(dependentConcept.getName()).thenReturn(dependentconceptName);
        when(conceptName2.getName()).thenReturn("name2");
        final String dependentConceptName = "name3";
        when(dependentconceptName.getName()).thenReturn(dependentConceptName);

        obs1.setId(1);
        obs1.setConcept(concept1);
        Encounter encounter = new Encounter();
        Patient person = new Patient();
        obs1.setEncounter(encounter);
        obs1.setPerson(person);
        obs1.setObsDatetime(new Date());
        obs1.setCreator(new User());
        obs1.setValueText("tr");
        final String dependentConceptValue = "2";
        final Date dependentObservationDate = new Date();

        obs1.addGroupMember(new Obs() {{
            setId(10);
            setObsDatetime(dependentObservationDate);
            setConcept(dependentConcept);
            setValueText(dependentConceptValue);
        }});

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
        Set<MRSObservation> actualMrsObservations = observationAdapter.convertOpenMRSToMRSObservations(openMRSObservations);

        assertThat(actualMrsObservations.size(), Matchers.is(equalTo(2)));
        final MRSObservation expectedObservation1 = new MRSObservation(obs1.getObsDatetime(), conceptName1.getName(), obs1.getValueText());
        expectedObservation1.addDependantObservation(new MRSObservation("10", dependentObservationDate, dependentConceptName, dependentConceptValue));
        assertMRSObservation(observationBy(conceptName1, actualMrsObservations), expectedObservation1, true);
        assertMRSObservation(observationBy(conceptName2, actualMrsObservations),
                new MRSObservation(obs2.getObsDatetime(), conceptName2.getName(), obs2.getValueNumeric()), false);
    }

    private MRSObservation observationBy(ConceptName conceptName1, Set<MRSObservation> actualMrsObservations) {
        return (MRSObservation) selectFirst(actualMrsObservations, having(on(MRSObservation.class).getConceptName(), equalTo(conceptName1.getName())));
    }

    private void assertMRSObservation(MRSObservation actualObservation, MRSObservation expectedObservation, boolean hasDependents) {
        assertThat(actualObservation.getConceptName(), is(expectedObservation.getConceptName()));
        assertThat(actualObservation.getValue(), is(expectedObservation.getValue()));
        assertThat(actualObservation.getDate(), is(expectedObservation.getDate()));
        if (hasDependents) {
            assertThat(actualObservation.getDependantObservations().size(), is(expectedObservation.getDependantObservations().size()));
            assertMRSObservation((MRSObservation) actualObservation.getDependantObservations().iterator().next(),
                    (MRSObservation) expectedObservation.getDependantObservations().iterator().next(), false);
        }
    }

    @Test
    public void shouldSetTheValueBasedOnType() {
        Obs fever = new Obs();
        Obs temperature = new Obs();
        Obs expectedDeliveryDate = new Obs();
        Obs HIV = new Obs();
        Obs conceptObs = new Obs();
        Obs nullObs = new Obs();

        String feverValue = "high";
        Double temperatureValue = 99.0;
        Boolean hivValue = false;
        Date expectedDeliveryDateValue = new LocalDate(2012, 12, 21).toDate();
        final MRSConcept concept = new MRSConcept("conceptName");

        Concept openMrsConcept = mock(Concept.class);
        when(mockConceptAdapter.getConceptByName(concept.getName())).thenReturn(openMrsConcept);

        observationAdapter.writeValueToOpenMRSObservation(feverValue, fever);
        observationAdapter.writeValueToOpenMRSObservation(temperatureValue, temperature);
        observationAdapter.writeValueToOpenMRSObservation(expectedDeliveryDateValue, expectedDeliveryDate);
        observationAdapter.writeValueToOpenMRSObservation(hivValue, HIV);
        observationAdapter.writeValueToOpenMRSObservation(concept, conceptObs);
        observationAdapter.writeValueToOpenMRSObservation(null, nullObs);

        assertThat(fever.getValueText(), is(equalTo(feverValue)));
        assertThat(temperature.getValueNumeric(), is(equalTo(temperatureValue)));
        assertThat(expectedDeliveryDate.getValueDatetime(), is(equalTo(expectedDeliveryDateValue)));
        assertThat(HIV.getValueAsBoolean(), is(equalTo(hivValue)));
        assertThat(conceptObs.getValueCoded(), is(equalTo(openMrsConcept)));
    }

    @Test
    public void shouldThrowExceptionIfInvalidArgumentIsSet() {
        try {
            observationAdapter.writeValueToOpenMRSObservation(new Object(), new Obs());
            Assert.fail("should throw exception");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void shouldSaveObservation() {
        OpenMRSObservationAdapter observationAdapterSpy = Mockito.spy(observationAdapter);
        MRSObservation mrsObservation = mock(MRSObservation.class);
        MRSObservation savedMRSObservation = mock(MRSObservation.class);
        Obs openMRSObservation = Mockito.mock(Obs.class);
        Obs savedOpenMRSObservation = Mockito.mock(Obs.class);

        Encounter encounter = new Encounter();
        Patient patient = new Patient();
        User creator = new User();
        Location facility = new Location();

        doReturn(openMRSObservation).when(observationAdapterSpy).createOpenMRSObservationForEncounter(mrsObservation, encounter, patient, facility, creator);
        when(mockObservationService.saveObs(openMRSObservation, null)).thenReturn(savedOpenMRSObservation);
        doReturn(savedMRSObservation).when(observationAdapterSpy).convertOpenMRSToMRSObservation(savedOpenMRSObservation);

        MRSObservation returnedMRSObservation = observationAdapterSpy.saveObservation(mrsObservation, encounter, patient, facility, creator);
        Assert.assertThat(returnedMRSObservation, Matchers.is(equalTo(savedMRSObservation)));
    }

    private void assertOpenMrsObservationProperties(Obs openMrsObservation, MRSObservation mrsObservation, Patient patient,
                                                    Location facility, Encounter encounter, User creator, Concept concept) {
        assertThat(openMrsObservation.getObsDatetime(), is(equalTo(mrsObservation.getDate())));
        assertThat(openMrsObservation.getConcept(), is(equalTo(concept)));
        assertThat(openMrsObservation.getPerson(), is(equalTo((Person) patient)));
        assertThat(openMrsObservation.getLocation(), is(equalTo(facility)));
        assertThat(openMrsObservation.getEncounter(), is(equalTo(encounter)));
        assertThat(openMrsObservation.getCreator(), is(equalTo(creator)));
    }

    @Test
    public void shouldFindObservation() {
        OpenMRSObservationAdapter spyOpenMrsObservationAdapter = spy(observationAdapter);

        String patientMotechId = "234";
        String concept = "conceptName";
        Patient openMRSpatient = new Patient();
        Concept openMRSConcept = new Concept();
        final Obs mockObs = mock(Obs.class);
        MRSObservation mockMrsObs = mock(MRSObservation.class);
        ArrayList<Obs> obsList = new ArrayList<Obs>(){{
            add(mockObs);
        }};

        when(mockOpenMRSPatientAdapter.getOpenmrsPatientByMotechId(patientMotechId)).thenReturn(openMRSpatient);
        when(mockConceptAdapter.getConceptByName(concept)).thenReturn(openMRSConcept);
        when(mockObservationService.getObservationsByPersonAndConcept(openMRSpatient, openMRSConcept)).thenReturn(obsList);
        doReturn(mockMrsObs).when(spyOpenMrsObservationAdapter).convertOpenMRSToMRSObservation(mockObs);

        spyOpenMrsObservationAdapter.findObservation(patientMotechId, concept);
        verify(spyOpenMrsObservationAdapter).convertOpenMRSToMRSObservation(mockObs);
    }

    @Test
    public void shouldVoidObservation() throws ObservationNotFoundException {
        String observationId = "34";
        String mrsUserId="userId";
        MRSObservation mrsObservation = new MRSObservation(observationId, new Date(), "name", Integer.valueOf("34"));
        Obs expectedOpenmRSObs = new Obs();
        expectedOpenmRSObs.setVoided(false);
        String reason = "reason";
        User user = new User(12);

        when(mockObservationService.getObs(Integer.valueOf(observationId))).thenReturn(expectedOpenmRSObs);
        when(mockOpenMRSUserAdapter.getOpenMrsUserByUserName(mrsUserId)).thenReturn(user);

        observationAdapter.voidObservation(mrsObservation, reason, mrsUserId);

        ArgumentCaptor<Obs> captor = ArgumentCaptor.forClass(Obs.class);
        verify(mockObservationService).voidObs(captor.capture(), eq(reason));
        Obs actualOpenMRSObservation = captor.getValue();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        assertThat(actualOpenMRSObservation.getVoided(), is(true));
        assertThat(actualOpenMRSObservation.getVoidReason(), is(reason));
        assertThat(actualOpenMRSObservation.getVoidedBy(), is(user));
        assertThat(format.format(actualOpenMRSObservation.getDateVoided()), is(format.format(Calendar.getInstance().getTime())));
    }

    @Test
    public void shouldReturnAllObservationsGivenPatientIdAndConceptName(){
        OpenMRSObservationAdapter spyOpenMrsObservationAdapter = spy(observationAdapter);

        String conceptName = "conceptName";
        String patientMotechId = "patientMotechId";
         Patient openMRSpatient = new Patient();
        Concept openMRSConcept = new Concept();
        final Obs mockObs1 = mock(Obs.class);
        final Obs mockObs2 = mock(Obs.class);
        final MRSObservation mockMrsObs1 = mock(MRSObservation.class);
        final MRSObservation mockMrsObs2 = mock(MRSObservation.class);
        List<Obs> obsList = new ArrayList<Obs>(){{
            add(mockObs1);
            add(mockObs2);
        }};

        List<MRSObservation> expectedMRSObservations = new ArrayList<MRSObservation>(){{
            add(mockMrsObs1);
            add(mockMrsObs2);
        }};

        when(mockOpenMRSPatientAdapter.getOpenmrsPatientByMotechId(patientMotechId)).thenReturn(openMRSpatient);
        when(mockConceptAdapter.getConceptByName(conceptName)).thenReturn(openMRSConcept);
        when(mockObservationService.getObservationsByPersonAndConcept(openMRSpatient, openMRSConcept)).thenReturn(obsList);
        doReturn(mockMrsObs1).when(spyOpenMrsObservationAdapter).convertOpenMRSToMRSObservation(mockObs1);
        doReturn(mockMrsObs2).when(spyOpenMrsObservationAdapter).convertOpenMRSToMRSObservation(mockObs2);

        List<MRSObservation> actualObservations = spyOpenMrsObservationAdapter.findObservations(patientMotechId, conceptName);
        ArgumentCaptor<Obs> obsArgumentCaptor=ArgumentCaptor.forClass(Obs.class);
        verify(spyOpenMrsObservationAdapter,times(2)).convertOpenMRSToMRSObservation(obsArgumentCaptor.capture());

        assertThat(obsArgumentCaptor.getAllValues(),is(obsList));
        assertThat(actualObservations,is(expectedMRSObservations));

    }
}
