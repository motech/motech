package org.motechproject.tasks.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.motechproject.tasks.domain.EventParamType.TEXTAREA;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;

public class EventParamTypeTest {

    @Test
    public void shouldFindTypeFromString() {
        EventParamType actual = EventParamType.fromString(TEXTAREA.getValue());

        assertNotNull(actual);
        assertEquals(TEXTAREA.getValue(), actual.getValue());
    }

    @Test
    public void shouldNotFindTypeFromWrongOrEmptyString() {
        assertNull(EventParamType.fromString("    "));
        assertNull(EventParamType.fromString(ERROR.getValue()));
    }
}
