package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.openmrs.model.OpenMRSConcept;
import org.motechproject.openmrs.model.OpenMRSConceptName;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class OpenMRSConceptAdapterTest {

    @Mock
    ConceptService conceptService;
    @Mock
    EventRelay eventRelay;

    OpenMRSConceptAdapter mrsConceptAdapter;

    @Before
    public void setUp() {
        initMocks(this);
        mrsConceptAdapter = new OpenMRSConceptAdapter();
        setField(mrsConceptAdapter, "conceptService", conceptService);
        setField(mrsConceptAdapter, "eventRelay", eventRelay);
    }
    
    @Test
    public void shouldFetchConceptByName() {

        String concept = "concept1";
        Concept openMRSConcept = mock(Concept.class);
        when(conceptService.getConcept(concept)).thenReturn(openMRSConcept);
        assertThat(mrsConceptAdapter.getConceptByName(concept), is(openMRSConcept));
    }

    @Test
    public void testSaveConcept() {
        String name = "name";
        String actualName = null;
        OpenMRSConcept mrsConcept = new OpenMRSConcept(new OpenMRSConceptName(name));
        Concept concept = mock(Concept.class);
        when(conceptService.saveConcept(Matchers.<Concept>any())).thenReturn(concept);

        mrsConceptAdapter.saveConcept(mrsConcept);

        ArgumentCaptor<Concept> conceptCaptor = ArgumentCaptor.forClass(Concept.class);
        verify(conceptService).saveConcept(conceptCaptor.capture());
        Concept actualConcept = conceptCaptor.getValue();
        verify(eventRelay).sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_CONCEPT_SUBJECT, EventHelper.conceptParameters(mrsConcept)));
        ConceptName conceptName = actualConcept.getNames().iterator().next();
        assertEquals(name, conceptName.getName());
    }

    static Concept createAConcept(String id, String name){
        Concept concept = new Concept();
        concept.setId(Integer.parseInt(id));
        ConceptName conceptName = new ConceptName();
        conceptName.setName(name);
        concept.addName(conceptName);

        return concept;
    }

    @Test
    public void testGetConcepts() {
        String conceptId = "100";
        String name = "name";

        List<Concept> concepts = Arrays.asList(createAConcept(conceptId, name));
        when(conceptService.getAllConcepts()).thenReturn(concepts);
        List<OpenMRSConcept> returnedConcepts = (List<OpenMRSConcept>) mrsConceptAdapter.getAllConcepts();
        assertEquals(Arrays.asList(new OpenMRSConcept(new OpenMRSConceptName(name))), returnedConcepts);
    }

    @Test
    public void shouldReturnNullIfLocationWasNotFound() {
        String locationId = "1000";
        Mockito.when(conceptService.getConcept(Integer.parseInt(locationId))).thenReturn(null);
        assertThat(mrsConceptAdapter.getConcept(locationId), org.hamcrest.Matchers.is(equalTo(null)));
    }

    @Test
    public void shouldReturnNullIfQueriedWithNullConceptId() {
        assertNull(mrsConceptAdapter.getConcept(null));
    }
}
