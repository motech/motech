package org.motechproject.server.verboice.domain;

public class Gather extends AggregatedVerboiceAction {
    private static final int DEFAULT_TIMEOUT = 5; // default 5 seconds
    private static final String METHOD = "POST"; //verboice doesnt pass digits pressed in GET call

    private String action;
    private int timeout = DEFAULT_TIMEOUT;
    private char finishOnKey = '#'; // default '#'
    private int numDigits;

    public Gather(String action, int numDigits) {
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
                , action, numDigits, finishOnKey, timeout, METHOD));
        buffer.append(super.toXMLString());
        buffer.append("</Gather>");
        return buffer.toString();
    }
}
