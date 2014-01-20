package org.motechproject.mds.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.constants.Constants;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.constants.Constants.Packages;

public class AllEntityMappingsIT extends BaseIT {
    private static final String SAMPLE_CLASS = String.format("%s.Sample", Packages.ENTITY);
    private static final String EXAMPLE_CLASS = String.format("%s.Example", Packages.ENTITY);
    private static final String FOO_CLASS = String.format("%s.Foo", Packages.ENTITY);
    private static final String BAR_CLASS = String.format("%s.Bar", Packages.ENTITY);

    @Autowired
    private AllEntityMappings allEntityMappings;

    @Before
    public void setUp() throws Exception {
        PersistenceManager persistenceManager = getPersistenceManager();
        persistenceManager.makePersistent(new EntityMapping(SAMPLE_CLASS));
        persistenceManager.makePersistent(new EntityMapping(EXAMPLE_CLASS));
        persistenceManager.makePersistent(new EntityMapping(FOO_CLASS));
    }

    @After
    public void tearDown() throws Exception {
        getPersistenceManager().deletePersistentAll(getEntityMappings());
    }

    @Test
    public void shouldSaveEntity() throws Exception {
        assertFalse(
                String.format("Found %s in database", BAR_CLASS),
                containsEntity(BAR_CLASS)
        );

        allEntityMappings.save(BAR_CLASS);

        assertTrue(
                String.format("Not found %s in database", BAR_CLASS),
                containsEntity(BAR_CLASS)
        );
    }

    @Test
    public void shouldFindExistingEntity() throws Exception {
        for (String className : Arrays.asList(SAMPLE_CLASS, EXAMPLE_CLASS, FOO_CLASS)) {
            String simpleName = className.substring(className.lastIndexOf('.') + 1);

            assertTrue(
                    String.format("Not found %s in database", className),
                    allEntityMappings.containsEntity(simpleName)
            );
        }
    }

    @Test
    public void shouldNotFindExistingEntity() throws Exception {
        String simpleName = BAR_CLASS.substring(BAR_CLASS.lastIndexOf('.') + 1);

        assertFalse(
                String.format("Found %s in database", BAR_CLASS),
                allEntityMappings.containsEntity(simpleName)
        );
    }

    @Test
    public void shouldDeleteEntity() throws Exception {
        allEntityMappings.save(BAR_CLASS);
        String simpleName = BAR_CLASS.substring(BAR_CLASS.lastIndexOf('.') + 1);

        assertTrue(
                String.format("Not found %s in database", BAR_CLASS),
                allEntityMappings.containsEntity(simpleName)
        );

        Query query = getPersistenceManager().newQuery(EntityMapping.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        EntityMapping found = (EntityMapping) query.execute(BAR_CLASS);

        allEntityMappings.delete(found.getId());

        assertFalse(
                String.format("Found %s in database", BAR_CLASS),
                allEntityMappings.containsEntity(simpleName)
        );
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenDeletingNotExistingEntity() throws Exception {
        allEntityMappings.delete(1000L);
    }

}
