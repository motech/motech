package org.motechproject.server.verboice.domain;

public class Hangup implements VerboiceVerb {

    @Override
    public String toXMLString() {
        return "<Hangup/>";
    }
}
