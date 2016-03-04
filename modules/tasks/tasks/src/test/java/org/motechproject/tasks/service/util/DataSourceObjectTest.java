package org.motechproject.tasks.service.util;

import org.junit.Test;
import org.motechproject.tasks.service.util.DataSourceObject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataSourceObjectTest {

    @Test
    public void shouldEquate() {
        assertTrue(new DataSourceObject("foo", null, true).equals(new DataSourceObject("foo", null, false)));
        assertFalse(new DataSourceObject("foo", null, true).equals(new DataSourceObject("goo", null, true)));
    }
}
