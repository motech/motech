package org.motechproject.mds.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.util.Constants.Packages;

public class AllEntitiesIT extends BaseIT {
    private static final String SAMPLE_CLASS = String.format("%s.Sample", Packages.ENTITY);
    private static final String EXAMPLE_CLASS = String.format("%s.Example", Packages.ENTITY);
    private static final String FOO_CLASS = String.format("%s.Foo", Packages.ENTITY);
    private static final String BAR_CLASS = String.format("%s.Bar", Packages.ENTITY);

    private static final String SAMPLE_LOOKUP = "SampleLookup";

    private static final String EXAMPLE_CLASS_WITH_LOOKUPS = String.format("%s.ExampleWithLookups", Packages.ENTITY);
    private static final String EXAMPLE_LOOKUP_1 = "ExampleLookup1";
    private static final String EXAMPLE_LOOKUP_2 = "ExampleLookup2";

    @Autowired
    private AllEntities allEntities;

    @Before
    public void setUp() throws Exception {
        clearDB();
        PersistenceManager persistenceManager = getPersistenceManager();
        persistenceManager.makePersistent(new Entity(SAMPLE_CLASS));
        persistenceManager.makePersistent(new Entity(EXAMPLE_CLASS));
        persistenceManager.makePersistent(new Entity(FOO_CLASS));

        Entity entityWithLookups = new Entity(EXAMPLE_CLASS_WITH_LOOKUPS);
        List<Lookup> lookups = new LinkedList<>();
        lookups.add(new Lookup(EXAMPLE_LOOKUP_1, true, false, entityWithLookups));
        lookups.add(new Lookup(EXAMPLE_LOOKUP_2, true, false, entityWithLookups));
        entityWithLookups.setLookups(lookups);
        persistenceManager.makePersistent(entityWithLookups);
    }

    @After
    public void tearDown() throws Exception {
        clearDB();
    }

    @Test
    public void shouldSaveEntity() throws Exception {
        assertFalse(
                String.format("Found %s in database", BAR_CLASS),
                containsEntity(BAR_CLASS)
        );

        EntityDto entity = new EntityDto();
        entity.setClassName(BAR_CLASS);

        allEntities.create(entity);

        assertTrue(
                String.format("Not found %s in database", BAR_CLASS),
                containsEntity(BAR_CLASS)
        );
    }

    @Test
    public void shouldFindExistingEntity() throws Exception {
        for (String className : Arrays.asList(SAMPLE_CLASS, EXAMPLE_CLASS, FOO_CLASS)) {
            assertTrue(
                    String.format("Not found %s in database", className),
                    allEntities.contains(className)
            );
        }
    }

    @Test
    public void shouldNotFindExistingEntity() throws Exception {
        assertFalse(
                String.format("Found %s in database", BAR_CLASS),
                allEntities.contains(BAR_CLASS)
        );
    }

    @Test
    public void shouldDeleteEntity() throws Exception {
        EntityDto entity = new EntityDto();
        entity.setClassName(BAR_CLASS);

        allEntities.create(entity);

        assertTrue(
                String.format("Not found %s in database", BAR_CLASS),
                allEntities.contains(BAR_CLASS)
        );

        Query query = getPersistenceManager().newQuery(Entity.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        Entity found = (Entity) query.execute(BAR_CLASS);

        allEntities.delete(found.getId());

        assertFalse(
                String.format("Found %s in database", BAR_CLASS),
                allEntities.contains(BAR_CLASS)
        );
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenDeletingNotExistingEntity() throws Exception {
        allEntities.delete(1000L);
    }

    @Test
    public void shouldCascadeSaveLookup() throws Exception {
        Lookup lookup = new Lookup(SAMPLE_LOOKUP, true, false);
        Entity entity = getEntities().get(0);
        List<Lookup> lookupSet = new LinkedList<>();
        lookupSet.add(lookup);
        entity.setLookups(lookupSet);

        int indexOfLookup = getLookups().indexOf(lookup);
        assertTrue(String.format("'%s' not found in database", SAMPLE_LOOKUP), indexOfLookup >= 0);
        assertEquals("Lookup was not associated with an entity",
                entity,
                getLookups().get(indexOfLookup).getEntity());
    }

    @Test
    public void shouldCascadeDeleteLookups() {
        Query query = getPersistenceManager().newQuery(Entity.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        Entity found = (Entity) query.execute(EXAMPLE_CLASS_WITH_LOOKUPS);
        List<Lookup> lookups = new ArrayList<>(found.getLookups());

        allEntities.delete(found.getId());

        assertFalse("Lookup was not deleted", getLookups().contains(lookups.get(0)));
        assertFalse("Lookup was not deleted", getLookups().contains(lookups.get(1)));
    }

    @Test
    public void shouldUpdateLookup() throws Exception {
        Query query = getPersistenceManager().newQuery(Entity.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        Entity entity = (Entity) query.execute(EXAMPLE_CLASS_WITH_LOOKUPS);

        for (Lookup lookup : entity.getLookups()) {
            if (EXAMPLE_LOOKUP_1.equals(lookup.getLookupName())) {
                lookup.setSingleObjectReturn(false);
            } else if (EXAMPLE_LOOKUP_2.equals(lookup.getLookupName())) {
                lookup.setExposedViaRest(true);
            }
        }

        for (Lookup lookup : getLookups()) {
            if (EXAMPLE_LOOKUP_1.equals(lookup.getLookupName())) {
                assertEquals("Lookup was not updated properly", false, lookup.isSingleObjectReturn());
                assertEquals("Lookup was not updated properly", false, lookup.isExposedViaRest());
            } else if (EXAMPLE_LOOKUP_2.equals(lookup.getLookupName())) {
                assertEquals("Lookup was not updated properly", true, lookup.isSingleObjectReturn());
                assertEquals("Lookup was not updated properly", true, lookup.isExposedViaRest());
            }
        }
    }
}
