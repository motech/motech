package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSRelationshipAdaptorTest {

    OpenMRSRelationshipAdaptor openMRSRelationshipAdaptor;

    @Mock
    PersonService mockPersonService;

    @Before
    public void setUp() {
        initMocks(this);
        openMRSRelationshipAdaptor = new OpenMRSRelationshipAdaptor();
        ReflectionTestUtils.setField(openMRSRelationshipAdaptor, "personService", mockPersonService);
    }

    @Test
    public void shouldCreateAMotherChildRelationship() {
        String motherId = "122";
        String childId = "123";
        Person mother = mock(Person.class);
        Person child = mock(Person.class);
        RelationshipType motherChildRelationshipType = mock(RelationshipType.class);
        when(mockPersonService.getPerson(Integer.valueOf(motherId))).thenReturn(mother);
        when(mockPersonService.getPerson(Integer.valueOf(childId))).thenReturn(child);
        when(mockPersonService.getRelationshipTypeByName(OpenMRSRelationshipAdaptor.PARENT_CHILD_RELATIONSHIP)).thenReturn(motherChildRelationshipType);
        openMRSRelationshipAdaptor.createMotherChildRelationship(motherId, childId);

        final ArgumentCaptor<Relationship> captor = ArgumentCaptor.forClass(Relationship.class);
        verify(mockPersonService).saveRelationship(captor.capture());
        final Relationship expectedRelationship = captor.getValue();

        assertThat(expectedRelationship.getPersonA(), is(mother));
        assertThat(expectedRelationship.getPersonB(), is(child));
        assertThat(expectedRelationship.getRelationshipType(), is(motherChildRelationshipType));
    }

    @Test
    public void shouldUpdateAMotherChildRelationship() {
        String motherId = "122";
        String childId = "123";
        Person newMother = mock(Person.class);
        Person child = mock(Person.class);
        Person oldMother = mock(Person.class);
        RelationshipType motherChildRelationshipType = mock(RelationshipType.class);
        when(mockPersonService.getPerson(Integer.valueOf(motherId))).thenReturn(newMother);
        when(mockPersonService.getPerson(Integer.valueOf(childId))).thenReturn(child);
        when(mockPersonService.getRelationshipTypeByName(OpenMRSRelationshipAdaptor.PARENT_CHILD_RELATIONSHIP)).thenReturn(motherChildRelationshipType);
        when(mockPersonService.getRelationships(null, child, motherChildRelationshipType)).
                thenReturn(Arrays.asList(new Relationship(oldMother, child, motherChildRelationshipType)));
        openMRSRelationshipAdaptor.updateMotherRelationship(motherId, childId);

        final ArgumentCaptor<Relationship> captor = ArgumentCaptor.forClass(Relationship.class);
        verify(mockPersonService).saveRelationship(captor.capture());
        final Relationship expectedRelationship = captor.getValue();

        assertThat(expectedRelationship.getPersonA(), is(newMother));
        assertThat(expectedRelationship.getPersonA(), is(not(equalTo(oldMother))));
        assertThat(expectedRelationship.getPersonB(), is(child));
        assertThat(expectedRelationship.getRelationshipType(), is(motherChildRelationshipType));
    }

    @Test
    public void shouldVoidAMotherChildRelationship() {
        String childId = "123";
        Person child = mock(Person.class);
        Person mother = mock(Person.class);
        RelationshipType motherChildRelationshipType = mock(RelationshipType.class);
        when(mockPersonService.getPerson(Integer.valueOf(childId))).thenReturn(child);
        when(mockPersonService.getRelationshipTypeByName(OpenMRSRelationshipAdaptor.PARENT_CHILD_RELATIONSHIP)).thenReturn(motherChildRelationshipType);
        Relationship relationship = new Relationship(mother, child, motherChildRelationshipType);
        when(mockPersonService.getRelationships(null, child, motherChildRelationshipType)).thenReturn(Arrays.asList(relationship));
        Relationship voidedRelationship = mock(Relationship.class);
        when(mockPersonService.voidRelationship(Matchers.<Relationship>any(), anyString())).thenReturn(voidedRelationship);

        openMRSRelationshipAdaptor.voidRelationship(childId);

        verify(mockPersonService).saveRelationship(voidedRelationship);
    }
    
    @Test
    public void shouldGetParentIdIfFound() {
        String childId = "12";
        Person child = mock(Person.class);
        RelationshipType motherChildRelationshipType = mock(RelationshipType.class);
        when(mockPersonService.getPerson(Integer.valueOf(childId))).thenReturn(child);
        when(mockPersonService.getRelationshipTypeByName(OpenMRSRelationshipAdaptor.PARENT_CHILD_RELATIONSHIP)).thenReturn(motherChildRelationshipType);
        openMRSRelationshipAdaptor.getMotherRelationship(childId);
        
        verify(mockPersonService).getRelationships(null, child, motherChildRelationshipType);
    }

    @Test
    public void shouldReturnNullIfNoRelationshipIsFound() {
        String childId = "12";
        Person child = mock(Person.class);
        RelationshipType motherChildRelationshipType = mock(RelationshipType.class);
        when(mockPersonService.getPerson(Integer.valueOf(childId))).thenReturn(child);
        when(mockPersonService.getRelationshipTypeByName(OpenMRSRelationshipAdaptor.PARENT_CHILD_RELATIONSHIP)).thenReturn(motherChildRelationshipType);
        when(mockPersonService.getRelationships(null, child, motherChildRelationshipType)).thenReturn(new ArrayList<Relationship>());
        assertNull(openMRSRelationshipAdaptor.getMotherRelationship(childId));
    }
}
