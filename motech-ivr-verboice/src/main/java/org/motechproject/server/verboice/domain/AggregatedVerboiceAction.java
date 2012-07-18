package org.motechproject.server.verboice.domain;

import java.util.LinkedList;
import java.util.List;

@Deprecated
public abstract class AggregatedVerboiceAction implements VerboiceVerb {

    private List<VerboiceVerb> verboiceVerbs = new LinkedList<VerboiceVerb>();
    
    @Override
    public String toXMLString() {
        final StringBuffer buffer = new StringBuffer();
        for (VerboiceVerb verboiceVerb : verboiceVerbs) {
            buffer.append(verboiceVerb.toXMLString());
        }
        return buffer.toString();
    }


    public void playUrl(String url) {
        verboiceVerbs.add(new Play(url));
    }

    public void playInLoopUrl(String url, int loopCount) {
        verboiceVerbs.add(new Play(url, loopCount));
    }

    public void say(String text) {
        verboiceVerbs.add(new Say(text));
    }

    public void say(String text, String voice, int loop) {
        verboiceVerbs.add(new Say(text, voice, loop));
    }

    public void gather(String action, int numDigits, char finishOnKey, int timeout) {
        verboiceVerbs.add(new Gather(action, numDigits, finishOnKey, timeout));
    }

    public void redirect(String url) {
        verboiceVerbs.add(new Redirect(url));
    }
    public void hangup() {
        verboiceVerbs.add(new Hangup());
    }
}
