package org.motechproject.server.service.ivr.astersik;

import org.asteriskjava.live.AsteriskChannel;
import org.asteriskjava.live.LiveException;
import org.asteriskjava.live.OriginateCallback;

/**
 *  Motech specific implementation of the Asterisk-Java call-back interface
 *   see org.asteriskjava.live.OriginateCallback.java for details
 *
 *
 *
 * TODO - implement properly
 */
public class MotechAsteriskCallBackImpl implements OriginateCallback {
    @Override
    public void onDialing(AsteriskChannel asteriskChannel) {
        System.out.println("onDialing " + asteriskChannel);
    }

    @Override
    public void onSuccess(AsteriskChannel asteriskChannel) {
        System.out.println("onSuccess");
    }

    @Override
    public void onNoAnswer(AsteriskChannel asteriskChannel) {
        System.out.println("onNoAnswer");
    }

    @Override
    public void onBusy(AsteriskChannel asteriskChannel) {
        System.out.println("onBusy");
    }

    @Override
    public void onFailure(LiveException e) {
        System.out.println("onFailure");
    }
}
