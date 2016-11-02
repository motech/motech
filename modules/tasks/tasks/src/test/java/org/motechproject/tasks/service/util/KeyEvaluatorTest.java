package org.motechproject.tasks.service.util;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.tasks.service.util.KeyEvaluator;

import static junit.framework.Assert.assertEquals;

public class KeyEvaluatorTest {

    @Test
    public void shouldProperlyApplyManipulations() throws Exception {
        String string = "ala-has-a-cat";
        DateTime now = DateUtil.now();
        String toString = now.toString();
        String toStringWithPattern = now.toString("yyyy-MM-dd");
        KeyEvaluator keyEvaluator = new KeyEvaluator(null);

        String pastDate = "2015-05-15";
        String timeZone = new DateTime(pastDate).toString("Z"); //Figure out the correct time zone for the given date and locale

        assertEquals("lower_case", keyEvaluator.manipulate("tolower", "LOWER_CASE"));
        assertEquals("UPPER_CASE", keyEvaluator.manipulate("toupper", "upper_case"));
        assertEquals("Capitalize", keyEvaluator.manipulate("capitalize", "capitalize"));
        assertEquals("My+sample+message", keyEvaluator.manipulate("urlencode", "My sample message"));
        assertEquals("37%2365%4078%2490", keyEvaluator.manipulate("URLEncode", "37#65@78$90"));
        assertEquals("67890", keyEvaluator.manipulate("substring(5)", "1234567890"));
        assertEquals("67", keyEvaluator.manipulate("substring(5,7)", "1234567890"));
        assertEquals(string, keyEvaluator.manipulate("join(-)", "ala has a cat"));
        assertEquals("ala", keyEvaluator.manipulate("split(-,0)", string));
        assertEquals("cat", keyEvaluator.manipulate("split(-,3)", string));
        assertEquals(pastDate + " 11:32 " + timeZone, keyEvaluator.manipulate("parseDate(yyyy/dd/MM hh:mm)", "2015/15/05 11:32"));
        assertEquals(toStringWithPattern, keyEvaluator.manipulate("datetime(yyyy-MM-dd)", toString));
        assertEquals(now.dayOfMonth().withMinimumValue().withTime(0, 0, 0, 0).toString(), keyEvaluator.manipulate("beginningOfMonth", toString));
        assertEquals(now.dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999).toString(), keyEvaluator.manipulate("endOfMonth", toString));
        assertEquals(now.plusMonths(1).toString(), keyEvaluator.manipulate("plusMonths(1)", toString));
        assertEquals(now.minusMonths(1).toString(), keyEvaluator.manipulate("minusMonths(1)", toString));
        assertEquals(now.plusDays(1).toString(), keyEvaluator.manipulate("plusDays(1)", toString));
        assertEquals(now.minusDays(1).toString(), keyEvaluator.manipulate("minusDays(1)", toString));
        assertEquals(now.plusHours(2).toString(), keyEvaluator.manipulate("plusHours(2)", toString));
        assertEquals(now.minusHours(2).toString(), keyEvaluator.manipulate("minusHours(2)", toString));
        assertEquals(now.plusMinutes(20).toString(), keyEvaluator.manipulate("plusMinutes(20)", toString));
        assertEquals(now.minusMinutes(20).toString(), keyEvaluator.manipulate("minusMinutes(20)", toString));
    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionWhenManipulationIsUnknown(){
        new KeyEvaluator(null).manipulate("undefined", "something");
    }
}
