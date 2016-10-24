package org.motechproject.tasks.domain;

import org.junit.Test;
import org.motechproject.tasks.domain.enums.ParameterType;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.motechproject.tasks.domain.ManipulationType.TOLOWER;
import static org.motechproject.tasks.domain.enums.TaskActivityType.ERROR;

public class ManipulationTypeTest {

    @Test
    public void shouldFindTypeFromString() {
        ManipulationType actual = ManipulationType.fromString(TOLOWER.getValue());

        assertNotNull(actual);
        assertEquals(TOLOWER.getValue(), actual.getValue());
    }

    @Test
    public void shouldNotFindTypeFromWrongOrEmptyString() {
        assertNull(ParameterType.fromString("    "));
        assertNull(ParameterType.fromString(ERROR.getValue()));
    }
}
