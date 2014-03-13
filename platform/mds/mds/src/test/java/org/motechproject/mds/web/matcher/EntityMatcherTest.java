package org.motechproject.mds.web.matcher;

import org.junit.Test;
import org.motechproject.mds.TestData;
import org.motechproject.mds.dto.EntityDto;

import static org.junit.Assert.assertEquals;

public class EntityMatcherTest {

    @Test
    public void shouldMatchAllEntitiesIfTermIsBlank() throws Exception {
        matchEntities("    ", 8, 0);
    }

    @Test
    public void shouldMatchEntitiesByModule() throws Exception {
        matchEntities("OpenMRS", 4, 4);
    }

    @Test
    public void shouldMatchEntitiesByModuleAndNamespace() throws Exception {
        matchEntities("acc, OpenMRS", 2, 6);
    }

    @Test
    public void shouldMatchEntitiesByNameAndModuleAndNamespace() throws Exception {
        matchEntities("acc, OpenMRS, Pat", 1, 7);
    }

    @Test
    public void shouldIgnoredExtraTerms() throws Exception {
        matchEntities("acc, OpenMRS, Pat, ignored", 1, 7);
    }

    private void matchEntities(String term, int matchedCount, int noMatchedCount) {
        EntityMatcher matcher = new EntityMatcher(term);
        int falseCout = 0;
        int trueCount = 0;

        for (EntityDto entity : TestData.getEntities()) {
            if (matcher.evaluate(entity)) {
                ++trueCount;
            } else {
                ++falseCout;
            }
        }

        assertEquals("The number of matched entities is incorrect", matchedCount, trueCount);
        assertEquals("The number of no matched entities is incorrect", noMatchedCount, falseCout);
    }

}
