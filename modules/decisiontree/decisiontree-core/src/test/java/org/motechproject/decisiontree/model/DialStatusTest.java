package org.motechproject.decisiontree.model;

import org.junit.Test;
import org.motechproject.decisiontree.core.model.DialStatus;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DialStatusTest {
    @Test
    public void shouldCheckValidityOfDialStatus() throws Exception {
        assertTrue(DialStatus.isValid("completed"));
        assertTrue(DialStatus.isValid("no-answer"));
        assertFalse(DialStatus.isValid("someInvalidKey"));
    }
}
