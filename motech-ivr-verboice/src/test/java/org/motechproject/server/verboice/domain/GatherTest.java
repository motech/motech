package org.motechproject.server.verboice.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GatherTest {
    @Test
    public void shouldGenerateGatherTag() {
        Gather gather = new Gather("http://test",2,'7',10);
        gather.say("hello");
        gather.playUrl("http://play");
        assertEquals(
                "<Gather action=\"http://test\" numDigits=\"2\" finishOnKey=\"7\" timeout=\"10\" method=\"POST\">"+
                "<Say voice=\"man\" loop=\"1\">hello</Say>"+
                "<Play loop=\"1\">http://play</Play>"+
                "</Gather>",gather.toXMLString());
    }

    @Test
    public void shouldGenerateSimpleGatherTag() {
        Gather gather = new Gather("http://test",2);
        gather.say("hello");
        gather.playUrl("http://play");
        assertEquals(
                "<Gather action=\"http://test\" numDigits=\"2\" finishOnKey=\"#\" timeout=\"5\" method=\"POST\">"+
                "<Say voice=\"man\" loop=\"1\">hello</Say>"+
                "<Play loop=\"1\">http://play</Play>"+
                "</Gather>",gather.toXMLString());
    }
}
