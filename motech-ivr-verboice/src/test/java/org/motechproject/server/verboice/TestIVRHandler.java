package org.motechproject.server.verboice;

import org.motechproject.server.verboice.domain.VerboiceHandler;

import java.util.Map;

public class TestIVRHandler implements VerboiceHandler{
    @Override
    public String handle(Map<String, String> parameters) {
        return "verboice response xml";
    }
}
