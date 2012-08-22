package org.motechproject.server.verboice;

import org.motechproject.server.verboice.domain.VerboiceHandler;
import org.motechproject.server.verboice.domain.VerboiceResponse;

import java.util.Map;

public class TestIVRHandler implements VerboiceHandler{
    @Override
    public String handle(Map<String, String> parameters) {
        final VerboiceResponse verboiceResponse = new VerboiceResponse();
        verboiceResponse.playUrl("http://example.com/fileHello.wav");
        return verboiceResponse.toXMLString();
    }
}
