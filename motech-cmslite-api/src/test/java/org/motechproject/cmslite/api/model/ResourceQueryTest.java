package org.motechproject.cmslite.api.model;

import org.junit.Test;
import org.motechproject.cmslite.api.model.Resource;
import org.motechproject.cmslite.api.model.ResourceQuery;

import static junit.framework.Assert.assertEquals;

public class ResourceQueryTest {
    @Test
    public void shouldReturnResource() {
        ResourceQuery resourceQuery = new ResourceQuery("name", "language");
        Resource resource = resourceQuery.getResource();

        assertEquals("name", resource.getName());
        assertEquals("language", resource.getLanguage());
    }
}