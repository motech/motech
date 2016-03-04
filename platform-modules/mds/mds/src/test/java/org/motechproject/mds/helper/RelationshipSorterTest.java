package org.motechproject.mds.helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RelationshipSorterTest extends EntitiesTopologyTest {

    private RelationshipSorter relationshipSorter;

    @Before
    public void setUp() throws Exception {
        relationshipSorter = new RelationshipSorter();
    }

    @Test
    public void shouldSortEntitiesForSimpleTopology() throws Exception {
        setupSimpleTopology();
        List<Entity> entities = asList(entityD, entityA, entityC, entityB);
        relationshipSorter.sort(entities);
        assertEntitiesOrder(entities, asSet(
                order(entityC, entityB),
                order(entityD, entityB),
                order(entityB, entityA)
        ));
    }

    @Test
    public void shouldSortEntitiesForComplexTopology() throws Exception {
        setupComplexTopology();
        List<Entity> entities = asList(entityE, entityD, entityA, entityF, entityC, entityB);
        relationshipSorter.sort(entities);
        assertEntitiesOrder(entities, asSet(
                order(entityB, entityA),
                order(entityE, entityA),
                order(entityC, entityB),
                order(entityD, entityB),
                order(entityE, entityC),
                order(entityF, entityC)
        ));
    }

    @Test
    public void shouldSortEntitiesForComplexTopologyWithLoop() throws Exception {
        setupComplexTopologyWithLoop();
        List<Entity> entities = asList(entityE, entityD, entityA, entityF, entityC, entityB);
        relationshipSorter.sort(entities);
        assertEntitiesOrder(entities, asSet(
                order(entityA, entityD),
                order(entityB, entityD),
                order(entityC, entityD),
                order(entityE, entityD),
                order(entityF, entityA),
                order(entityF, entityB),
                order(entityF, entityC),
                order(entityF, entityE)
        ));
    }

    private void assertEntitiesOrder(List<Entity> entities, Set<Order> orders) {
        for (Order order : orders) {
            assertTrue(String.format("%s should be before %s", order.before.getName(), order.after.getName()), entities.indexOf(order.before) < entities.indexOf(order.after));
        }
    }

    private static Order order(Entity before, Entity after) {
        return new Order(before, after);
    }

    private static class Order {
        final Entity before;
        final Entity after;
        public Order(Entity before, Entity after) {
            this.before = before;
            this.after = after;
        }
    }
}