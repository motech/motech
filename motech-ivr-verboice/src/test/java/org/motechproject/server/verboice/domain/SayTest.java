package org.motechproject.server.verboice.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SayTest {
    @Test
    public void shouldGenerateSayTag() throws Exception {
        Say say = new Say("hello");
        assertEquals("<Say voice=\"man\" loop=\"1\">hello</Say>",say.toXMLString());
    }

    @Test
    public void shouldGenerateSayTagWithGivenVoiceAndLoopCount() throws Exception {
        Say say = new Say("hello","woman",3);
        assertEquals("<Say voice=\"woman\" loop=\"3\">hello</Say>",say.toXMLString());
    }

}
