package org.motechproject.mds.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.LookupMapping;
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
import static org.motechproject.mds.constants.Constants.Packages;

public class AllEntityMappingsIT extends BaseIT {
    private static final String SAMPLE_CLASS = String.format("%s.Sample", Packages.ENTITY);
    private static final String EXAMPLE_CLASS = String.format("%s.Example", Packages.ENTITY);
    private static final String FOO_CLASS = String.format("%s.Foo", Packages.ENTITY);
    private static final String BAR_CLASS = String.format("%s.Bar", Packages.ENTITY);

    private static final String SAMPLE_LOOKUP = "SampleLookup";

    private static final String EXAMPLE_CLASS_WITH_LOOKUPS = String.format("%s.ExampleWithLookups", Packages.ENTITY);
    private static final String EXAMPLE_LOOKUP_1 = "ExampleLookup1";
    private static final String EXAMPLE_LOOKUP_2 = "ExampleLookup2";

    @Autowired
    private AllEntityMappings allEntityMappings;

    @Before
    public void setUp() throws Exception {
        clearDB();
        PersistenceManager persistenceManager = getPersistenceManager();
        persistenceManager.makePersistent(new EntityMapping(SAMPLE_CLASS));
        persistenceManager.makePersistent(new EntityMapping(EXAMPLE_CLASS));
        persistenceManager.makePersistent(new EntityMapping(FOO_CLASS));

        EntityMapping entityWithLookups = new EntityMapping(EXAMPLE_CLASS_WITH_LOOKUPS);
        List<LookupMapping> lookups = new LinkedList<>();
        lookups.add(new LookupMapping(EXAMPLE_LOOKUP_1, true, false, entityWithLookups));
        lookups.add(new LookupMapping(EXAMPLE_LOOKUP_2, true, false, entityWithLookups));
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

        allEntityMappings.save(entity);

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
                    allEntityMappings.containsEntity(className)
            );
        }
    }

    @Test
    public void shouldNotFindExistingEntity() throws Exception {
        assertFalse(
                String.format("Found %s in database", BAR_CLASS),
                allEntityMappings.containsEntity(BAR_CLASS)
        );
    }

    @Test
    public void shouldDeleteEntity() throws Exception {
        EntityDto entity = new EntityDto();
        entity.setClassName(BAR_CLASS);

        allEntityMappings.save(entity);

        assertTrue(
                String.format("Not found %s in database", BAR_CLASS),
                allEntityMappings.containsEntity(BAR_CLASS)
        );

        Query query = getPersistenceManager().newQuery(EntityMapping.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        EntityMapping found = (EntityMapping) query.execute(BAR_CLASS);

        allEntityMappings.delete(found.getId());

        assertFalse(
                String.format("Found %s in database", BAR_CLASS),
                allEntityMappings.containsEntity(BAR_CLASS)
        );
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenDeletingNotExistingEntity() throws Exception {
        allEntityMappings.delete(1000L);
    }

    @Test
    public void shouldCascadeSaveLookup() throws Exception {
        LookupMapping lookupMapping = new LookupMapping(SAMPLE_LOOKUP, true, false);
        EntityMapping entityMapping = getEntityMappings().get(0);
        List<LookupMapping> lookupMappingSet = new LinkedList<>();
        lookupMappingSet.add(lookupMapping);
        entityMapping.setLookups(lookupMappingSet);

        int indexOfLookup = getLookupMappings().indexOf(lookupMapping);
        assertTrue(String.format("'%s' not found in database", SAMPLE_LOOKUP), indexOfLookup >= 0);
        assertEquals("Lookup was not associated with an entity",
                entityMapping,
                getLookupMappings().get(indexOfLookup).getEntity());
    }

    @Test
    public void shouldCascadeDeleteLookups() {
        Query query = getPersistenceManager().newQuery(EntityMapping.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        EntityMapping found = (EntityMapping) query.execute(EXAMPLE_CLASS_WITH_LOOKUPS);
        List<LookupMapping> lookups = new ArrayList<>(found.getLookups());

        allEntityMappings.delete(found.getId());

        assertFalse("Lookup was not deleted", getLookupMappings().contains(lookups.get(0)));
        assertFalse("Lookup was not deleted", getLookupMappings().contains(lookups.get(1)));
    }

    @Test
    public void shouldUpdateLookup() throws Exception {
        Query query = getPersistenceManager().newQuery(EntityMapping.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        EntityMapping entityMapping = (EntityMapping) query.execute(EXAMPLE_CLASS_WITH_LOOKUPS);

        for (LookupMapping lookupMapping : entityMapping.getLookups()) {
            if (EXAMPLE_LOOKUP_1.equals(lookupMapping.getLookupName())) {
                lookupMapping.setSingleObjectReturn(false);
            } else if (EXAMPLE_LOOKUP_2.equals(lookupMapping.getLookupName())) {
                lookupMapping.setExposedViaRest(true);
            }
        }

        for (LookupMapping lookupMapping : getLookupMappings()) {
            if (EXAMPLE_LOOKUP_1.equals(lookupMapping.getLookupName())) {
                assertEquals("Lookup was not updated properly", false, lookupMapping.isSingleObjectReturn());
                assertEquals("Lookup was not updated properly", false, lookupMapping.isExposedViaRest());
            } else if (EXAMPLE_LOOKUP_2.equals(lookupMapping.getLookupName())) {
                assertEquals("Lookup was not updated properly", true, lookupMapping.isSingleObjectReturn());
                assertEquals("Lookup was not updated properly", true, lookupMapping.isExposedViaRest());
            }
        }
    }
}
