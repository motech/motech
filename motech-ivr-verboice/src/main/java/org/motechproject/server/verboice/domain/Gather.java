package org.motechproject.server.verboice.domain;

public class Gather extends AggregatedVerboiceAction {
    String action;
    final String method = "POST"; //verboice doesnt pass digits pressed in GET call
    int timeout = 5;  // default 5 seconds
    char finishOnKey = '#'; // default '#'
    int numDigits;

    public Gather(String action, int numDigits){
        this.action = action;
        this.numDigits = numDigits;
    }

    public Gather(String action, int numDigits, char finishOnKey, int timeout) {
        this(action, numDigits);
        this.finishOnKey = finishOnKey;
        this.timeout = timeout;
    }

    @Override
    public String toXMLString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(String.format(
                "<Gather action=\"%s\" numDigits=\"%d\" finishOnKey=\"%c\" timeout=\"%d\" method=\"%s\">"
                , action, numDigits, finishOnKey, timeout, method));
        buffer.append(super.toXMLString());
        buffer.append("</Gather>");
        return buffer.toString();
    }
}
