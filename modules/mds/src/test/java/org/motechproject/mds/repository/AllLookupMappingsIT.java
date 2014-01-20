package org.motechproject.mds.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.constants.Constants;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.LookupMapping;
import org.motechproject.mds.dto.LookupDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.PersistenceManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.constants.Constants.Packages;

public class AllLookupMappingsIT extends BaseIT {
    private static final String SAMPLE_CLASS = String.format("%s.Sample", Packages.ENTITY);
    private static final String SAMPLE_LOOKUP = "testLookup";

    @Autowired
    private AllLookupMappings allLookupMappings;

    @Before
    public void setUp() throws Exception {
        PersistenceManager persistenceManager = getPersistenceManager();
        persistenceManager.makePersistent(new EntityMapping(SAMPLE_CLASS));
    }

    @After
    public void tearDown() throws Exception {
        getPersistenceManager().deletePersistentAll(getLookupMappings());
        getPersistenceManager().deletePersistentAll(getEntityMappings());
    }

    @Test
    public void shouldSaveLookup() throws Exception {
        assertFalse(getEntityMappings().isEmpty());
        EntityMapping em = getEntityMappings().get(0);

        allLookupMappings.save(new LookupDto(SAMPLE_LOOKUP, true), em);

        assertTrue(containsLookup(SAMPLE_LOOKUP));
    }

    @Test
    public void shouldCheckGetLookupByIdMethod() {
        LookupMapping lookup = allLookupMappings.save(new LookupDto(SAMPLE_LOOKUP, true), null);
        assertNotNull(allLookupMappings.getLookupById(lookup.getId()));
        assertNull(allLookupMappings.getLookupById(9999L));
    }
}
