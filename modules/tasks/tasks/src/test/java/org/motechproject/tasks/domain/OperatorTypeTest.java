package org.motechproject.tasks.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.tasks.domain.OperatorType.AFTER;
import static org.motechproject.tasks.domain.OperatorType.AFTER_NOW;
import static org.motechproject.tasks.domain.OperatorType.BEFORE;
import static org.motechproject.tasks.domain.OperatorType.BEFORE_NOW;
import static org.motechproject.tasks.domain.OperatorType.CONTAINS;
import static org.motechproject.tasks.domain.OperatorType.ENDSWITH;
import static org.motechproject.tasks.domain.OperatorType.EQUALS;
import static org.motechproject.tasks.domain.OperatorType.EXIST;
import static org.motechproject.tasks.domain.OperatorType.GT;
import static org.motechproject.tasks.domain.OperatorType.LESS_DAYS_FROM_NOW;
import static org.motechproject.tasks.domain.OperatorType.LT;
import static org.motechproject.tasks.domain.OperatorType.MORE_DAYS_FROM_NOW;
import static org.motechproject.tasks.domain.OperatorType.STARTSWITH;
import static org.motechproject.tasks.domain.OperatorType.fromString;
import static org.motechproject.tasks.domain.OperatorType.needExpression;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;

public class OperatorTypeTest {

    @Test
    public void shouldFindTypeFromString() {
        OperatorType actual = fromString(EXIST.getValue());

        assertNotNull(actual);
        assertEquals(EXIST.getValue(), actual.getValue());
    }

    @Test
    public void shouldNotFindTypeFromWrongOrEmptyString() {
        assertNull(fromString("    "));
        assertNull(fromString(ERROR.getValue()));
    }

    @Test
    public void shouldNeedExpression() {
        assertTrue(needExpression(CONTAINS.getValue()));
        assertTrue(needExpression(EQUALS.getValue()));
        assertTrue(needExpression(STARTSWITH.getValue()));
        assertTrue(needExpression(ENDSWITH.getValue()));
        assertTrue(needExpression(GT.getValue()));
        assertTrue(needExpression(LT.getValue()));
        assertTrue(needExpression(AFTER.getValue()));
        assertTrue(needExpression(BEFORE.getValue()));
        assertTrue(needExpression(LESS_DAYS_FROM_NOW.getValue()));
        assertTrue(needExpression(MORE_DAYS_FROM_NOW.getValue()));
    }

    @Test
    public void shouldNotNeedExpression() {
        assertFalse(needExpression(EXIST.getValue()));
        assertFalse(needExpression(AFTER_NOW.getValue()));
        assertFalse(needExpression(BEFORE_NOW.getValue()));
    }
}
