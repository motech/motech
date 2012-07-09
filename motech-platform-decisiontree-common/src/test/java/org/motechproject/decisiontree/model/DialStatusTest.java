package org.motechproject.decisiontree.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DialStatusTest {
    @Test
    public void shouldCheckValidityOfDialStatus() throws Exception {
        assertTrue(DialStatus.isValid("completed"));
        assertFalse(DialStatus.isValid("someInvalidKey"));
        assertTrue(DialStatus.isValid("no-answer"));
    }
}
