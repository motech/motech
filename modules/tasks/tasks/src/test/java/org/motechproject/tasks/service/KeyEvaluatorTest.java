package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.date.util.DateUtil;

import static junit.framework.Assert.assertEquals;

public class KeyEvaluatorTest {

    @Test
    public void shouldProperlyApplyManipulations() throws Exception {
        String string = "ala-has-a-cat";
        DateTime now = DateUtil.now();
        String toString = now.toString();
        String toStringWithPattern = now.toString("yyyy-MM-dd");
        KeyEvaluator keyEvaluator = new KeyEvaluator(null);
        String timeZone = now.toString("Z");

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
        assertEquals("2015-05-15 11:32 " + timeZone, keyEvaluator.manipulate("parseDate(yyyy/dd/MM hh:mm)", "2015/15/05 11:32"));
        assertEquals(toStringWithPattern, keyEvaluator.manipulate("datetime(yyyy-MM-dd)", toString));
        assertEquals(now.plusDays(1).toString(), keyEvaluator.manipulate("plusDays(1)", toString));
    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionWhenManipulationIsUnknown(){
        new KeyEvaluator(null).manipulate("undefined", "something");
    }
}
