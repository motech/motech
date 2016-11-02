package org.motechproject.tasks.domain;

import org.junit.Test;
import org.motechproject.tasks.domain.mds.task.OperatorType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.tasks.domain.mds.task.OperatorType.AFTER;
import static org.motechproject.tasks.domain.mds.task.OperatorType.AFTER_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.BEFORE;
import static org.motechproject.tasks.domain.mds.task.OperatorType.BEFORE_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.CONTAINS;
import static org.motechproject.tasks.domain.mds.task.OperatorType.ENDSWITH;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EQUALS;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EQUALS_IGNORE_CASE;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EXIST;
import static org.motechproject.tasks.domain.mds.task.OperatorType.GT;
import static org.motechproject.tasks.domain.mds.task.OperatorType.LESS_DAYS_FROM_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.LESS_MONTHS_FROM_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.LT;
import static org.motechproject.tasks.domain.mds.task.OperatorType.MORE_DAYS_FROM_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.MORE_MONTHS_FROM_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.STARTSWITH;
import static org.motechproject.tasks.domain.mds.task.OperatorType.fromString;
import static org.motechproject.tasks.domain.mds.task.OperatorType.needExpression;
import static org.motechproject.tasks.domain.enums.TaskActivityType.ERROR;

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
        assertTrue(needExpression(EQUALS_IGNORE_CASE.getValue()));
        assertTrue(needExpression(STARTSWITH.getValue()));
        assertTrue(needExpression(ENDSWITH.getValue()));
        assertTrue(needExpression(GT.getValue()));
        assertTrue(needExpression(LT.getValue()));
        assertTrue(needExpression(AFTER.getValue()));
        assertTrue(needExpression(BEFORE.getValue()));
        assertTrue(needExpression(LESS_DAYS_FROM_NOW.getValue()));
        assertTrue(needExpression(MORE_DAYS_FROM_NOW.getValue()));
        assertTrue(needExpression(LESS_MONTHS_FROM_NOW.getValue()));
        assertTrue(needExpression(MORE_MONTHS_FROM_NOW.getValue()));
    }

    @Test
    public void shouldNotNeedExpression() {
        assertFalse(needExpression(EXIST.getValue()));
        assertFalse(needExpression(AFTER_NOW.getValue()));
        assertFalse(needExpression(BEFORE_NOW.getValue()));
    }
}
