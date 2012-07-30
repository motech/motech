package org.motechproject.eventlogging.converter.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.motechproject.scheduler.domain.MotechEvent;

public class DefaultFileToLogConverterTest {

    @Test
    public void testShouldConvertEventToCorrectString() {
        DefaultFileToLogConverter converter = new DefaultFileToLogConverter();

        MotechEvent event = new MotechEvent("org.test");

        MotechEvent secondEvent = new MotechEvent("org.test.subject");
        secondEvent.getParameters().put("testKey1", "value1");

        List<String> stringList = new ArrayList<String>();

        stringList.add("string1");
        stringList.add("string2");
        stringList.add("string3");

        secondEvent.getParameters().put("testList", stringList);

        String eventConvertedString = converter.convertToLog(event);

        String secondEventConvertedString = converter
                .convertToLog(secondEvent);

        assertEquals("EVENT: org.test at TIME: ",
                eventConvertedString.subSequence(0, 25));

        assertEquals("EVENT: org.test.subject at TIME: ",
                secondEventConvertedString.subSequence(0, 33));

        assertTrue(secondEventConvertedString
                .contains("with PARAMETERS: testList/[string1, string2, string3] testKey1/value1"));
    }
}
