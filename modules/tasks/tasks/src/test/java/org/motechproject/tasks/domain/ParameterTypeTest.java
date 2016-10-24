package org.motechproject.tasks.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.tasks.domain.enums.ParameterType;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.motechproject.tasks.domain.enums.ParameterType.TEXTAREA;
import static org.motechproject.tasks.domain.enums.TaskActivityType.ERROR;

public class
    ParameterTypeTest {

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

    @Test
    public void testConvertTo() {
        DateTime now = DateTimeSourceUtil.now().withSecondOfMinute(0).withMillis(0);

        assertEquals("text", ParameterType.UNICODE.parse("text"));
        assertEquals("text\nline2", ParameterType.TEXTAREA.parse("text\nline2"));
        assertEquals(123, ParameterType.INTEGER.parse("123"));
        assertEquals(100000000000L, ParameterType.LONG.parse("100000000000"));
        assertEquals(123.45, ParameterType.DOUBLE.parse("123.45"));
        assertEquals(now, ParameterType.DATE.parse(now.toString("yyyy-MM-dd HH:mm Z")));
        assertEquals(true, ParameterType.BOOLEAN.parse("true"));
        assertEquals(asList("value", "value2"), ParameterType.LIST.parse("value\nvalue2"));

        assertTime(now, (DateTime) ParameterType.TIME.parse(now.toString("HH:mm Z")));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionIfMapIsConverted() {
        ParameterType.MAP.parse("key:value");
    }
    private void assertTime(DateTime expected, DateTime actual) {
        assertEquals(expected.getHourOfDay(), actual.getHourOfDay());
        assertEquals(expected.getMinuteOfHour(), actual.getMinuteOfHour());
        assertEquals(expected.getZone(), actual.getZone());
    }

}
