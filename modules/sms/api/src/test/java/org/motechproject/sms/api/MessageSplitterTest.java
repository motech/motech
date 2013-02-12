package org.motechproject.sms.api;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

public class MessageSplitterTest {

    private MessageSplitter messageSplitter;

    @Before
    public void setup() {
        messageSplitter = new MessageSplitter();
    }

    @Test
    public void shouldNotSplitMessageIfLessThanUnitLength() {
        List<String> parts = messageSplitter.split("very short message", 20, "(%d/%d):", "..");
        assertEquals(asList("very short message"), parts);
    }

    @Test
    public void shouldSplitMessageInto2Parts() {
        List<String> parts = messageSplitter.split("this message is longer than 30 characters.", 30, "(%d/%d):", "..");
        assertEquals(asList("(1/2):this message is longer..", "(2/2): than 30 characters."), parts);
    }

    @Test
    public void shouldSplitMessageInto3Parts() {
        List<String> parts = messageSplitter.split("this message should be split into six parts.", 16, "(%d/%d):", "..");
        assertEquals(asList("(1/6):this mes..", "(2/6):sage sho..", "(3/6):uld be s..", "(4/6):plit int..", "(5/6):o six pa..", "(6/6):rts."), parts);
    }

    @Test
    public void shouldNotSplitMessageIfSplitSizeIsLessThanEqualToZero() {
        List<String> parts = messageSplitter.split("this message should not be split into any parts.", 0, "(%d/%d):", "..");
        assertEquals(asList("this message should not be split into any parts."), parts);
    }
}
