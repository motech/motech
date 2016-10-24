package org.motechproject.tasks.domain;

import org.junit.Test;
import org.motechproject.tasks.domain.enums.ParameterType;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.motechproject.tasks.domain.ManipulationTarget.STRING;
import static org.motechproject.tasks.domain.enums.TaskActivityType.ERROR;

public class ManipulationTargetTest {

    @Test
    public void shouldFindTypeFromString() {
        ManipulationTarget actual = ManipulationTarget.fromString(STRING.getValue());

        assertNotNull(actual);
        assertEquals(STRING.getValue(), actual.getValue());
    }

    @Test
    public void shouldNotFindTypeFromWrongOrEmptyString() {
        assertNull(ParameterType.fromString("    "));
        assertNull(ParameterType.fromString(ERROR.getValue()));
    }
}
