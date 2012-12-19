package org.motechproject.tasks.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.motechproject.tasks.domain.EventParamType.TEXTAREA;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;

public class TaskActivityTypeTest {

    @Test
    public void shouldFindTypeFromString() {
        TaskActivityType actual = TaskActivityType.fromString(ERROR.getValue());

        assertNotNull(actual);
        assertEquals(ERROR.getValue(), actual.getValue());
    }

    @Test
    public void shouldNotFindTypeFromWrongOrEmptyString() {
        assertNull(TaskActivityType.fromString("    "));
        assertNull(TaskActivityType.fromString(TEXTAREA.getValue()));
    }
}
