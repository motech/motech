package org.motechproject.mds.helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.repository.internal.AllEntities;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RelationshipResolverTest extends EntitiesTopologyTest {

    @Mock
    private AllEntities allEntities;

    private RelationshipResolver relationshipResolver;

    @Before
    public void setUp() throws Exception {
        relationshipResolver = new RelationshipResolver();
        relationshipResolver.setAllEntities(allEntities);
        when(allEntities.retrieveByClassName(anyString())).thenReturn(null);
    }

    @Test
    public void shouldRemoveUnresolvedEntitiesForSimpleTopology() {
        setupSimpleTopology();

        assertEntities(asSet(entityC), relationshipResolver.removeUnresolvedEntities(asSet(entityA, entityB, entityC)));
        assertEntities(asSet(entityC, entityD), relationshipResolver.removeUnresolvedEntities(asSet(entityD, entityA, entityC)));
    }

    @Test
    public void shouldNotRemoveResolvedEntitiesForSimpleTopology() {
        setupSimpleTopology();

        assertEntities(asSet(entityB, entityC, entityD), relationshipResolver.removeUnresolvedEntities(asSet(entityD, entityB, entityC)));
        assertEntities(asSet(entityA, entityB, entityC, entityD), relationshipResolver.removeUnresolvedEntities(asSet(entityA, entityD, entityB, entityC)));
    }

    @Test
    public void shouldRemoveUnresolvedEntitiesForComplexTopology() {
        setupComplexTopology();

        assertEntities(asSet(entityD, entityE), relationshipResolver.removeUnresolvedEntities(asSet(entityA, entityB, entityC, entityD, entityE)));
        assertEntities(asSet(entityD, entityE, entityF), relationshipResolver.removeUnresolvedEntities(asSet(entityA, entityB, entityD, entityE, entityF)));
        assertEntities(asSet(entityC, entityD, entityE, entityF), relationshipResolver.removeUnresolvedEntities(asSet(entityA, entityC, entityD, entityE, entityF)));
    }

    @Test
    public void shouldNotRemoveResolvedEntitiesForComplexTopology() {
        setupComplexTopology();

        assertEntities(asSet(entityA, entityB, entityC, entityD, entityE, entityF), relationshipResolver.removeUnresolvedEntities(asSet(entityA, entityB, entityC, entityD, entityE, entityF)));
    }

    @Test
    public void shouldRemoveUnresolvedEntitiesForComplexTopologyWithLoop() {
        setupComplexTopologyWithLoop();

        assertEntities(asSet(entityF), relationshipResolver.removeUnresolvedEntities(asSet(entityA, entityB, entityC, entityD, entityF)));
        assertEntities(this.<Entity>asSet(), relationshipResolver.removeUnresolvedEntities(asSet(entityA, entityC, entityE, entityD)));
        assertEntities(this.<Entity>asSet(), relationshipResolver.removeUnresolvedEntities(asSet(entityA, entityC, entityD, entityE, entityB)));
    }

    @Test
    public void shouldNotRemoveResolvedEntitiesForComplexTopologyWithLoop() {
        setupComplexTopologyWithLoop();

        assertEntities(asSet(entityA, entityB, entityC, entityD, entityE, entityF), relationshipResolver.removeUnresolvedEntities(asSet(entityE, entityB, entityF, entityD, entityA, entityC)));
    }

    private void assertEntities(Set<Entity> expected, List<Entity> actual) {
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }
}