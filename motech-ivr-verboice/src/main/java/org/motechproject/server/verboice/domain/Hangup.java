package org.motechproject.server.verboice.domain;

public class Hangup implements VerboiceAction {

    @Override
    public String toXMLString() {
        return "<Hangup/>";
    }
}
