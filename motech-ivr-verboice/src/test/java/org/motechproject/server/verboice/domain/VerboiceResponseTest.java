package org.motechproject.server.verboice.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class VerboiceResponseTest {
    final private String listenerURL = "/verboice-ivr";

    @Test
    public void shouldGenerateSimpleResponse() throws Exception {
        VerboiceResponse verboiceResponse = new VerboiceResponse(listenerURL);
        final String url = "http://test";
        final String text = "Welcome";
        verboiceResponse.playUrl(url);
        verboiceResponse.playInLoopUrl(url, 2);
        verboiceResponse.say(text, "woman", 2);
        String expectedOutput = "<Response>"+
                "<Play loop=\"1\">" + url + "</Play>" +
                "<Play loop=\"2\">" + url + "</Play>" +
                "<Say voice=\"woman\" loop=\"2\">" + text + "</Say>" +
                "<Redirect method=\"POST\">" + listenerURL + "</Redirect>" +
                "</Response>";
        assertEquals(expectedOutput, verboiceResponse.toXMLString());
    }

    @Test
    public void shouldNotIncludeRedirectInResponseIfCallbackUrlIsEmpty() throws Exception {
        VerboiceResponse verboiceResponse = new VerboiceResponse();
        final String url = "http://test";
        final String text = "Welcome";
        verboiceResponse.playUrl(url);
        verboiceResponse.playInLoopUrl(url, 2);
        verboiceResponse.say(text, "woman", 2);
        String expectedOutput = "<Response>"+
                "<Play loop=\"1\">" + url + "</Play>" +
                "<Play loop=\"2\">" + url + "</Play>" +
                "<Say voice=\"woman\" loop=\"2\">" + text + "</Say>" +
                "</Response>";
        assertEquals(expectedOutput, verboiceResponse.toXMLString());
    }



}
