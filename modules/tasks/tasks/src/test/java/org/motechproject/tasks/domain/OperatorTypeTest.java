package org.motechproject.tasks.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.motechproject.tasks.domain.OperatorType.EXIST;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;

public class OperatorTypeTest {

    @Test
    public void shouldFindTypeFromString() {
        OperatorType actual = OperatorType.fromString(EXIST.getValue());

        assertNotNull(actual);
        assertEquals(EXIST.getValue(), actual.getValue());
    }

    @Test
    public void shouldNotFindTypeFromWrongOrEmptyString() {
        assertNull(OperatorType.fromString("    "));
        assertNull(OperatorType.fromString(ERROR.getValue()));
    }
}
