package org.motechproject.server.verboice.domain;

import java.util.LinkedList;
import java.util.List;

public abstract class AggregatedVerboiceAction implements VerboiceAction {

    List<VerboiceAction> verboiceActions = new LinkedList<VerboiceAction>();
    
    @Override
    public String toXMLString() {
        final StringBuffer buffer = new StringBuffer();
        for (VerboiceAction verboiceAction : verboiceActions) {
            buffer.append(verboiceAction.toXMLString());
        }
        return buffer.toString();
    }


    public void playUrl(String url) {
        verboiceActions.add(new Play(url));
    }

    public void playInLoopUrl(String url, int loopCount) {
        verboiceActions.add(new Play(url, loopCount));
    }

    public void say(String text){
        verboiceActions.add(new Say(text));
    }

    public void say(String text,String voice, int loop){
        verboiceActions.add(new Say(text,voice,loop));
    }
    
    public void redirect(String url) {
        verboiceActions.add(new Redirect(url));
    }
    public void hangup(){
        verboiceActions.add(new Hangup());
    }
}
