package org.motechproject.ivr.kookoo.service;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class OutboundResponseParserTest {

    @Test
    public void isErrorShouldBeTrue_whenStatusIsError() throws Exception {
        String responseBody = "<response><status>error</status><message>some big error message</message></response>";
        OutboundResponseParser responseParser = new OutboundResponseParser();

        assertTrue(responseParser.isError(responseBody));
    }

    @Test
    public void isErrorShouldBeFalse_whenStatusIsQueued() throws Exception {
        String responseBody = "<response><status>queued</status><message>123456</message></response>";
        OutboundResponseParser responseParser = new OutboundResponseParser();

        assertFalse(responseParser.isError(responseBody));
    }

    @Test
    public void returnMessage() throws Exception {
        String responseBody = "<response><status>queued</status><message>123456</message></response>";
        OutboundResponseParser responseParser = new OutboundResponseParser();

        assertEquals("123456", responseParser.getMessage(responseBody));
    }

    @Test
    public void returnEmptyWhenMessageIsEmpty() throws Exception {
        String responseBody = "<response><status>queued</status><message></message></response>";
        OutboundResponseParser responseParser = new OutboundResponseParser();

        assertEquals("", responseParser.getMessage(responseBody));
    }
}
