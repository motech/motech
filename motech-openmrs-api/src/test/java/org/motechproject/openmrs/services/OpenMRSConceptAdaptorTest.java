package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class OpenMRSConceptAdaptorTest {

    @Mock
    ConceptService conceptService;

    OpenMRSConceptAdaptor adaptor;

    @Before
    public void setUp() {
        initMocks(this);
        adaptor = new OpenMRSConceptAdaptor();
        setField(adaptor, "conceptService", conceptService);
    }
    
    @Test
    public void shouldFetchConceptByName() {

        String concept = "concept1";
        Concept openMRSConcept = mock(Concept.class);
        when(conceptService.getConcept(concept)).thenReturn(openMRSConcept);
        assertThat(adaptor.getConceptByName(concept), is(openMRSConcept));
    }
}
