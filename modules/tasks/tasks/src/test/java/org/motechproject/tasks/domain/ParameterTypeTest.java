package org.motechproject.tasks.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.motechproject.tasks.domain.ParameterType.TEXTAREA;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;

public class ParameterTypeTest {

    @Test
    public void shouldFindTypeFromString() {
        ParameterType actual = ParameterType.fromString(TEXTAREA.getValue());

        assertNotNull(actual);
        assertEquals(TEXTAREA.getValue(), actual.getValue());
    }

    @Test
    public void shouldNotFindTypeFromWrongOrEmptyString() {
        assertNull(ParameterType.fromString("    "));
        assertNull(ParameterType.fromString(ERROR.getValue()));
    }
}
