package org.motechproject.server.verboice.domain;

import java.util.Map;

@Deprecated
public class GatherAction extends VerboiceAction {

    private String digits;

    public GatherAction(Map<String, String> attributes) {
        super(attributes);
        digits = attributes.get("Digits");
    }

    public String getDigits() {
        return digits;
    }
}
