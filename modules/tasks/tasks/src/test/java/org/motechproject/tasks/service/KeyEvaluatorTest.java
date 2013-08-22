package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.date.util.DateUtil;

import static junit.framework.Assert.assertEquals;

public class KeyEvaluatorTest {

    @Test
    public void testManipulate() throws Exception {
        String string = "ala-has-a-cat";
        DateTime now = DateUtil.now();
        String toString = now.toString();
        String toStringWithPattern = now.toString("yyyy-MM-dd");
        KeyEvaluator keyEvaluator = new KeyEvaluator(null);

        assertEquals("lower_case", keyEvaluator.manipulate("tolower", "LOWER_CASE"));
        assertEquals("UPPER_CASE", keyEvaluator.manipulate("toupper", "upper_case"));
        assertEquals("Capitalize", keyEvaluator.manipulate("capitalize", "capitalize"));
        assertEquals("67890", keyEvaluator.manipulate("substring(5)", "1234567890"));
        assertEquals("67", keyEvaluator.manipulate("substring(5,7)", "1234567890"));
        assertEquals(string, keyEvaluator.manipulate("join(-)", "ala has a cat"));
        assertEquals("ala", keyEvaluator.manipulate("split(-,0)", string));
        assertEquals("cat", keyEvaluator.manipulate("split(-,3)", string));
        assertEquals(toStringWithPattern, keyEvaluator.manipulate("datetime(yyyy-MM-dd)", toString));
        assertEquals(now.plusDays(1).toString(), keyEvaluator.manipulate("plusDays(1)", toString));
    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionWhenManipulationIsUnknown(){
        new KeyEvaluator(null).manipulate("undefined", "something");
    }
}
