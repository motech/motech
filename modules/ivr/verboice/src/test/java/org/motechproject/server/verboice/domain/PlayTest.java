package org.motechproject.server.verboice.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlayTest {
    @Test
    public void shouldGeneratePlayTag() throws Exception {
        final Play play = new Play("http://play", 3);
        assertEquals("<Play loop=\"3\">http://play</Play>", play.toXMLString());
    }

    @Test
    public void shouldGeneratePlayTagWithDefaultLoopCount() throws Exception {
        final Play play = new Play("http://play");
        assertEquals("<Play loop=\"1\">http://play</Play>", play.toXMLString());
    }
}
