package org.motechproject.server.verboice.domain;

public class VerboiceResponse extends AggregatedVerboiceAction {
    
    String callbackUrl;

    public VerboiceResponse(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    @Override
    public String toXMLString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("<Response>");
        buffer.append(super.toXMLString());
        buffer.append(new Redirect(callbackUrl).toXMLString());
        buffer.append("</Response>");
        return buffer.toString();
    }
}
